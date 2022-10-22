package Business.ChessPieces;

import Business.Boards.TwoPlayerChessBoard;
import Business.Color;
import Business.Place;

import java.util.Collection;

public abstract class ChessPiece {
    private boolean hasMoved;
    private Color color;

    public ChessPiece(Color color){
        hasMoved =false;
        this.color = color;
    }

    public ChessPiece(Color color, boolean hasMoved){
        this.hasMoved =hasMoved;
        this.color = color;
    }

    public abstract boolean isLegalPieceMove(Place start, Place finish, TwoPlayerChessBoard board);

    public void moved(){
        hasMoved = true;}
    public boolean hasMoved(){return hasMoved;}
    public Color getColor() {
        return color;
    }


    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && this.hasMoved == ((ChessPiece) obj).hasMoved && this.color == ((ChessPiece) obj).color;
    }

    public abstract ChessPiece clone();


    public abstract Collection<Place> getMovingOptions(Place src, TwoPlayerChessBoard board);

    public String toString(){
        String className = this.getClass().getSimpleName();
        if (className.equals("Knight")){
            return getColor() == Color.White ? "H" : "h";
        }
        return getColor() == Color.White ? className.substring(0,1) : className.substring(0,1).toLowerCase();
    }
}
