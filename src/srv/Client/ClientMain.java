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
        int id;
        synchronized (lock){
            id = ClientMain.id++;
        }
        try (SocketChannel channel = SocketChannel.open(new InetSocketAddress(host,port))){
            ClientConnectionHandler<Message> clientConnectionHandler = new ClientConnectionHandler<Message>(new MessageEncoderDecoder(),new ClientProtocol(),channel,id,new ConnectionsImp<Message>());
            Thread thread1= new Thread(clientConnectionHandler::continueRead);
            Thread thread2 = new Thread(clientConnectionHandler::continueWrite);
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int generateRandom(){
        int min =0, max = 1000000;
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }
}
