package Presentation;

import Business.Boards.TwoPlayerChessBoard;
import Business.ChessPieces.ChessPiece;
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

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class tableGui {
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final TwoPlayerChessBoard board;
    private Place source;
    private Place dest;
    private ChessPiece humanMovedPiece;
    private final static Dimension FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    public tableGui() {
        this.gameFrame = new JFrame("Two Player Chess");
        this.gameFrame.setLayout(new BorderLayout());
        JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(FRAME_DIMENSION);
        this.board= new TwoPlayerChessBoard();
        this.boardPanel = new BoardPanel();

        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
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

        public void drawBoard(TwoPlayerChessBoard board) {
            for (TilePanel tile :
                    boardTiles) {
                tile.drawTile(board);
                add(tile);
            }
            validate();
            repaint();
        }
    }

    private class TilePanel extends JPanel {
        private final int tileId;
        private final Place tilePlace;

        private final String PIECE_ICON_PATH = "C:\\Users\\Ofek\\Desktop\\ofek\\Programing\\SelfProject\\Chess\\Photos\\";
        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            this.tilePlace = getPlaceFromId();
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(board);
            
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if(isLeftMouseButton(e)){
                        // first click
                        if (source == null){
                            source = tilePlace;
                            humanMovedPiece = board.getPieceInPlace(tilePlace);
                            if (humanMovedPiece == null)
                                resetVariables();
                        }
                        else{
                            //second click
                            dest = tilePlace;
                            if (humanMovedPiece.isLegalMove(source,dest,board)){
                                board.moveAPiece(source,dest,()->'Q');
                                System.out.println("yeyy");
                            }
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    boardPanel.drawBoard(board);
                                }
                            });
                            resetVariables();
                        }
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
            int row = tileId/8;
            int column = tileId%8;
            return Place.getPlace(row,column);
        }

        private void assignTilePieceIcon(final TwoPlayerChessBoard board){
            this.removeAll();

            ChessPiece pieceInPlace = board.getPieceInPlace(this.tilePlace);
            if(pieceInPlace!= null){
                String className = pieceInPlace.getClass().toString().substring(pieceInPlace.getClass().toString().lastIndexOf(".")+1,pieceInPlace.getClass().toString().lastIndexOf(".")+3);
                try {
                    final BufferedImage image = ImageIO.read(new File(PIECE_ICON_PATH + pieceInPlace.getColor().toString().substring(0,1) +className+ ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        private void assignTileColor() {
            if (this.tileId < 8 | (this.tileId >= 16 & this.tileId < 24) | (this.tileId >= 32 & this.tileId < 40) | (this.tileId >= 48 & this.tileId < 56)) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);

        }

        public void drawTile(TwoPlayerChessBoard board) {
            assignTileColor();
            assignTilePieceIcon(board);
            validate();
            repaint();
        }
    }

    private void resetVariables() {
        source = null;
        dest = null;
        humanMovedPiece = null;
    }
}
