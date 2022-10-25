package srv.api.Messages;

import Business.Place;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlacesMessage extends Message implements CollectionMessage<Place>{
    private static final short OPCODE =3;
    private final Collection<Place> places;

    public PlacesMessage(List<Byte> bytes){
        super(OPCODE);
        places = new HashSet<>();
        for (int i = 0; i< bytes.size();i++){
            char row = (char) bytes.get(i++).shortValue();
            i++;
            char column = (char) bytes.get(i++).shortValue();
            places.add(Place.getPlace(row-'0',column-'0'));
        }
    }

    public PlacesMessage(Collection<Place> places){
        super(OPCODE);
        this.places = places;
    }

    public Collection<Place> getPlace() {
        return places;
    }

    @Override
    public Collection<Place> getCollection() {
        return places;
    }
}
