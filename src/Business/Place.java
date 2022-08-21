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

    public void moveRight(){column++;}
    public void moveLeft(){column--;}
    public void moveUp(){row--;}
    public void moveDown(){row++;}

    public void move(TwoPlayerChessBoard.Direction direction){
        switch (direction){

            case Up -> moveUp();

            case Down -> moveDown();

            case Left -> moveLeft();

            case Right -> moveRight();

            case LeftUpDiagonal -> {moveLeft(); moveUp();
            }
            case LeftDownDiagonal -> {moveLeft(); moveDown();
            }
            case RightUpDiagonal -> {moveRight(); moveUp();
            }
            case RightDownDiagonal -> {moveRight(); moveDown();
            }
            case Knight -> throw new IllegalArgumentException(ILLEGAL_MOVE);

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
