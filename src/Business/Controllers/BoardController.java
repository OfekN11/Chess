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
        // gameManager != null means that 2 player has entered, and I want to get  back to the thread which is in wait()
        while (BoardController.player1 != null && gameManager != null) {
            wait();
        }

        // true if he is the first player
        if (BoardController.player1 == null) {
            BoardController.player1 = userMessageReceiver;
            wait();
            GameManager output = gameManager;
            resetVariables();
            notifyAll();
            return output;

        } else { // second player
            BoardController.gameManager = new GameManager(new TwoPlayerChessBoard(), BoardController.player1, userMessageReceiver);
            notifyAll();
            return gameManager;
        }

    }

    private void resetVariables() {
        gameManager = null;
        player1 = null;
    }
}
