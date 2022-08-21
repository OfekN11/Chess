package Business.ChessPieces;

import Business.Boards.TwoPlayerChessBoard;
import Business.Place;

public class Queen  extends ChessPiece{
    @Override
    public boolean isLegalMove(Place origin, Place to, TwoPlayerChessBoard board) {
        return board.isLegalPieceMovement(origin,to,this);

    }
}
