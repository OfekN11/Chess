package Business.Boards;

import Business.ChessPieces.*;
import Business.Color;
import Business.Place;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TwoPlayerChessBoardTest {
    private final String emptyLine = "--------\n";
    private final String table1 =
            "Q-------\n" +
            "--k-----\n" +
            emptyLine +
            emptyLine +
            emptyLine +
            emptyLine +
            emptyLine +
            "---K----";

    private final String table2 =
            "--------\n" +
            "--------\n" +
            "r-----B-\n" +
            "k-------\n" +
            "H-------\n" +
            "K----Q--\n" +
            emptyLine +
            emptyLine;


    private final String table3 =
            "---k---r\n" +
            "P-------\n" +
            "r-----B-\n" +
            "k-p-----\n" +
            "H-P-----\n" +
            "K----Q--\n" +
            emptyLine +
            emptyLine;

    private final String table4 =
            "Q-------\n" +
            "--k---p-\n" +
            emptyLine +
            emptyLine +
            emptyLine +
            emptyLine +
            emptyLine +
            "---K--P-";


    private final String table5 =
            "--------\n" +
            "---p--p-\n" +
            "r-----B-\n" +
            "kh-q----\n" +
            "H-------\n" +
            "K----Q--\n" +
            emptyLine +
            emptyLine;

    private final String table6 =
            "Q-------\n" +
            "-k------\n" +
            "r-----B-\n" +
            "kh-q----\n" +
            "H-------\n" +
            "K----Q--\n" +
            emptyLine +
            emptyLine;

    private final String table7 =
            "Q--bk---\n" +
            "--------\n" +
            "r-----B-\n" +
            "kh-q----\n" +
            "H-------\n" +
            "K----Q--\n" +
            emptyLine +
            emptyLine;

    private final String table8 =
            "r---k--r\n" +
            emptyLine +
            emptyLine +
            emptyLine +
            emptyLine +
            emptyLine +
            emptyLine +
            "R---K--R\n";

    private final String table9 =
            "Q---k--r\n" +
            "R-------\n" +
            emptyLine +
            emptyLine +
            emptyLine +
            emptyLine +
            emptyLine +
            "R---K---\n";

    private final String table10 =
            "Q---k--r\n" +
            "Rp------\n" +
            "rP------\n" +
            emptyLine +
            emptyLine +
            emptyLine +
            emptyLine +
            "R---K---\n";
    private TwoPlayerChessBoard board;


    @Test
    void isInCheckMate() {
        board = new TwoPlayerChessBoard(table7);
        assertFalse(board.isInCheckMate(Color.Black));
        try {
            //should throw exception because there is no chess
            board.isInCheckMate(Color.White);
            fail();
        }catch (RuntimeException ignored){

        }
        board = new TwoPlayerChessBoard(table9);
        assertTrue(board.isInCheckMate(Color.Black));
        board.moveAPiece(Place.getPlace(0,7),Place.getPlace(2,1),()->'Q');
        assertFalse(board.isInCheckMate(Color.Black));

    }

    @Test
    void isInPat() {
        board = new TwoPlayerChessBoard(table8);
        assertFalse(board.isInPat(Color.Black));
        board.moveAPiece(Place.getPlace(0,1),Place.getPlace(0,0),()->'Q');
        board.moveAPiece(Place.getPlace(0,1),Place.getPlace(0,7),()->'Q');
        board.moveAPiece(Place.getPlace(7,7),Place.getPlace(1,5),()->'Q');
        board.moveAPiece(Place.getPlace(7,0),Place.getPlace(1,3),()->'Q');
        assertTrue(board.isInPat(Color.Black));
    }

    @Test
    void parseStringConstructorTest() {
        TwoPlayerChessBoard board1 = new TwoPlayerChessBoard(table1);
        TwoPlayerChessBoard board2 = new TwoPlayerChessBoard(table2);
        TwoPlayerChessBoard board3 = new TwoPlayerChessBoard(table3);

        // board 1
        assertTrue(board1.getPieceInPlace(0,0).getClass().equals(Queen.class) && board1.getPieceInPlace(0,0).getColor()==Color.White);
        assertTrue(board1.getPieceInPlace(1,2).getClass().equals(King.class) && board1.getPieceInPlace(1,2).getColor()==Color.Black);
        assertNull(board1.getPieceInPlace(3, 3));
        assertTrue(board1.getPieceInPlace(7,3).getClass().equals(King.class) && board1.getPieceInPlace(7,3).getColor()==Color.White);

        // board 2
        assertTrue(board2.getPieceInPlace(2,0).getClass().equals(Rook.class) && board2.getPieceInPlace(2,0).getColor()==Color.Black);
        assertTrue(board2.getPieceInPlace(2,6).getClass().equals(Bishop.class) && board2.getPieceInPlace(2,6).getColor()==Color.White);
        assertTrue(board2.getPieceInPlace(4,0).getClass().equals(Knight.class) && board2.getPieceInPlace(4,0).getColor()==Color.White);
        assertTrue(board2.getPieceInPlace(5,5).getClass().equals(Queen.class) && board2.getPieceInPlace(5,5).getColor()==Color.White);

        // board 3
        assertTrue(board3.getPieceInPlace(3,2).getClass().equals(Pawn.class) && board3.getPieceInPlace(3,2).getColor()==Color.Black);
        assertTrue(board3.getPieceInPlace(4,2).getClass().equals(Pawn.class) && board3.getPieceInPlace(4,2).getColor()==Color.White);
    }


    @Test
    public void usingOpponentPiece() {
        board = new TwoPlayerChessBoard();
        assertFalse(board.isLegalMove(Place.getPlace(1, 0), Place.getPlace(2, 0), Color.White));
        assertFalse(board.isLegalMove(Place.getPlace(6, 0), Place.getPlace(5, 0), Color.Black));
    }


    @Test
    public void pawnMovementTests() {
        pawnOneMoveTest();
        pawnTwoMoveTest();
        pawnEatingTest();
        promotionTest();
    }

    private void pawnOneMoveTest() {
        board = new TwoPlayerChessBoard(table4);
        assertTrue(board.isLegalMove(Place.getPlace(1,6),Place.getPlace(2,6),Color.Black));
        assertTrue(board.isLegalMove(Place.getPlace(7,6),Place.getPlace(6,6),Color.White));

        board = new TwoPlayerChessBoard(table3);
        assertFalse(board.isLegalMove(Place.getPlace(3,2),Place.getPlace(4,2),Color.Black));
        assertFalse(board.isLegalMove(Place.getPlace(4,2),Place.getPlace(3,2),Color.White));
    }

    private void pawnTwoMoveTest() {
        board = new TwoPlayerChessBoard(table4);
        assertTrue(board.isLegalMove(Place.getPlace(1,6),Place.getPlace(3,6),Color.Black));
        assertFalse(board.isLegalMove(Place.getPlace(3,6),Place.getPlace(5,6),Color.Black));
        assertTrue(board.isLegalMove(Place.getPlace(7,6),Place.getPlace(5,6),Color.White));
        assertFalse(board.isLegalMove(Place.getPlace(5,5),Place.getPlace(3,5),Color.White));

        board = new TwoPlayerChessBoard(table5);
        assertFalse(board.isLegalMove(Place.getPlace(1,6),Place.getPlace(3,6),Color.Black));
        assertFalse(board.isLegalMove(Place.getPlace(1,3),Place.getPlace(3,3),Color.Black));
    }

    private void promotionTest() {
        board = new TwoPlayerChessBoard(table3);
        assertTrue(board.isLegalMove(Place.getPlace(1,0),Place.getPlace(0,0),Color.White));
        board.moveAPiece(Place.getPlace(1,0),Place.getPlace(0,0),()->'Q');
        assertTrue(board.getPieceInPlace(Place.getPlace(0,0))instanceof Queen);
    }

    private void pawnEatingTest() {
        board = new TwoPlayerChessBoard(table3);
        assertFalse(board.isLegalMove(Place.getPlace(4,2),Place.getPlace(3,3),Color.White));
        assertFalse(board.isLegalMove(Place.getPlace(4,2),Place.getPlace(3,1),Color.White));
        assertFalse(board.isLegalMove(Place.getPlace(3,2),Place.getPlace(4,3),Color.Black));

        board = new TwoPlayerChessBoard(table10);
        assertFalse(board.isLegalMove(Place.getPlace(1,1),Place.getPlace(2,0),Color.Black));
        assertTrue(board.isLegalMove(Place.getPlace(2,1),Place.getPlace(1,0),Color.White));
    }

    @Test
    public void rookMovementTest() {
        board = new TwoPlayerChessBoard(table3);
        assertTrue(board.isLegalMove(Place.getPlace(2,0),Place.getPlace(2,6),Color.Black));
        assertTrue(board.isLegalMove(Place.getPlace(2,0),Place.getPlace(2,5),Color.Black));
        assertTrue(board.isLegalMove(Place.getPlace(2,0),Place.getPlace(3,0),Color.Black));
        assertFalse(board.isLegalMove(Place.getPlace(2,0),Place.getPlace(3,1),Color.Black));
        assertTrue(board.isLegalMove(Place.getPlace(5,0),Place.getPlace(5,2),Color.White));
        assertFalse(board.isLegalMove(Place.getPlace(5,0),Place.getPlace(5,7),Color.White));
    }

    @Test
    public void knightMovementTest() {
        board = new TwoPlayerChessBoard(table5);
        assertTrue(board.isLegalMove(Place.getPlace(4,0),Place.getPlace(3,2),Color.White));
        assertTrue(board.isLegalMove(Place.getPlace(4,0),Place.getPlace(5,1),Color.White));
        assertFalse(board.isLegalMove(Place.getPlace(4,0),Place.getPlace(6,1),Color.White));
    }

    @Test
    public void bishopMovementTests() {
        board = new TwoPlayerChessBoard(table2);
        assertTrue(board.isLegalMove(Place.getPlace(2,6),Place.getPlace(3,7),Color.White));
        assertTrue(board.isLegalMove(Place.getPlace(2,6),Place.getPlace(3,5),Color.White));
        assertTrue(board.isLegalMove(Place.getPlace(2,6),Place.getPlace(0,4),Color.White));
        assertFalse(board.isLegalMove(Place.getPlace(2,6),Place.getPlace(2,5),Color.White));
        assertTrue(board.isLegalMove(Place.getPlace(2,6),Place.getPlace(6,2),Color.White));
    }

    @Test
    public void kingMovementTests() {
        kingMovementTest();
        selfCheckTest();
        castlingTest();
    }

    private void kingMovementTest() {
        board = new TwoPlayerChessBoard(table1);
        assertTrue(board.isLegalMove(Place.getPlace(7,3),Place.getPlace(7,4),Color.White));
        assertTrue(board.isLegalMove(Place.getPlace(7,3),Place.getPlace(6,4),Color.White));
        assertFalse(board.isLegalMove(Place.getPlace(7,3),Place.getPlace(5,4),Color.White));
    }

    private void selfCheckTest() {
        board = new TwoPlayerChessBoard(table6);
        assertFalse(board.isLegalMove(Place.getPlace(1,1),Place.getPlace(1,0),Color.Black));
        board = new TwoPlayerChessBoard(table7);
        assertFalse(board.isLegalMove(Place.getPlace(0,3),Place.getPlace(1,4),Color.Black));
    }

    private void castlingTest() {
        board = new TwoPlayerChessBoard(table8);
        assertTrue(board.isLegalMove(Place.getPlace(0,4),Place.getPlace(0,7),Color.Black));
        assertTrue(board.isLegalMove(Place.getPlace(0,4),Place.getPlace(0,0),Color.Black));
        assertTrue(board.isLegalMove(Place.getPlace(7,4),Place.getPlace(7,0),Color.White));
        assertTrue(board.isLegalMove(Place.getPlace(7,4),Place.getPlace(7,7),Color.White));
        board.moveAPiece(Place.getPlace(7,4),Place.getPlace(7,5),()->'Q');
        assertFalse(board.isLegalMove(Place.getPlace(7,5),Place.getPlace(7,7),Color.White));
        board.moveAPiece(Place.getPlace(7,7),Place.getPlace(7,6),()->'Q');
        assertFalse(board.isLegalMove(Place.getPlace(0,4),Place.getPlace(0,7),Color.Black));

    }

    @Test
    public void queenMovementTests() {
        board = new TwoPlayerChessBoard(table9);
        assertFalse(board.isLegalMove(Place.getPlace(0,0),Place.getPlace(0,7),Color.White));
        assertFalse(board.isLegalMove(Place.getPlace(0,0),Place.getPlace(2,0),Color.White));
        assertFalse(board.isLegalMove(Place.getPlace(0,0),Place.getPlace(1,0),Color.White));
        assertTrue(board.isLegalMove(Place.getPlace(0,0),Place.getPlace(0,4),Color.White));
        assertTrue(board.isLegalMove(Place.getPlace(0,0),Place.getPlace(7,7),Color.White));
        assertTrue(board.isLegalMove(Place.getPlace(0,0),Place.getPlace(5,5),Color.White));
    }

}