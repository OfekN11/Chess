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
    public TwoPlayerChessBoard(){
        pieces = new ChessPiece[8][8];
        parseStringToPieces(NORMAL_PIECE_ORDER_STRING);
    }


    public TwoPlayerChessBoard(String stringToParse){
        pieces = new ChessPiece[8][8];
        parseStringToPieces(stringToParse);
    }

    public void parseStringToPieces(String stringToParse){
        int row = 0, column =0;
        for (int i = 0; i < stringToParse.length(); i++,column++) {
            if (stringToParse.charAt(i) == '\n')
                 {row++ ; column = -1; continue;} // going threw the next line column will be zero at the next turn

            try {

                validBoardPlace(Place.getPlace(row,column));
            }catch (Exception e){
                throw new RuntimeException(MessagesLibrary.ILLEGAL_STRING);
            }

            switch (stringToParse.charAt(i)){
                case 'P' -> pieces[row][column] = new Pawn(Color.White);
                case 'p' ->  pieces[row][column] = new Pawn(Color.Black);
                case 'R' ->  pieces[row][column] = new Rook(Color.White);
                case 'r' ->  pieces[row][column] = new Rook(Color.Black);
                case 'B' ->  pieces[row][column] = new Bishop(Color.White);
                case 'b' ->  pieces[row][column] = new Bishop(Color.Black);
                case 'H' ->  pieces[row][column] = new Knight(Color.White);
                case 'h' ->  pieces[row][column] = new Knight(Color.Black);
                case 'K' ->  {pieces[row][column] = new King(Color.White); whiteKingPlace =Place.getPlace(row,column);}
                case 'k' ->  {pieces[row][column] = new King(Color.Black); blackKingPlace = Place.getPlace(row,column);}
                case 'Q' ->  pieces[row][column] = new Queen(Color.White);
                case 'q' ->  pieces[row][column] = new Queen(Color.Black);
                case '-' ->   pieces[row][column] = null;

            }
        }
    }



    /**
     * reset the boards, and pieces so a new game can begin
     */


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
                this.pieces[i][j] = board.getPieceInPlace(i,j);
            }
        }

        this.blackKingPlace = board.blackKingPlace;
        this.whiteKingPlace = board.whiteKingPlace;
        moveAPiece(start, finish,()->'Q');
    }

    // Methods

    private void validBoardPlace(Place place) {
        if (place.getRow() > 7 | place.getColumn() < 0 | place.getRow() < 0 | place.getColumn() > 7)
            throw new IllegalArgumentException(MessagesLibrary.INVALID_CHOICE);
    }

    /**
     * design pattern visitor.
     */
    public boolean isLegalMove(Place start, Place finish, Color playerColor) {
        validBoardPlace(start);
        validBoardPlace(finish);
        if (getPieceInPlace(start) == null || getPieceInPlace(start).getColor() != playerColor)
            return false;
        ChessPiece piece = getPieceInPlace(start);

        boolean legalMovement =piece.isLegalMove(start, finish, this);
        boolean notCauseASelfCheck = !new TwoPlayerChessBoard(this, start, finish).isKingThreaten(playerColor); // checking that the move is legal for the piece, and that the king is not threaten

        return  legalMovement && notCauseASelfCheck;
    }


    public boolean isLegalPieceMovement(Place start, Place finish, Pawn pawn) {
        Direction direction;
        try {
            direction = Place.calculateDirection(start, finish);

        }catch (Exception e) {
            return false;
        }
        Place finishPlusOne;

        try { // put in try and catch cause +1 to a direction can be out of the board.
             finishPlusOne = finish.move(direction); // we need "finishPlusOne" because "isThereAPieceInTheWay" does not check the finish place.

        }catch (Exception e){// case when +1 of the direction is out of the table, so we just act as the finish is finish, so we check if there is a piece in finish manually
            if (getPieceInPlace(finish) != null)
                return true;
            else
                finishPlusOne = finish;
        }
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
        return fullRunnerIsLegalPieceMovement(start, finish, fullRunnerValidMovementDirectionsMap.get(Rook.class));
    }


    public boolean isLegalPieceMovement(Place start, Place finish, Knight knight) {
        try {
            return Place.calculateDirection(start, finish) == Direction.Knight;
        }catch (Exception e){
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

        }catch (Exception e) {
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
            ChessPiece rook = direction == Direction.Right ? getPieceInPlace(start.getRow(), 8) : getPieceInPlace(start.getRow(), 0);
            Place finishPlusOne = finish.move(direction);
            Place startPlusOne = start.move(direction);
            return rook instanceof Rook && !rook.hasMoved() && !isThereAPieceBetween(start, finishPlusOne, direction) && !isPlaceThreatenByAColor(startPlusOne, opponentColor) && !isPlaceThreatenByAColor(finish, opponentColor);
        }
        return fullRunnerIsLegalPieceMovement(start, finish, fullRunnerValidMovementDirectionsMap.get(Queen.class)) && !isPlaceThreatenByAColor(finish,getOpponentColor(king.getColor())); // we use the queen class because king and queen can move the same direction, and we checked that the king don't move 2 steps
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

        }catch (Exception e) {
            return false;
        }
        ChessPiece piece = getPieceInPlace(start);
        boolean validateRoute = validDirections.contains(direction) && !isThereAPieceBetween(start, finish, direction) ;
        boolean emptyPlaceOrDifferentColor =  getPieceInPlace(finish) ==null || getPieceInPlace(finish).getColor() != piece.getColor();
        return validateRoute & emptyPlaceOrDifferentColor;
    }

    /**
     * @param start     The current Place of a piece you want to move
     * @param finish    Where you want to move the piece
     * @param direction the direction between "start" and "finish", can be calculated by "calculateDirection"
     * @return true if there is a piece between "start" and "finish" not including both, false otherwise.
     */
    private boolean isThereAPieceBetween(Place start, Place finish, Direction direction) {
        start = start.move(direction);
        while (!start.equals(finish)) {
            if (getPieceInPlace(start) != null)
                return true;
            else
                start = start.move(direction);
        }
        return false;
    }

    public ChessPiece getPieceInPlace(Place place) {
        return pieces[place.getRow()][place.getColumn()];
    }

    public ChessPiece getPieceInPlace(int row, int column) {
        return pieces[row][column];
    }


    /**
     * This function move the piece on the board, without validating rules, it replaces the start place with a null
     * @param start  where the piece at
     * @param finish where to move it
     * @param promotionLetterSupplier in case of a promotion, the supplier should give the letter of the piece he wants to promote to
     */
    public void moveAPiece(Place start, Place finish, Supplier<Character> promotionLetterSupplier) {
        ChessPiece piece = getPieceInPlace(start);
        if (piece instanceof King) {
            moveAKing(start, finish, (King) piece);
        }
        if ((piece instanceof Pawn) && isPromotionNeeded(finish, (Pawn) piece))
            piece = getPromotionPiece(piece.getColor(),promotionLetterSupplier);
        piece.moved();
        pieces[finish.getRow()][finish.getColumn()] = piece;
        pieces[start.getRow()][start.getColumn()] = null;
    }

    /**
     *  this function checks if a pawn gets to the end of the board
     */
    private boolean isPromotionNeeded(Place finish, Pawn pawn) {
        return pawn.getColor().equals(Color.White) && finish.getRow() == 0 || pawn.getColor().equals(Color.Black) && finish.getRow() == 7;
    }

    /**
     * promote a pawn
     * @param color the color of the pawn that is being promoted
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
                pieces[start.getRow()][start.getColumn() + 1] = pieces[start.getRow()][8];
                pieces[start.getRow()][8] = null;
                pieces[start.getRow()][start.getColumn() + 1].moved();
            }
        }

    }

    private boolean isKingThreaten(Color kingColor) {
        Place kingPlace = getKingPlace(kingColor);
        return isPlaceThreatenByAColor(kingPlace, getOpponentColor(kingColor));
    }

    /**
     * @param place a place you want to check if is threatened by the color
     * @param color the color that you want to check if he is threatening the place
     * @return true if the color is threatened on the place, false otherwise
     */
    private boolean isPlaceThreatenByAColor(Place place, Color color) {
        boolean placeThreaten = false;
        for (int i = 0; i < 8 & !placeThreaten; i++) {
            for (int j = 0; j < 8 & !placeThreaten; j++) {
                Place piecePlace = Place.getPlace(i, j);
                ChessPiece piece = getPieceInPlace(piecePlace);
                if (piece == null)
                    continue;

                if (piece instanceof King) {
                    placeThreaten = color == piece.getColor() && piece.isLegalMove(piecePlace,place,this);
                } else
                    placeThreaten = piece.getColor() == color && piece.isLegalMove(piecePlace, place,this);
            }
        }
        return placeThreaten;
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

    /**
     * @param playerColor the player color you want the moving option of its king
     * @return returns the places where the king can move
     */
    private Collection<? extends Place> getKingMovingOptions(Color playerColor) {
        Set<Place> availablePlaces = new HashSet<>();
        Place kingPlace = getKingPlace(playerColor);
        for (Direction direction : fullRunnerValidMovementDirectionsMap.get(Queen.class))
            try {
                if (isLegalMove(kingPlace, kingPlace.move(direction), playerColor))
                    availablePlaces.add(kingPlace.move(direction));
            } catch (Exception ignored) {
                // if there is no place on the board in the direction we can ignore (not to add it)
            }
        return availablePlaces;
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
            if (isPlaceThreatenByAColor(place, color))
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
                if (piece.getColor().equals(color) && !(piece instanceof King) && !(piece instanceof Pawn))
                    return false;
            }
        return getKingMovingOptions(color).isEmpty() && isKingThreaten(color);
    }

}


