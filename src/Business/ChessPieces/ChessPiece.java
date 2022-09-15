package Business.ChessPieces;

import Business.Boards.TwoPlayerChessBoard;
import Business.Color;
import Business.Place;

public abstract class ChessPiece {
    private boolean moved;
    private Color color;

    public ChessPiece(Color color){
        moved =false;
        this.color = color;
    }

    public abstract boolean isLegalMove(Place start, Place finish, TwoPlayerChessBoard board);

    public void moved(){moved = true;}
    public boolean hasMoved(){return moved;}
    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && this.moved == ((ChessPiece) obj).moved && this.color == ((ChessPiece) obj).color;
    }
}
