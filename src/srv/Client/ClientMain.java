package srv.Client;
import srv.api.ConnectionsImp;
import srv.api.MessageEncoderDecoder;
import srv.api.Messages.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientMain {
    static Object lock = new Object();
    static int id =0;
    public static void main(String[] args) {
        String host ="localhost";
        int port = 7777;
        try (SocketChannel channel = SocketChannel.open(new InetSocketAddress(host,port))){
            ClientConnectionHandler<Message> clientConnectionHandler = new ClientConnectionHandler<Message>(new MessageEncoderDecoder(),new ClientProtocol(),channel,generateRandom(),new ConnectionsImp<Message>());
            clientConnectionHandler.start();


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int generateRandom(){
        int min =100, max = 1000000;
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }
}
