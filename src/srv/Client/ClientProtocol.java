package srv.Client;

import Business.Controllers.BoardController;
import Business.GameManager;
import Presentation.TableGui;
import srv.api.Connections;
import srv.api.Messages.Message;
import srv.api.Protocol;

import java.util.function.Supplier;

public class ClientProtocol implements Protocol<Message> {
    private boolean shouldTerminate;
    private Connections<Message> connections;
    private int handlerConnectionId;
    private Supplier<GameManager> gameManagerSupplier;
    private GameManager gameManager;
    private BoardController boardController;

    private TableGui tableGui;

    public ClientProtocol(Supplier<String> boardStringSupplier){

    }
    @Override
    public void start(int connectionId, Connections<Message> connections) {
        tableGui = new TableGui();
        this.connections = connections;
        handlerConnectionId = connectionId;
        shouldTerminate = false;
        this.boardController = new BoardController();
        this.gameManagerSupplier = ()-> {
            try {
                return boardController.startGame(this);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        gameManager = gameManagerSupplier.get();
    }

    @Override
    public void process(Message message) {

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
