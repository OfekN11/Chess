package srv.api;

import java.util.Collection;

public interface UserMessageReceiver {
    <K> void receiveCollection(Collection<K> calculateMovingOptions,short opcode);

    void receiveMsg(String s);
}
