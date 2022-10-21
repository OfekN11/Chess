package srv.api.Messages;

import Business.Place;

import java.util.List;

public class PlaceMessage extends Message{
    private static final short OPCODE =1;
    private final Place place;

    public PlaceMessage(List<Byte> bytes){
        super(OPCODE);
        char row = (char) bytes.get(0).shortValue();
        char column = (char) bytes.get(2).shortValue();
        place =Place.getPlace(row-'0',column-'0');
    }

    public Place getPlace() {
        return place;
    }
}
