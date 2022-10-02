package Business.ChessPieces;

import Business.Boards.TwoPlayerChessBoard;
import Business.Color;
import Business.Place;

import java.util.Collection;

public class Rook  extends ChessPiece{
    public Rook(Color color) {
        super(color);
    }

    @Override
    public boolean isLegalPieceMove(Place origin, Place to, TwoPlayerChessBoard board) {
        return board.isLegalPieceMovement(origin,to,this);

    }

    private Rook(Color color,boolean hasMoved) {
        super(color,hasMoved);
    }
    public ChessPiece clone() {
        return new Rook(getColor(),hasMoved());
    }

    @Override
    public Collection<Place> getMovingOptions(Place src, TwoPlayerChessBoard board) {
        return board.calculateMovingOptions(src,this);
    }
}
