package srv.Client;
import srv.NonBlockingConnectionHandler;
import srv.api.Messages.Message;

public class ClientMain {
    public static void main(String[] args) {
        new NonBlockingConnectionHandler<Message>()
    }
}
