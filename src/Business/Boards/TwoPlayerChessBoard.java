package Business.Boards;

import Business.ChessPieces.*;
import Business.Color;
import Business.Place;

import java.util.*;

/**
 * The TwoPlayerChessBoard is a class to represent a normal chess board,of a normal chess game.
 * This class is responsible for all the chess rules
 */
public class TwoPlayerChessBoard {
    private static final String ILLEGAL_MOVE = "Illegal move";
    private static final String INVALID_CHOICE = "Invalid choice";
    private static final Map<Class<? extends ChessPiece>,Set<Direction>> fullRunnerValidMovementDirectionsMap = new HashMap<>(); // contains the valid moving direction for the pieces
    static {
        fullRunnerValidMovementDirectionsMap.put(Bishop.class,new HashSet<>(Arrays.asList(Direction.RightDownDiagonal,Direction.RightUpDiagonal,Direction.LeftDownDiagonal,Direction.LeftUpDiagonal)));
        fullRunnerValidMovementDirectionsMap.put(Queen.class,new HashSet<>(Arrays.asList(Direction.LeftUpDiagonal,Direction.Up,Direction.RightUpDiagonal,Direction.Right,Direction.RightDownDiagonal,Direction.Down,Direction.LeftDownDiagonal,Direction.Left)));
        fullRunnerValidMovementDirectionsMap.put(Rook.class,new HashSet<>(Arrays.asList(Direction.Right,Direction.Up,Direction.Left,Direction.Down)));
    }

    public enum Direction {Up, Down, Left, Right, LeftUpDiagonal, LeftDownDiagonal, RightUpDiagonal, RightDownDiagonal, Knight}

    private final ChessPiece[][] pieces;
    private Place whiteKingPlace;
    private Place blackKingPlace;


    /**
     * This constructor is meant for testing a board after a move, and if the new board is not a valid one, it will be easy to reverse the changes
     * @param board  a board to copy
     * @param start  The place piece you want to move without conditions
     * @param finish The place you want to move the piece
     */
    private TwoPlayerChessBoard(TwoPlayerChessBoard board, Place start, Place finish) {
        pieces = board.pieces.clone();
        moveAPiece(start, finish);
    }

    /**
     * @param playerColor the player color who make the move
     * @param start       place of the piece you want to move
     * @param finish      where to move the piece
     */
    public void play(Color playerColor, Place start, Place finish) throws RuntimeException {
        validInBoardPlaces(start);
        validInBoardPlaces(finish);

        if (!isLegalMove(start, finish,playerColor))
            throw new RuntimeException(INVALID_CHOICE);

        moveAPiece(start,finish);
    }

    private void validInBoardPlaces(Place start) {
        if (start.getRow() > 7 | start.getColumn() < 0 | start.getRow()<0 | start.getColumn()>7)
            throw new IllegalArgumentException(INVALID_CHOICE);
    }

    /**
     * design pattern visitor.
     */
    public boolean isLegalMove(Place start, Place finish,Color playerColor) {
        ChessPiece piece = getPieceInPlace(start);
        if (piece == null || piece.getColor() != playerColor)
            return false;

        return piece.isLegalMove(start, finish, this) && !new TwoPlayerChessBoard(this, start, finish).isKingThreaten(playerColor); // checking that the move is legal for the piece, and that the king is not threaten
    }


