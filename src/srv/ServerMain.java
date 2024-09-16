package srv;


import srv.api.ConnectionsImp;
import srv.api.MessageEncoderDecoder;
import srv.api.Messages.Message;
import srv.api.ServerProtocol;

public class ServerMain {

    public static void main(String[] args) {

        int numOfThreads = 6;
        Server.reactor(
                numOfThreads,
                8080,
                () -> new ServerProtocol(),
                () -> new MessageEncoderDecoder() {
                },
                new ConnectionsImp<Message>()
        ).serve();
    }

}

