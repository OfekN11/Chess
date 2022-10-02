package Business.Boards;

import Business.ChessPieces.*;
import Business.Color;
import Business.MessagesLibrary;
import Business.Place;
import Business.Place.Direction;

import java.util.*;
import java.util.function.Supplier;

/**
 * The TwoPlayerChessBoard is a class to represent a normal chess board,of a normal chess game.
 * This class is responsible for all the chess rules
 */
public class TwoPlayerChessBoard {

    // static fields

    private static final Map<Class<? extends ChessPiece>, Set<Direction>> fullRunnerValidMovementDirectionsMap = new HashMap<>(); // contains the valid moving direction for the pieces
    private static final String NORMAL_PIECE_ORDER_STRING = "rhbqkbhr\npppppppp\n\n\n\n\nPPPPPPPP\nRHBQKBHR"; // this string represent the starting order of a normal chess game.


    static {
        fullRunnerValidMovementDirectionsMap.put(Bishop.class, new HashSet<>(Arrays.asList(Direction.RightDownDiagonal, Direction.RightUpDiagonal, Direction.LeftDownDiagonal, Direction.LeftUpDiagonal)));
        fullRunnerValidMovementDirectionsMap.put(Queen.class, new HashSet<>(Arrays.asList(Direction.LeftUpDiagonal, Direction.Up, Direction.RightUpDiagonal, Direction.Right, Direction.RightDownDiagonal, Direction.Down, Direction.LeftDownDiagonal, Direction.Left)));
        fullRunnerValidMovementDirectionsMap.put(Rook.class, new HashSet<>(Arrays.asList(Direction.Right, Direction.Up, Direction.Left, Direction.Down)));
    }

    //enums


    // fields
    private final ChessPiece[][] pieces;
    private Place whiteKingPlace;
    private Place blackKingPlace;


    // Constructors
    public TwoPlayerChessBoard() {
        pieces = new ChessPiece[8][8];
        parseStringToPieces(NORMAL_PIECE_ORDER_STRING);
    }


    public TwoPlayerChessBoard(String stringToParse) {
        pieces = new ChessPiece[8][8];
        parseStringToPieces(stringToParse);
    }

    /**
     * This function initialize the pieces array
     *
     * @param stringToParse a string represent the position of the pieces on the board
     */
    public void parseStringToPieces(String stringToParse) {
        int row = 0, column = 0;
        for (int i = 0; i < stringToParse.length(); i++, column++) {
            if (stringToParse.charAt(i) == '\n') {
                row++;
                column = -1;
                continue;
            } // going threw the next line column will be zero at the next turn

            try {

                validBoardPlace(Place.getPlace(row, column));
            } catch (Exception e) {
                throw new RuntimeException(MessagesLibrary.ILLEGAL_STRING);
            }

            switch (stringToParse.charAt(i)) {
                case 'P' -> pieces[row][column] = new Pawn(Color.White);
                case 'p' -> pieces[row][column] = new Pawn(Color.Black);
                case 'R' -> pieces[row][column] = new Rook(Color.White);
                case 'r' -> pieces[row][column] = new Rook(Color.Black);
                case 'B' -> pieces[row][column] = new Bishop(Color.White);
                case 'b' -> pieces[row][column] = new Bishop(Color.Black);
                case 'H' -> pieces[row][column] = new Knight(Color.White);
                case 'h' -> pieces[row][column] = new Knight(Color.Black);
                case 'K' -> {
                    pieces[row][column] = new King(Color.White);
                    whiteKingPlace = Place.getPlace(row, column);
                }
                case 'k' -> {
                    pieces[row][column] = new King(Color.Black);
                    blackKingPlace = Place.getPlace(row, column);
                }
                case 'Q' -> pieces[row][column] = new Queen(Color.White);
                case 'q' -> pieces[row][column] = new Queen(Color.Black);
                case '-' -> pieces[row][column] = null;

            }
        }
    }


