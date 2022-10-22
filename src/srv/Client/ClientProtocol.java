package srv.Client;

import Business.Controllers.BoardController;
import Business.GameManager;
import Presentation.ClickListener;
import Presentation.TableGui;
import srv.api.Connections;
import srv.api.Messages.*;
import srv.api.Protocol;

import java.util.function.Supplier;

public class ClientProtocol implements Protocol<Message> {
    private boolean shouldTerminate;
    private TableGui tableGui;

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        ClickListener sendPlace = (place) -> connections.send(connectionId,new PlaceMessage(place));
        tableGui = new TableGui(sendPlace);
        shouldTerminate = false;
    }

    @Override
    public void process(Message message) {
        short op = message.getOpcode();
        switch (op) {

            //2) String message
            case 2:
                StringMessage msg = (StringMessage) message;
                System.out.println(msg);
                shouldTerminate =true;
                break;

                //3) PlacesMessage
            case 3:
                PlacesMessage placesMessage = (PlacesMessage) message;
                tableGui.setPossibleDestinationsForChosenPiece(placesMessage.getCollection());
                break;

                // Board change look message
            case 4:
                BoardContentMessage boardContentMessage = (BoardContentMessage) message;
                tableGui.setBoardAsString(boardContentMessage.getBoardContent());
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
