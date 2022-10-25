package srv.api.Messages;

public class FinishGameMessage extends Message {
    public static final short OPCODE = 5;
    public FinishGameMessage(){
        super(OPCODE);
    }
}
