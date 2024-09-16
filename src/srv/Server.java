package srv;


import srv.api.Protocol;
import srv.api.ConnectionsImp;
import srv.api.MessageEncoderDecoderInterface;

import java.io.Closeable;
import java.util.function.Supplier;

public interface Server<T> extends Closeable {

    /**
     * The main loop of the server, Starts listening and handling new clients.
     */
    void serve();


    /**
     * This function returns a new instance of a reactor pattern server
     * @param nThreads Number of threads available for protocol processing
     * @param port The port for the server socket
     * @param protocolFactory A factory that creats new MessagingProtocols
     * @param encoderDecoderFactory A factory that creats new MessageEncoderDecoder
     * @param <T> The Message Object for the protocol
     * @return A new reactor server
     */
    public static <T> Server<T> reactor(
            int nThreads,
            int port,
            Supplier<Protocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoderInterface<T>> encoderDecoderFactory,
            ConnectionsImp<T> connections) {
        return new Reactor<T>(nThreads, port, protocolFactory, encoderDecoderFactory, connections);
    }

}
