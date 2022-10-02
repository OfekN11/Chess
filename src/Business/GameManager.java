package Business;

import Business.Boards.GameScore;
import Business.Boards.TwoPlayerChessBoard;
import Business.ChessPieces.ChessPiece;
import Business.Interfaces.IOController;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

/**
 * this class is responsible for the creation and the flow of the game
 */
public class GameManager {
    private TwoPlayerChessBoard board;
    private Place src;
    private Color colorTurn;
    private ChessPiece chosenPiece;


    public GameManager(TwoPlayerChessBoard board) {
        this.board = board;
        this.src = null;
        this.colorTurn = Color.White;
    }


    public Collection<Place> enteredInput(Place chosenPlace) {
        if (chosenPlace == src) { // double click to cancel
            resetVariables();
            return Collections.emptyList();
        }

        try {
            if (src == null) {
                src = chosenPlace;
                chosenPiece = board.getPieceInPlace(chosenPlace);
                if (chosenPiece == null || chosenPiece.getColor() != colorTurn)
                    resetVariables();
                else
                    return board.calculateMovingOptions(src);
            } else {
                //second click
                if (board.isLegalMove(src, chosenPlace, colorTurn)) {
                    board.moveAPiece(src, chosenPlace, () -> 'Q'); // that's a big bug, but I do not perfect with the Gui
                    colorTurn = Color.getOpponent(colorTurn, 2).get(0);

                    if (board.isInCheckMate(colorTurn)) {
                        System.out.println(Color.getOpponent(colorTurn, 2).get(0) + " has won");
                        System.exit(0);
                    } else if (board.isInPat(colorTurn)) {
                        System.out.println("its a tie!");
                        System.exit(0);
                    }
                }
                resetVariables();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private void resetVariables() {
        this.src = null;
        this.chosenPiece = null;
    }


    public String getStringRepresentationOfPieceInPlace(Place piecePlace) {
        return board.getStringRepresentationOfPieceInPlace(piecePlace);
    }



        /**
         * this method should be called when a game is done
         */
 /*   private void finishGame(GameScore gameScore) {
        Color opponent = Color.getOpponent(gameScore.winner, 2).get(0);
        switch (gameScore.reason) {
            case GiveUp -> {
                colorIOControllerMap.get(gameScore.winner).presentMsg(String.format(MessagesLibrary.WIN_MASSAGE, "Your opponent has" + MessagesLibrary.GAVE_UP_MASSAGE));
                colorIOControllerMap.get(opponent).presentMsg(String.format(MessagesLibrary.LOSE_MASSAGE, "You" + MessagesLibrary.GAVE_UP_MASSAGE));
            }
            case CheckMate -> {
                colorIOControllerMap.get(gameScore.winner).presentMsg(String.format(MessagesLibrary.WIN_MASSAGE, MessagesLibrary.CHECKMATE_MASSAGE));
                colorIOControllerMap.get(opponent).presentMsg(String.format(MessagesLibrary.LOSE_MASSAGE, MessagesLibrary.CHECKMATE_MASSAGE));
            }
            case Pat -> {
                colorIOControllerMap.get(gameScore.winner).presentMsg(MessagesLibrary.PAT_MASSAGE);
                colorIOControllerMap.get(opponent).presentMsg(MessagesLibrary.PAT_MASSAGE);
            }
        }
        //finishCallBack.call();
    }*/

    /**
     * @param playerColor the player color who make the move
     * @param start       place of the piece you want to move
     * @param finish      where to move the piece
     */
    /*public void makeAMove(Color playerColor, Place start, Place finish) throws RuntimeException {


        if (!board.isLegalMove(start, finish, playerColor))
            throw new RuntimeException(MessagesLibrary.INVALID_CHOICE);

        board.moveAPiece(start, finish, () -> 'Q');
    }*/
}