    public boolean isLegalPieceMovement(Place start, Place finish, Pawn pawn) {
        Direction direction = calculateDirection(start, finish);
        Place finishPlusOne = new Place(finish); // we need "finishPlusOne" because "isThereAPieceInTheWay" does not check the finish place.
        finishPlusOne.move(direction);
        int rowDifferent = Place.calculateRowDistance(start, finish);

        switch (direction) {
            // check if the pass is clear
            case Up:
            case Down:

                if (isThereAPieceBetween(start, finishPlusOne, direction))
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
        return fullRunnerIsLegalMovement(start,finish,fullRunnerValidMovementDirectionsMap.get(Rook.class));
    }


    public boolean isLegalPieceMovement(Place start, Place finish, Knight knight) {
        return calculateDirection(start, finish) == Direction.Knight;

    }

    public boolean isLegalPieceMovement(Place start, Place finish, Bishop piece) {
        return fullRunnerIsLegalMovement(start,finish,fullRunnerValidMovementDirectionsMap.get(Bishop.class));
    }

    public boolean isLegalPieceMovement(Place start, Place finish, Queen piece) {
        return fullRunnerIsLegalMovement(start,finish,fullRunnerValidMovementDirectionsMap.get(Queen.class));

    }



    public boolean isLegalPieceMovement(Place start, Place finish, King king) {
        Direction direction = calculateDirection(start, finish);
        Color opponentColor = king.getColor() == Color.White ? Color.Black : Color.White;
        int rowDistance = Place.calculateRowDistance(start, finish);
        int columnDistance = Place.calculateColumnDistance(start, finish);
        if (rowDistance>1 || columnDistance>2 || direction == Direction.Knight)
            return false;

        // castling
        if (columnDistance == 2){
            if (king.hasMoved())
                return false;
            ChessPiece rook = direction == Direction.Right ? getPieceInPlace(start.getRow(),8) :  getPieceInPlace(start.getRow(),0);
            Place finishPlusOne = new Place(finish);
            Place startPlusOne = new Place(start);
            finishPlusOne.move(direction);
            startPlusOne.move(direction);
            return rook instanceof Rook && !rook.hasMoved() && !isThereAPieceBetween(start,finishPlusOne,direction) && !isPlaceThreatenByAColor(startPlusOne,opponentColor) && !isPlaceThreatenByAColor(finish,opponentColor);
        }
        return fullRunnerIsLegalMovement(start,finish, fullRunnerValidMovementDirectionsMap.get(Queen.class)); // we use the queen class because king and queen can move the same direction, and we checked that the king don't move 2 steps
    }

    /**
     * Checks if a full runner piece ,who start in "start" can go to "finish" using the "validDirectionsForPieceType"
     * @param start piece start place
     * @param finish piece start place
     * @param validDirections The direction the piece is allow to move
     * @return true if its a legal movement
     */
    private boolean fullRunnerIsLegalMovement(Place start, Place finish, Set<Direction> validDirections){
        Direction direction = calculateDirection(start, finish);
        ChessPiece piece = getPieceInPlace(finish);
        return validDirections.contains(direction) && !isThereAPieceBetween(start, finish, direction) && piece != null && piece.getColor() != getPieceInPlace(start).getColor();
    }

    /**
     * @param start     The current Place of a piece you want to move
     * @param finish    Where you want to move the piece
     * @param direction the direction between "start" and "finish", can be calculated by "calculateDirection"
     * @return true if there is a piece between "start" and "finish" not including both, false otherwise.
     */
    private boolean isThereAPieceBetween(Place start, Place finish, Direction direction) {
        Place startCopy = new Place(start);
        startCopy.move(direction);
        while (!startCopy.equals(finish))
            if (getPieceInPlace(startCopy) != null)
                return true;
        return false;
    }


    /**
     * calculate the direction of the movement
     * @param start from where
     * @param finish to where
     * @return the direction of the move
     */
    private Direction calculateDirection(Place start, Place finish) throws RuntimeException {
        if (start.equals(finish))
            throw new RuntimeException("please choose two different locations");

        if (start.getRow() == finish.getRow())
            return start.getColumn() < finish.getColumn() ? Direction.Right : Direction.Left;

        else if (start.getColumn() == finish.getColumn())
            return start.getRow() < finish.getRow() ? Direction.Down : Direction.Up;

        else if (start.getRow() - start.getColumn() == finish.getRow() - finish.getColumn())
            return start.getRow() < finish.getRow() ? Direction.RightDownDiagonal : Direction.LeftUpDiagonal;

        else if (start.getRow() + start.getColumn() == finish.getRow() + finish.getColumn()) {
            return start.getRow() < finish.getRow() ? Direction.RightUpDiagonal : Direction.LeftDownDiagonal;
        } else {
            int rowDifferent = Place.calculateRowDistance(start, finish);
            int columnDifferent = Place.calculateColumnDistance(start, finish);
            if (rowDifferent < 3 && columnDifferent < 3 && rowDifferent != columnDifferent)
                return Direction.Knight;
        }
        throw new RuntimeException(ILLEGAL_MOVE);
    }

    private ChessPiece getPieceInPlace(Place place) {
        return pieces[place.getRow()][place.getColumn()];
    }
    private ChessPiece getPieceInPlace(int row, int column) {
        return pieces[row][column];
    }


    /**
     * This function move the piece on the board, without validating rules, it replace the start place with a null
     *
     * @param start  where the piece at
     * @param finish where to move it
     */
    private void moveAPiece(Place start, Place finish) {
        ChessPiece piece = getPieceInPlace(start);
        if (piece instanceof King) {
            moveAKing(start, finish);
        }
        pieces[finish.getRow()][finish.getColumn()] = piece;
        pieces[start.getRow()][start.getColumn()] = null;
    }

    /**
     * this function should only be called from "moveAPiece", This function is continuation of special case of movingAPiece
     *  
     */
    private void moveAKing(Place start, Place finish) {
        Direction direction = calculateDirection(start, finish);
        ChessPiece king = getPieceInPlace(start);
        if (king.getColor() == Color.Black)
            blackKingPlace = finish;
        else
            whiteKingPlace = finish;

        // castling
        if (Place.calculateColumnDistance(start, finish) == 2) {
            if (direction == Direction.Left) {
                pieces[start.getRow()][start.getColumn() - 1] = pieces[start.getRow()][0];
                pieces[start.getRow()][0] = null;

            } else if (direction == Direction.Right) {
                pieces[start.getRow()][start.getColumn() + 1] = pieces[start.getRow()][8];
                pieces[start.getRow()][8] = null;
            }
        }

        king.moved();

    }

    private boolean isKingThreaten(Color kingColor) {
        Place kingPlace = kingColor == Color.White ? whiteKingPlace : blackKingPlace;
        return isPlaceThreatenByAColor(kingPlace, kingColor);
    }

    /**
     * @param place a place you want to check if is threaten by the color
     * @param color the color that you want to check if he is threaten on the place
     * @return true if the color is threaten on the place, false otherwise
     */
    private boolean isPlaceThreatenByAColor(Place place, Color color) {
        boolean placeThreaten = false;
        for (int i = 0; i < 8 & !placeThreaten; i++) {
            for (int j = 0; j < 8 & !placeThreaten; j++) {
                Place piecePlace = new Place(i, j);
                ChessPiece piece = getPieceInPlace(piecePlace);
                if (piece instanceof King) {
                    placeThreaten = color != piece.getColor() && Place.calculateRowDistance(place, piecePlace) < 2 && Place.calculateColumnDistance(place, piecePlace) < 2;
                } else
                    placeThreaten = piece.getColor() != color && isLegalMove(piecePlace, place, color);
            }
        }
        return placeThreaten;
    }

}
