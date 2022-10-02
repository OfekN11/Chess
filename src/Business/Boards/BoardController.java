package Business.Boards;


import Business.GameManager;
import Business.Interfaces.IOController;


/**
 * this class will have use when a server would be built
 */
public class BoardController {
    static IOController whiteIOController;
    public synchronized GameManager startGame() {
        return new GameManager(new TwoPlayerChessBoard());
    }
}
