package Business.Interfaces;

import Business.Place;

public interface IOController {
    public Place getPlace(String msgToPresent);
    public void presentMsg(String msg);

    char getPromotionChar();
}
