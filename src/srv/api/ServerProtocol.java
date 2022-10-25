package srv.api;

import Business.Color;
import Business.Controllers.BoardController;
import Business.GameManager;
import Business.Place;
import srv.api.Messages.*;

import java.util.Collection;
import java.util.function.Supplier;

public class ServerProtocol implements Protocol<Message>, UserMessageReceiver {
    private boolean shouldTerminate;
    private Connections<Message> connections;
    private int handlerConnectionId;
    private Supplier<GameManager> gameManagerSupplier;
    private GameManager gameManager;
    private BoardController boardController;

    @Override
    public void start(int connectionId, Connections<Message> connections) {
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
    }

    @Override
    public void process(Message message) {
        short op = message.getOpcode();
        switch (op) {

            //1) PlaceMsg
            case (1):
                PlaceMessage msg = (PlaceMessage) message;
                if (gameManager != null)
                    gameManager.userClick(msg.getPlace(),this);
                break;

                //string message
            case 2:
                StringMessage message1 = (StringMessage) message;
                if (message1.getMsg().equals("Start")) {
                    gameManager = gameManagerSupplier.get();
                }
                break;
        }
    }


    @Override
    public boolean shouldTerminate() {
        return false;
    }

    @Override
    public <K> void receiveCollection(Collection<K> collection,short opcode) {
        if (opcode == 3)
            connections.send(handlerConnectionId, new PlacesMessage((Collection<Place>)collection));
    }

    @Override
    public void receiveMsg(String msg) {
        connections.send(handlerConnectionId, new StringMessage(msg));
    }

    @Override
    public void receiveBoardAsString(String boardString) {
        connections.send(handlerConnectionId, new BoardContentMessage(boardString));
    }

    @Override
    public void gameFinishCallback() {
        connections.send(handlerConnectionId,new FinishGameMessage());
    }

    @Override
    public void receiveColor(Color color) {
        connections.send(handlerConnectionId,new ColorMessage(color));
    }
}
