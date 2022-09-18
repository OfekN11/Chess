package Business.ChessPieces;

import Business.Boards.TwoPlayerChessBoard;
import Business.Color;
import Business.Place;

public class King extends ChessPiece{
    public King(Color color) {
        super(color);
    }

    @Override
    public boolean isLegalMove(Place origin, Place to, TwoPlayerChessBoard board) {
        return board.isLegalPieceMovement(origin,to,this);
    }

    private King(Color color,boolean hasMoved) {
        super(color,hasMoved);
    }
    public ChessPiece clone() {
        return new King(getColor(),hasMoved());
    }
}
