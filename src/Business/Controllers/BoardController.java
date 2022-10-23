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
            System.out.println("11");
            BoardController.player1 = userMessageReceiver;
            userMessageReceiver.receiveMsg("Waiting for opponent");
            System.out.println("22");
            return gameManager;
        } else { // second player
            gameManager.start(userMessageReceiver);
            System.out.println("yep");
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
