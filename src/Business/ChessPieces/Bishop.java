package Business.ChessPieces;

import Business.Boards.TwoPlayerChessBoard;
import Business.Color;
import Business.Place;

public class Bishop  extends ChessPiece{
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public boolean isLegalMove(Place origin, Place to, TwoPlayerChessBoard board) {
        return board.isLegalPieceMovement(origin,to,this);
    }

}
