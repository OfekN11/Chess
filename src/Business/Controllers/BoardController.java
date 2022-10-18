package Business.Controllers;


import Business.Boards.TwoPlayerChessBoard;
import Business.GameManager;


/**
 * this class will have use when a server would be built
 */
public class BoardController {
    public GameManager startGame() {
        return new GameManager(new TwoPlayerChessBoard());
    }
}
