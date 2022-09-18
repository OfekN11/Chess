package Business.ChessPieces;

import Business.Boards.TwoPlayerChessBoard;
import Business.Color;
import Business.Place;

public class Knight  extends ChessPiece{
    public Knight(Color color) {
        super(color);
    }

    @Override
    public boolean isLegalMove(Place origin, Place to, TwoPlayerChessBoard board) {
        return board.isLegalPieceMovement(origin,to,this);

    }

    private Knight(Color color,boolean hasMoved) {
        super(color,hasMoved);
    }
    public ChessPiece clone() {
        return new Knight(getColor(),hasMoved());
    }
}
