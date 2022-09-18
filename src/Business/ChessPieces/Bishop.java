package Business.ChessPieces;

import Business.Boards.TwoPlayerChessBoard;
import Business.Color;
import Business.Place;

public class Bishop  extends ChessPiece{
    public Bishop(Color color) {
        super(color);
    }

    private Bishop(Color color,boolean hasMoved) {
        super(color,hasMoved);
    }

    @Override
    public boolean isLegalMove(Place origin, Place to, TwoPlayerChessBoard board) {
        return board.isLegalPieceMovement(origin,to,this);
    }

    @Override
    public ChessPiece clone() {
        return new Bishop(getColor(),hasMoved());
    }

}
