package srv.api.Messages;

import java.util.*;

public class StringMessage extends Message{
    private static final short OPCODE =2;
    private final String msg;

    public StringMessage(List<Byte> bytes){
        super(OPCODE);
        StringBuilder tmp = new StringBuilder();
        for (int i=1;i<bytes.size();i++) {
            char c = (char) bytes.get(i).shortValue();
            tmp.append(c);
        }
        msg = tmp.toString();
    }

    public StringMessage(String msg) {
        super(OPCODE);
        this.msg =msg;
    }

    public String getMsg() {
        return msg;
    }
}
