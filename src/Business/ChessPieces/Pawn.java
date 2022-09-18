package Business.ChessPieces;

import Business.Boards.TwoPlayerChessBoard;
import Business.Color;
import Business.Place;

public class Pawn  extends ChessPiece{


    public Pawn(Color color) {
        super(color);
    }

    @Override
    public boolean isLegalMove(Place origin, Place to, TwoPlayerChessBoard board) {
        return board.isLegalPieceMovement(origin,to, this);
    }

    private Pawn(Color color,boolean hasMoved) {
        super(color,hasMoved);
    }
    public ChessPiece clone() {
        return new Pawn(getColor(),hasMoved());
    }
}
