package srv.api.Messages;

import java.util.*;

public class BoardContentMessage extends Message{
    private static final short OPCODE =4;
    private final String boardContent;

    public BoardContentMessage(List<Byte> bytes){
        super(OPCODE);
        StringBuilder tmp = new StringBuilder();
        for (Byte aByte : bytes) {
            char c = (char) aByte.shortValue();
            tmp.append(c);
        }
        boardContent = tmp.toString();
    }

    public BoardContentMessage(String boardContent) {
        super(OPCODE);
        this.boardContent =boardContent;
    }

    public String getBoardContent() {
        return boardContent;
    }
}