/*private void placePieces() {
        // pawns
        for (int column = 0; column < 8; column++) {
            pieces[1][column] = new Pawn(Color.Black);
            pieces[6][column] = new Pawn(Color.White);
        }

        //rook
        pieces[0][0] = new Rook(Color.Black);
        pieces[0][7] = new Rook(Color.Black);
        pieces[7][7] = new Rook(Color.White);
        pieces[7][0] = new Rook(Color.White);

        //Knight
        pieces[0][1] = new Knight(Color.Black);
        pieces[0][6] = new Knight(Color.Black);
        pieces[7][1] = new Knight(Color.White);
        pieces[7][6] = new Knight(Color.White);

        //Bishop
        pieces[0][2] = new Bishop(Color.Black);
        pieces[0][5] = new Bishop(Color.Black);
        pieces[7][2] = new Bishop(Color.White);
        pieces[7][5] = new Bishop(Color.White);

        // King
        pieces[0][3] = new King(Color.Black);
        pieces[7][3] = new King(Color.White);
        blackKingPlace = Place.getPlace(0, 3);
        whiteKingPlace = Place.getPlace(7, 3);

        //Queen
        pieces[0][4] = new Queen(Color.Black);
        pieces[7][4] = new Queen(Color.White);

        for (int row = 2; row < 6; row++) {
            for (int column = 0; column < 8; column++) {
                pieces[row][column] = null;
            }
        }
    }*/

