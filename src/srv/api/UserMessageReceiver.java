package srv.api;

import Business.Color;

import java.util.Collection;

public interface UserMessageReceiver {
    <K> void receiveCollection(Collection<K> calculateMovingOptions,short opcode);

    void receiveMsg(String s);

    void receiveBoardAsString(String boardString);

    void gameFinishCallback();

    void receiveColor(Color color);
}
