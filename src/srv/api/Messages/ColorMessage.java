package srv.api.Messages;

import Business.Color;
import srv.api.Messages.Message;

import java.util.List;

public class ColorMessage extends Message {
    public final static short OPCODE =6;

    private final Color color;

    public ColorMessage(List<Byte> bytes){
        super(OPCODE);
        StringBuilder tmp = new StringBuilder();
        for (Byte aByte : bytes) {
            char c = (char) aByte.shortValue();
            tmp.append(c);
        }
        String colorString = tmp.toString();
        this.color = Color.valueOf(colorString);
    }

    public ColorMessage(Color color) {
        super(OPCODE);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
