package Business;

import java.util.*;


/**
 * this class represent place on the board
 */
public class Place {

    public enum Direction {Up, Down, Left, Right, LeftUpDiagonal, LeftDownDiagonal, RightUpDiagonal, RightDownDiagonal, Knight}

    // if the player give up we will send that
    public static final Place GIVE_UP_PLACE = new Place(-1, -1);

    private static final List<List<Place>> places = new ArrayList<>();

    static {
        for (int i = 0; i < 8; i++) {
            places.add(new ArrayList<>());
            for (int j = 0; j < 8; j++) {
                places.get(i).add(new Place(i, j));
            }
        }
    }

    private final int row;
    private final int column;

    private Place(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Place moveRight() {
        return getPlace(this.row, column + 1);
    }

    public Place moveLeft() {
        return getPlace(this.row, column - 1);
    }

    public Place moveUp() {
        return getPlace(row - 1, this.column);
    }

    public Place moveDown() {
        return getPlace(row + 1, this.column);
    }

    public Place move(Direction direction) {
        switch (direction) {

            case Up:
                return moveUp();

            case Down:
                return moveDown();

            case Left:
                return moveLeft();

            case Right:
                return moveRight();

            case LeftUpDiagonal: {
                return moveLeft().moveUp();
            }
            case LeftDownDiagonal: {
                return moveLeft().moveDown();
            }
            case RightUpDiagonal: {
                return moveRight().moveUp();
            }
            case RightDownDiagonal: {
                return moveRight().moveDown();
            }
            default:
                throw new IllegalArgumentException(MessagesLibrary.ILLEGAL_MOVE);
        }
    }

    public Set<Place> calculateKnightJumpOptionFromSrc() {
        int[] rowToAdd ={-2,-1,1,2,2,1,-1,-2};
        int[] columnToAdd ={1,2,2,1,-1,-2,-2,-1};
        Set<Place> output = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            try{
                output.add(getPlace(this.row +rowToAdd[i],this.column+columnToAdd[i]));
            }
            catch (Exception ignored){
                // out of
            }
        }
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Place other))
            return false;
        return this.column == other.column & this.row == other.row;
    }

    public static int calculateRowDistance(Place place1, Place place2) {
        return Math.abs(place1.row - place2.row);
    }

    public static int calculateColumnDistance(Place place1, Place place2) {
        return Math.abs(place1.column - place2.column);
    }

    public static Place getPlace(int row, int column) {

        return places.get(row).get(column);
    }


    /**
     * @param start starting place
     * @param end   ending Place
     * @return all the places between start and end, includes start, exclude end!
     */
    public static Set<Place> getPath(Place start, Place end) {
        Place next = start;
        Set<Place> output = new HashSet<>();
        Direction direction = calculateDirection(start, end);
        if (direction != Direction.Knight)
            while (!next.equals(end)) {
                output.add(next);
                next = next.move(direction);
            }
        return output;
    }


    /**
     * calculate the direction of the movement
     *
     * @param start  from where
     * @param finish to where
     * @return the direction of the move
     */
    public static Direction calculateDirection(Place start, Place finish) throws RuntimeException {
        if (start.equals(finish))
            throw new RuntimeException("please choose two different locations");

        if (start.getRow() == finish.getRow())
            return start.getColumn() < finish.getColumn() ? Direction.Right : Direction.Left;

        else if (start.getColumn() == finish.getColumn())
            return start.getRow() < finish.getRow() ? Direction.Down : Direction.Up;

        else if (start.getRow() - start.getColumn() == finish.getRow() - finish.getColumn())
            return start.getRow() < finish.getRow() ? Direction.RightDownDiagonal : Direction.LeftUpDiagonal;

        else if (start.getRow() + start.getColumn() == finish.getRow() + finish.getColumn()) {
            return start.getRow() < finish.getRow() ? Direction.LeftDownDiagonal : Direction.RightUpDiagonal;
        } else {
            int rowDifferent = Place.calculateRowDistance(start, finish);
            int columnDifferent = Place.calculateColumnDistance(start, finish);
            if (rowDifferent < 3 && columnDifferent < 3 && rowDifferent != columnDifferent)
                return Direction.Knight;
        }
        throw new RuntimeException(MessagesLibrary.ILLEGAL_MOVE);
    }
}