    /**
     * This constructor is meant for testing a board after a move, and if the new board is not a valid one, it will be easy to reverse the changes
     *
     * @param board  a board to copy
     * @param start  The place piece you want to move without conditions
     * @param finish The place you want to move the piece
     */
    private TwoPlayerChessBoard(TwoPlayerChessBoard board, Place start, Place finish) {
        pieces = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getPieceInPlace(i, j) != null)
                    this.pieces[i][j] = board.getPieceInPlace(i, j).clone();
                else
                    this.pieces[i][j] = null;
            }
        }

        this.blackKingPlace = board.blackKingPlace;
        this.whiteKingPlace = board.whiteKingPlace;
        moveAPiece(start, finish, () -> 'Q');
    }

    // Methods

    private void validBoardPlace(Place place) {
        if (place.getRow() > 7 | place.getColumn() < 0 | place.getRow() < 0 | place.getColumn() > 7)
            throw new IllegalArgumentException(MessagesLibrary.INVALID_CHOICE);
    }

    /**
     * design pattern visitor.
     * this function calls piece.isLegalPieceMovement, which returns true if the certain piece is allow to move to this place (by movement rules only)
     */
    public boolean isLegalMove(Place start, Place finish, Color playerColor) {
        validBoardPlace(start);
        validBoardPlace(finish);
        if (getPieceInPlace(start) == null || getPieceInPlace(start).getColor() != playerColor)
            return false;
        ChessPiece piece = getPieceInPlace(start);

        boolean legalMovement = piece.isLegalPieceMove(start, finish, this);
        boolean notCauseASelfCheck = !new TwoPlayerChessBoard(this, start, finish).isKingThreaten(playerColor); // checking that the move is legal for the piece, and that the king is not threaten

        return legalMovement && notCauseASelfCheck;
    }


    public boolean isLegalPieceMovement(Place start, Place finish, Pawn pawn) {
        Direction direction;
        try {
            direction = Place.calculateDirection(start, finish);

        } catch (Exception e) {
            return false;
        }

        int rowDifferent = Place.calculateRowDistance(start, finish);

        switch (direction) {
            // check if the pass is clear
            case Up:
            case Down:

                if (isThereAPieceBetween(start, finish, direction, true))
                    return false;

                switch (rowDifferent) {
                    case 2:
                        if (pawn.hasMoved())
                            return false;
                    case 1:
                        return (pawn.getColor() == Color.White && direction.equals(Direction.Up)) ||
                                (pawn.getColor() == Color.Black && direction.equals(Direction.Down));

                    default:
                        return false;
                }
                // we will check that in "finish" there is enemy and one step away
            case RightUpDiagonal:
            case LeftUpDiagonal:
            case LeftDownDiagonal:
            case RightDownDiagonal:

                ChessPiece enemy = getPieceInPlace(finish);

                Color pawnColor = pawn.getColor();
                return (enemy != null && enemy.getColor() != pawn.getColor() && rowDifferent == 1) && //white and up or black and down
                        ((pawnColor == Color.White && direction == Direction.RightUpDiagonal || pawnColor == Color.White && direction == Direction.LeftUpDiagonal) ||
                                (pawnColor == Color.Black && direction == Direction.RightDownDiagonal || pawnColor == Color.Black && direction == Direction.LeftDownDiagonal));

            default:
                return false;
        }
    }

    public boolean isLegalPieceMovement(Place start, Place finish, Rook rook) {
        return fullRunnerIsLegalPieceMovement(start, finish, fullRunnerValidMovementDirectionsMap.get(Rook.class));
    }


    public boolean isLegalPieceMovement(Place start, Place finish, Knight knight) {
        try {
            boolean landingOnAllies = getPieceInPlace(finish) != null && getPieceInPlace(finish).getColor() == knight.getColor();
            boolean legalDirection = Place.calculateDirection(start, finish) == Direction.Knight;
            return !landingOnAllies && legalDirection;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean isLegalPieceMovement(Place start, Place finish, Bishop piece) {
        return fullRunnerIsLegalPieceMovement(start, finish, fullRunnerValidMovementDirectionsMap.get(Bishop.class));
    }

    public boolean isLegalPieceMovement(Place start, Place finish, Queen piece) {
        return fullRunnerIsLegalPieceMovement(start, finish, fullRunnerValidMovementDirectionsMap.get(Queen.class));

    }


    public boolean isLegalPieceMovement(Place start, Place finish, King king) {
        Direction direction;
        try {
            direction = Place.calculateDirection(start, finish);

        } catch (Exception e) {
            return false;
        }
        Color opponentColor = getOpponentColor(king.getColor());
        int rowDistance = Place.calculateRowDistance(start, finish);
        int columnDistance = Place.calculateColumnDistance(start, finish);
        if (rowDistance > 1 || columnDistance > 2 || direction == Direction.Knight)
            return false;

        // castling
        if (columnDistance == 2) {
            if (king.hasMoved())
                return false;
            ChessPiece rook = direction == Direction.Right ? getPieceInPlace(start.getRow(), 7) : getPieceInPlace(start.getRow(), 0);
            Place startPlusOne = start.move(direction);
            return rook instanceof Rook && !rook.hasMoved() && !isThereAPieceBetween(start, finish, direction, true) && !isPlaceThreatenByAColor(startPlusOne, opponentColor) && !isPlaceThreatenByAColor(finish, opponentColor) && !isPlaceThreatenByAColor(start, opponentColor);
        }
        boolean legalMove = fullRunnerIsLegalPieceMovement(start, finish, fullRunnerValidMovementDirectionsMap.get(Queen.class)) && !isPlaceThreatenByAColor(finish, getOpponentColor(king.getColor())); // we use the queen class because king and queen can move the same direction, and we checked that the king don't move 2 steps
        boolean kingIsInCheckInNewPlace = new TwoPlayerChessBoard(this,start,finish).isKingThreaten(king.getColor());
        return  legalMove && !kingIsInCheckInNewPlace;
    }

    /**
     * Checks if a full runner piece ,who start in "start" can go to "finish" using the "validDirectionsForPieceType"
     * note : there is no checking that the piece in start is not null and a full runner!
     *
     * @param start           piece start place
     * @param finish          piece start place
     * @param validDirections The direction the piece is allows  to move
     * @return true if it's a legal movement
     */
    private boolean fullRunnerIsLegalPieceMovement(Place start, Place finish, Set<Direction> validDirections) {
        Direction direction;
        try {
            direction = Place.calculateDirection(start, finish);

        } catch (Exception e) {
            return false;
        }
        ChessPiece piece = getPieceInPlace(start);
        boolean validateRoute = validDirections.contains(direction) && !isThereAPieceBetween(start, finish, direction, false);
        boolean emptyPlaceOrDifferentColor = getPieceInPlace(finish) == null || getPieceInPlace(finish).getColor() != piece.getColor();
        return validateRoute & emptyPlaceOrDifferentColor;
    }

    /**
     * @param start        The current Place of a piece you want to move
     * @param finish       Where you want to move the piece
     * @param direction    the direction between "start" and "finish", can be calculated by "calculateDirection"
     * @param toIncludeEnd When true the function will check if there is a piece between and start and finish, or in finish, and when false the function will check if there is a piece between and start and finish not care about what in finish
     * @return true if there is a piece between "start" and "finish" not excluding start.
     */
    private boolean isThereAPieceBetween(Place start, Place finish, Direction direction, boolean toIncludeEnd) {
        start = start.move(direction);
        while (!start.equals(finish)) {
            if (getPieceInPlace(start) != null)
                return true;
            else
                start = start.move(direction);
        }

        return toIncludeEnd ? getPieceInPlace(finish) != null : false;
    }

    public ChessPiece getPieceInPlace(Place place) {
        return pieces[place.getRow()][place.getColumn()];
    }

    public ChessPiece getPieceInPlace(int row, int column) {
        return pieces[row][column];
    }


    /**
     * This function move the piece on the board, without validating rules, it replaces the start place with a null
     *
     * @param start                   where the piece at
     * @param finish                  where to move it
     * @param promotionLetterSupplier in case of a promotion, the supplier should give the letter of the piece he wants to promote to
     */
    public void moveAPiece(Place start, Place finish, Supplier<Character> promotionLetterSupplier) {
        ChessPiece piece = getPieceInPlace(start);
        if (piece instanceof King) {
            moveAKing(start, finish, (King) piece);
        }
        if ((piece instanceof Pawn) && isPromotionNeeded(finish, (Pawn) piece))
            piece = getPromotionPiece(piece.getColor(), promotionLetterSupplier);
        piece.moved();
        pieces[finish.getRow()][finish.getColumn()] = piece;
        pieces[start.getRow()][start.getColumn()] = null;
    }

    /**
     * this function checks if a pawn gets to the end of the board
     */
    private boolean isPromotionNeeded(Place finish, Pawn pawn) {
        return pawn.getColor().equals(Color.White) && finish.getRow() == 0 || pawn.getColor().equals(Color.Black) && finish.getRow() == 7;
    }

    /**
     * promote a pawn
     *
     * @param color    the color of the pawn that is being promoted
     * @param supplier a supplier so we will know what kind of piece the player wants to replace the pawn
     * @return The new piece which the player decided to promote the pawn to
     */
    private ChessPiece getPromotionPiece(Color color, Supplier<Character> supplier) {
        ChessPiece piece;
        Character character = supplier.get();
        if (character == 'R' || character == 'r') {
            piece = new Rook(color);
        } else if (character == 'K' || character == 'k') {
            piece = new Knight(color);
        } else if (character == 'B' || character == 'b') {
            piece = new Bishop(color);
        } else if (character == 'Q' || character == 'q') {
            piece = new Queen(color);
        } else {
            throw new RuntimeException(MessagesLibrary.INVALID_CHOICE);
        }
        return piece;
    }

    /**
     * this function should only be called from "moveAPiece", This function is continuation of special case of movingAPiece
     * and should handle a king movment
     */
    private void moveAKing(Place start, Place finish, King king) {
        Direction direction = Place.calculateDirection(start, finish); // will throw exception if it is illegal move
        if (king.getColor() == Color.Black)
            blackKingPlace = finish;
        else
            whiteKingPlace = finish;

        // castling
        if (Place.calculateColumnDistance(start, finish) == 2) {
            if (direction == Direction.Left) {
                pieces[start.getRow()][start.getColumn() - 1] = pieces[start.getRow()][0];
                pieces[start.getRow()][0] = null;
                pieces[start.getRow()][start.getColumn() - 1].moved();
            } else if (direction == Direction.Right) {
                pieces[start.getRow()][start.getColumn() + 1] = pieces[start.getRow()][7];
                pieces[start.getRow()][7] = null;
                pieces[start.getRow()][start.getColumn() + 1].moved();
            }
        }

    }

    private boolean isKingThreaten(Color kingColor) {
        Place kingPlace = getKingPlace(kingColor);
        return isPlaceThreatenByAColor(kingPlace, getOpponentColor(kingColor));
    }

    /**
     * @param place the place you want to check if the {@param color} can move to
     * @param color the color of the piece you want to check if they can move to {@param place}
     * @return true if a piece of {@param color} can move to {@param place}
     */
    private boolean canColorMoveToPlace(Place place, Color color) {
        boolean canKingMoveToPlace = getPieceInPlace(getKingPlace(color)).isLegalPieceMove(getKingPlace(color), place, this);
        boolean canColorMoveToPlaceWithoutMovingItsKing =canColorMoveToPlaceWithoutMovingItsKing(place, color);
        return  canKingMoveToPlace|| canColorMoveToPlaceWithoutMovingItsKing;
    }


    /**
     * @param place the place you want to check if the {@param color} can move to
     * @param color the color of the piece you want to check if they can move to {@param place} without he's king
     * @return true if a piece of {@param color} can move to {@param place}
     */
    private boolean canColorMoveToPlaceWithoutMovingItsKing(Place place, Color color) {
        boolean placeThreaten = false;
        for (int i = 0; i < 8 & !placeThreaten; i++) {
            for (int j = 0; j < 8 & !placeThreaten; j++) {
                Place piecePlace = Place.getPlace(i, j);
                ChessPiece piece = getPieceInPlace(piecePlace);
                if (piece == null || piece instanceof King)
                    continue;
                else
                    placeThreaten = piece.getColor() == color && piece.isLegalPieceMove(piecePlace, place, this);
            }
        }
        return placeThreaten;
    }

    /**
     * @param place a place you want to check if is threatened by the color
     * @param color the color that you want to check if he is threatening the place
     * @return true if the color is threatened on the place, false otherwise
     */
    private boolean isPlaceThreatenByAColor(Place place, Color color) {
        Place kingPlace = getKingPlace(color);
        boolean isKingThreatenThePlace = Place.calculateRowDistance(place, kingPlace) <= 1 && Place.calculateColumnDistance(place, kingPlace) <= 1;

        // for all the pieces, but the king, if the piece is threatening the place its means it can go there, for the so we just need to check for the king
        return isKingThreatenThePlace || canColorMoveToPlaceWithoutMovingItsKing(place, color);
    }

    /**
     * this function returns all the Places you can move to prevent the chess.
     *
     * @param playerColor the color that in chess
     * @return A set of Places you can go to prevent the chess
     */
    private Set<Place> getPlacesToBlockChess(Color playerColor) {
        Set<Place> output = null;
        Place kingPlace = getKingPlace(playerColor);
        int numOfPieceThreatTheKing = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Place piecePlace = Place.getPlace(i, j);
                ChessPiece piece = getPieceInPlace(piecePlace);
                if (piece != null && piece.getColor() != playerColor && isLegalMove(piecePlace, kingPlace, getOpponentColor(playerColor))) {  // this piece is threaten the king
                    numOfPieceThreatTheKing++;
                    output = Place.getPath(piecePlace, kingPlace);
                }
            }
        }
        switch (numOfPieceThreatTheKing) {
            case 0:
                throw new RuntimeException(MessagesLibrary.NO_CHESS_ERROR);
            case 1:
                break;
            default:
                output = new HashSet<>();
        }

        return output;
    }


    private Place getKingPlace(Color color) {
        return color.equals(Color.White) ? whiteKingPlace : blackKingPlace;
    }

    private Color getOpponentColor(Color color) {
        return color == Color.White ? Color.Black : Color.White;
    }


    /**
     * @param color the player color we want to check if he is in checkmate (has lost)
     * @return true, if the {@param color} player in checkmate (has lost)
     */
    public boolean isInCheckMate(Color color) {

        if (!isKingThreaten(color))
            return false;

        for (Place place : getPlacesToBlockChess(color)) {
            if (canColorMoveToPlace(place, color))
                return false;
        }
        return getKingMovingOptions(color).isEmpty();
    }


    /**
     * @param color the player color we want to check in pat
     * @return true, if the {@param color} player in pat
     */
    public boolean isInPat(Color color) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = getPieceInPlace(i, j);

                //every tool can move always but the pawn and the king
                if (piece != null && piece.getColor().equals(color) && !(piece instanceof King)) {
                    if (!(piece instanceof Pawn))
                        //return false if the player (color) has a piece other than a king and pawns
                        return false;

                    // check if the pawn can move
                    int toAdd = color.equals(Color.White) ? -1 : 1; //up for white, down for black
                    boolean canEatLeft = false, canEatRight = false, canMoveForeword = false;
                    Place pawnPlace = Place.getPlace(i, j);
                    if (j > 0)
                        canEatLeft = isLegalMove(pawnPlace, Place.getPlace(i + toAdd, j - 1), color);
                    if (j < 7)
                        canEatRight = isLegalMove(pawnPlace, Place.getPlace(i + toAdd, j + 1), color);
                    canMoveForeword = isLegalMove(pawnPlace, Place.getPlace(i + toAdd, j), color);

                    if (canEatRight | canEatLeft | canMoveForeword)
                        return false;
                }
            }
        return getKingMovingOptions(color).isEmpty() && !isKingThreaten(color);
    }


    /**
     * @param playerColor the player color you want the moving option of its king
     * @return returns the places where the king can move
     */
    private Collection<Place> getKingMovingOptions(Color playerColor) {
        Set<Place> canMoveTo = new HashSet<>();
        Place kingPlace = getKingPlace(playerColor);
        for (Direction direction : fullRunnerValidMovementDirectionsMap.get(Queen.class)) {
            try {
                if (isLegalMove(kingPlace, kingPlace.move(direction), playerColor))
                    canMoveTo.add(kingPlace.move(direction));
            } catch (Exception ignored) {
                // if there is no place on the board in the direction we can ignore (not to add it)
            }
        }

        // castling
        try {

            Place twoToTheLeft = kingPlace.moveLeft().moveLeft();
            Place twoToTheRight = kingPlace.moveRight().moveRight();

            if (isLegalMove(kingPlace, twoToTheLeft, playerColor))
                canMoveTo.add(twoToTheLeft);

            if (isLegalMove(kingPlace, twoToTheRight, playerColor))
                canMoveTo.add(twoToTheRight);
        } catch (Exception ignore) {
            // if i got here that's mean that the king is not in his initial place so there cannot be any castling
        }


        return canMoveTo;
    }

    /**
     * This function use the visitor pattern
     *
     * @param src the piece Place which you want the moving option of
     * @return a Collection of Places that the piece from {@param src} can move to
     */
    public Collection<Place> calculateMovingOptions(Place src) {
        if (getPieceInPlace(src) == null)
            return new HashSet<>();

        return getPieceInPlace(src).getMovingOptions(src, this);
    }

    public Collection<Place> calculateMovingOptions(Place src, Pawn piece) {
        // pawn cannot move as a queen, but all of his possible directions (both black and white) are contained
        // inside the queen direction.
        // so when "calculateFullRunnerMovingOptions" invoke on the pawn, he will check all directions
        return calculateFullRunnerMovingOptions(src, fullRunnerValidMovementDirectionsMap.get(Queen.class));
    }

    public Collection<Place> calculateMovingOptions(Place src, Rook piece) {
        return calculateFullRunnerMovingOptions(src, fullRunnerValidMovementDirectionsMap.get(Rook.class));
    }

    public Collection<Place> calculateMovingOptions(Place src, Knight piece) {
        Set<Place> canMoveTo = new HashSet<>();
        for (Place dest : src.calculateKnightJumpOptionFromSrc()) {// get all possible moving options
            if (isLegalMove(src, dest, piece.getColor()))
                canMoveTo.add(dest);
        }
        return canMoveTo;
    }

    public Collection<Place> calculateMovingOptions(Place src, Bishop piece) {
        return calculateFullRunnerMovingOptions(src, fullRunnerValidMovementDirectionsMap.get(Bishop.class));
    }

    public Collection<Place> calculateMovingOptions(Place src, Queen piece) {
        return calculateFullRunnerMovingOptions(src, fullRunnerValidMovementDirectionsMap.get(Queen.class));
    }

    public Collection<Place> calculateMovingOptions(Place src, King piece) {
        return getKingMovingOptions(piece.getColor());
    }

    /**
     * @param src                the source place of the "full runner"
     * @param possibleDirections The directions the piece in {@param src} can move
     * @return A set of Places the piece in {@param src} can move to
     */
    private Collection<Place> calculateFullRunnerMovingOptions(Place src, Set<Direction> possibleDirections) {
        Color pieceColor = getPieceInPlace(src).getColor();
        Set<Place> canMoveTo = new HashSet<>();

        for (Direction direction : possibleDirections) {
            try {
                Place dest = src.move(direction);
                while (true) { //we need to check all the options because in check case, we might be able to move the piece to the end of the direction, but not in the middle
                    if (isLegalMove(src, dest, pieceColor))
                        canMoveTo.add(dest);

                    dest = dest.move(direction);
                }
            } catch (Exception unRelevant) {
                // this indicates that you got to the end of the board
            }
        }
        return canMoveTo;
    }

    public String getStringRepresentationOfPieceInPlace(Place piecePlace) {
        StringBuilder output = new StringBuilder();
        ChessPiece piece = getPieceInPlace(piecePlace);
        if (piece == null)
            return "";

        String className = piece.getClass().toString().substring(piece.getClass().toString().lastIndexOf(".") + 1, piece.getClass().toString().lastIndexOf(".") + 3);
        output.append(piece.getColor().toString().substring(0, 1) + className);

        return output.toString();
    }
}

