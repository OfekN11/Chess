package srv.api.Messages;

import java.util.*;

public class BoardContentMessage extends Message{
    private static final short OPCODE =4;
    private final String boardContent;

    public BoardContentMessage(List<Byte> bytes){
        super(OPCODE);
        StringBuilder tmp = new StringBuilder();
        for (int i=1;i<bytes.size();i++) {
            char c = (char) bytes.get(i).shortValue();
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
