package srv.Client;

import Presentation.ClickListener;
import Presentation.TableGui;
import srv.api.Connections;
import srv.api.Messages.*;
import srv.api.Protocol;

import java.util.Collections;

import static Business.Color.Black;

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
                System.out.println(msg.getMsg());
                break;

            //3) PlacesMessage
            case 3:
                PlacesMessage placesMessage = (PlacesMessage) message;
                tableGui.setPossibleDestinationsForChosenPiece(placesMessage.getCollection());
                tableGui.reprint();
                break;

            // Board change look message
            case 4:
                BoardContentMessage boardContentMessage = (BoardContentMessage) message;
                tableGui.setBoardAsString(boardContentMessage.getBoardContent());
                tableGui.setPossibleDestinationsForChosenPiece(Collections.emptyList());
                tableGui.reprint();
                break;

            case 5: // finish game message
                System.exit(0);
                break;

            case 6: // color message
                ColorMessage colorMessage = (ColorMessage) message;
                if (colorMessage.getColor() ==Black) {
                    System.out.println("client protocol received color black");
                    tableGui.reverseTable();
                }
                break;
        }

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
