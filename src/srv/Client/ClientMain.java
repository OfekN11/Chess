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
        String host ="192.168.161.98";
        int port = 8080;
        try (SocketChannel channel = SocketChannel.open(new InetSocketAddress(host,port))){
            ClientConnectionHandler<Message> clientConnectionHandler = new ClientConnectionHandler<Message>(new MessageEncoderDecoder(),new ClientProtocol(),channel,generateRandom(),new ConnectionsImp<Message>());
            clientConnectionHandler.start();


        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException ignored){
            // there is a exception that awt throws sometimes that i have no idea why.
            // but it is not interrupting the flaw of the program
        }
        int[] input =new int[4];
        int result =0;

        int whereToPutNextDuplicate =1; //start from one because the first element cannot be a duplicate
        for(int i=1;i<input.length;i++){ //loop starts from one because the first element cannot be a duplicate
            if(input[i]==input[i-1]){
                result =result +1;
            }
            else{ //that is one we encounter new element
                input[whereToPutNextDuplicate] = input[i];
                whereToPutNextDuplicate = whereToPutNextDuplicate +1;
            }
        }
    }

    public static int generateRandom(){
        int min =100, max = 1000000;
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }
}
