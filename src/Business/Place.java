package Business;

import Business.Boards.TwoPlayerChessBoard;

public class Place {
    private static final String ILLEGAL_MOVE = "Illegal move";


    private int row;
    private int column;

    public Place(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Place(Place place) {
        this.row = place.row;
        this.column= place.column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Place moveRight(){return new Place(this.row,++column);}
    public Place moveLeft(){return new Place(this.row,--column);}
    public Place moveUp(){return new Place(--row,this.column);}
    public Place moveDown(){return new Place(++row,this.column);}

    public Place move(TwoPlayerChessBoard.Direction direction){
        switch (direction){

            case Up: return moveUp();

            case Down: return moveDown();

            case Left: return moveLeft();

            case Right: return moveRight();

            case LeftUpDiagonal :{return moveLeft().moveUp();
            }
            case LeftDownDiagonal :{return moveLeft().moveDown();
            }
            case RightUpDiagonal :{return moveRight().moveUp();
            }
            case RightDownDiagonal :{return moveRight().moveDown();
            }
            default: throw new IllegalArgumentException(ILLEGAL_MOVE);
        }
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Place other))
            return false;
        return  this.column == other.column & this.row == other.row;
    }

    public static int calculateRowDistance(Place place1,Place place2){
        return Math.abs(place1.row- place2.row);
    }
    public static int calculateColumnDistance(Place place1,Place place2){
        return Math.abs(place1.column- place2.column);
    }
}
