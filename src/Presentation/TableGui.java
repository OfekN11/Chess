package Presentation;

import Business.Place;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.Supplier;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;


/**
 * All This class has been written by the help of Amir650 so please check it out at- https://www.youtube.com/c/amir650.
 */
public class TableGui {
    private final JFrame gameFrame;
    private final Object lock;
    private final BoardPanel boardPanel;
    private BoardDirection boardDirection;
    private boolean highlightLegalMove;
    private Collection<Place> possibleDestinationsForChosenPiece;
    private final static Dimension FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");
    private String boardAsString;
    private final ClickListener clickListener;
    private boolean isVisible;


    public TableGui(ClickListener listener) {
        lock = new byte[1];
        this.clickListener = listener;
        this.gameFrame = new JFrame("Two Player Chess");
        this.gameFrame.setLayout(new BorderLayout());
        JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(FRAME_DIMENSION);
        this.boardPanel = new BoardPanel();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMove = true;
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        possibleDestinationsForChosenPiece = Collections.emptyList();
        this.isVisible = false;
        this.boardAsString = "";
    }

    private JMenuBar createTableMenuBar() {
        JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        final JCheckBoxMenuItem legalMoveHighlighterCheckBox = new JCheckBoxMenuItem("Highlight Legal Moves", true);
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard();
            }
        });

        legalMoveHighlighterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMove = legalMoveHighlighterCheckBox.isSelected();
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();
        preferencesMenu.add(legalMoveHighlighterCheckBox);

        return preferencesMenu;
    }

    public void setPossibleDestinationsForChosenPiece(Collection<Place> possibleDestinationsForChosenPiece){
        synchronized (lock){
            this.possibleDestinationsForChosenPiece = possibleDestinationsForChosenPiece;
        }
    }

    public void reprint(){
        boardPanel.drawBoard();
    }

    public void setBoardAsString(String board){
        this.boardAsString = board;
        if (!isVisible)
            gameFrame.setVisible(true);
    }

    public void reverseTable() {
        boardDirection = boardDirection.opposite();
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }

            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard() {
            synchronized (lock){
                removeAll();
                List<TilePanel> tilePanels = boardDirection.traverse(boardTiles);
                int charPlaceInTheString = boardDirection == BoardDirection.NORMAL ? 0: 63;
                int toAdd = boardDirection == BoardDirection.NORMAL ? 1: -1;
                for (int i = 0; i<boardAsString.length(); charPlaceInTheString += toAdd, i++) {
                    tilePanels.get(i).drawTile(boardAsString.charAt(charPlaceInTheString));
                    add(tilePanels.get(i));
                }
                repaint();
                validate();
            }
           /* for (TilePanel tile :
                    boardDirection.traverse(boardTiles)) {
                tile.drawTile();
                add(tile);
            }
            validate();
            repaint();*/
        }
    }

    private class TilePanel extends JPanel {
        private final int tileId;
        private final Place tilePlace;

        private final String PHOTOS_DIRECTORY_PATH = "C:\\Users\\Ofek\\Desktop\\ofek\\Programing\\SelfProject\\Chess\\Photos\\";

        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            this.tilePlace = getPlaceFromId();
            setPreferredSize(TILE_PANEL_DIMENSION);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (isLeftMouseButton(e)) {
                        Place thisTilePlace = getPlaceFromId();
                        clickListener.listen(thisTilePlace);
                    } else if (isRightMouseButton(e)) {
                        resetVariables();
                    }
                }


                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            validate();
        }


        private Place getPlaceFromId() {
            int row = tileId / 8;
            int column = tileId % 8;
            return Place.getPlace(row, column);
        }

        private void assignTilePieceIcon(char c) {
            this.removeAll();
            String pieceRepresentationString = getStringRepresentationOfPieceInPlace(c);
            if (pieceRepresentationString != null) {
                try {
                    final BufferedImage image = ImageIO.read(new File(PHOTOS_DIRECTORY_PATH + pieceRepresentationString + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public String getStringRepresentationOfPieceInPlace(char c) {
            return switch (c) {
                case 'P' ->  "W" + "Pa";
                case 'p' ->  "B" + "Pa";
                case 'R' -> "W" + "Ro";
                case 'r' -> "B" + "Ro";
                case 'B' -> "W" + "Bi";
                case 'b' -> "B" + "Bi";
                case 'H' -> "W" + "Kn";
                case 'h' -> "B" + "Kn";
                case 'K' -> "W" + "Ki";
                case 'k' -> "B" + "Ki";
                case 'Q' -> "W" + "Qu";
                case 'q' -> "B" + "Qu";
                default -> null;
            };
        }

        private void assignTileColor() {
            if (this.tileId < 8 | (this.tileId >= 16 & this.tileId < 24) | (this.tileId >= 32 & this.tileId < 40) | (this.tileId >= 48 & this.tileId < 56)) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);

        }

        public void drawTile(char c) {
            assignTileColor();
            assignTilePieceIcon(c);
            highlightLegalMoves();
            validate();
            repaint();
        }

        private void highlightLegalMoves() {
            if (highlightLegalMove) { // change it to a user input if he what to highLight legal moves
                if (possibleDestinationsForChosenPiece.contains(this.tilePlace)) {
                    try {
                        add(new JLabel(new ImageIcon(ImageIO.read(new File(PHOTOS_DIRECTORY_PATH + "green_dot.png")))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        private void resetVariables() {
            possibleDestinationsForChosenPiece = Collections.emptyList();
        }
    }


    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },

        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                TilePanel[] output = new TilePanel[64];
                for (int i = 0; i < boardTiles.size(); i++) {
                    output[boardTiles.size() - 1 - i] = boardTiles.get(i);
                }
                return Arrays.stream(output).toList();
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final java.util.List<TilePanel> boardTiles);

        abstract BoardDirection opposite();
    }

}
