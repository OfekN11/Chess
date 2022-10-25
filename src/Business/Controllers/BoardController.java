package Business.Controllers;


import Business.Boards.TwoPlayerChessBoard;
import Business.GameManager;
import srv.api.UserMessageReceiver;


/**
 * this class will have use when a server would be built
 */
public class BoardController {

    private static UserMessageReceiver player1;
    private static GameManager gameManager;

    public synchronized GameManager startGame(UserMessageReceiver userMessageReceiver) throws InterruptedException {

        // true if he is the first player
        if (BoardController.player1 == null) {
            gameManager = new GameManager(new TwoPlayerChessBoard(),userMessageReceiver);
            BoardController.player1 = userMessageReceiver;
            userMessageReceiver.receiveMsg("Waiting for opponent");
            return gameManager;
        } else { // second player
            gameManager.start(userMessageReceiver);
            GameManager output =gameManager;
            resetVariables();

            return output;
        }

    }

    private void resetVariables() {
        gameManager = null;
        player1 = null;
    }
}
