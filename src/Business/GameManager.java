package Business;

import Business.Boards.GameScore;
import Business.Boards.TwoPlayerChessBoard;
import Business.Interfaces.IOController;

import java.util.HashMap;
import java.util.Map;

/**
 * this class is responsible for the creation and the flow of the game
 */
public class GameManager {
    TwoPlayerChessBoard board;
    private final Map<Color, IOController> colorIOControllerMap = new HashMap<>();



    /**
     * @param whiteIoController an Io interface to communicate with the white player
     * @param blackIoController an Io interface to communicate with the black player
     */
    public GameManager(IOController whiteIoController, IOController blackIoController) {
        colorIOControllerMap.put(Color.White, whiteIoController);
        colorIOControllerMap.put(Color.Black, blackIoController);
    }

    public void startGame() {
         board = new TwoPlayerChessBoard();
        GameScore gameScore = gameLoop();
        finishGame(gameScore);
    }



    /**
     * A While loop that controls the game flaw
     * @return A game status instance which contains the winner and the reason of the winning
     */
    private GameScore gameLoop() {
        Color colorTurn = Color.White;
        while (true) {
            try {
                Place start = colorIOControllerMap.get(colorTurn).getPlace(MessagesLibrary.CHOOSE_A_PIECE);

                if (start.equals(Place.GIVE_UP_PLACE)) {
                    Color winner = Color.getOpponent(colorTurn,2).get(0);
                    return new GameScore(winner, GameScore.ReasonOfFinish.GiveUp);
                }

                Place finish = colorIOControllerMap.get(colorTurn).getPlace(MessagesLibrary.CHOOSE_Destination);

                // in case of regret (double tap on the same piece
                if (start.equals(finish))
                    continue;

                makeAMove(colorTurn, start, finish);
                colorIOControllerMap.get(colorTurn).presentMsg(MessagesLibrary.EMPTY_MESSAGE);


                // if the "other player" is in checkmate or pat
                if (board.isInCheckMate(Color.getOpponent(colorTurn,2).get(0))) {
                    return new GameScore(colorTurn, GameScore.ReasonOfFinish.CheckMate);
                }


                if (board.isInPat(Color.getOpponent(colorTurn,2).get(0))) {
                    return new GameScore(Color.getOpponent(colorTurn,2).get(0), GameScore.ReasonOfFinish.Pat);
                }

            } catch (RuntimeException e) {
                colorIOControllerMap.get(colorTurn).presentMsg(e.getMessage());
            }
        }
    }

    /**
     * this method should be called when a game is done
     */
    private void finishGame(GameScore gameScore) {
        Color opponent = Color.getOpponent(gameScore.winner,2).get(0);
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
    }

    /**
     * @param playerColor the player color who make the move
     * @param start       place of the piece you want to move
     * @param finish      where to move the piece
     */
    public void makeAMove(Color playerColor, Place start, Place finish) throws RuntimeException {


        if (!board.isLegalMove(start, finish, playerColor))
            throw new RuntimeException(MessagesLibrary.INVALID_CHOICE);

        board.moveAPiece(start, finish,()->'Q');
    }
}
