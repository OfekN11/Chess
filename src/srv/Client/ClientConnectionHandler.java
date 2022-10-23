package srv.Client;

import srv.ConnectionHandler;
import srv.api.Messages.StringMessage;
import srv.api.Protocol;
import srv.api.ConnectionsImp;
import srv.api.MessageEncoderDecoderInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientConnectionHandler<T> implements ConnectionHandler<T> {

    private static final int BUFFER_ALLOCATION_SIZE = 1 << 13; //8k
    private static final ConcurrentLinkedQueue<ByteBuffer> BUFFER_POOL = new ConcurrentLinkedQueue<>();

    private final Protocol<T> protocol;
    private final MessageEncoderDecoderInterface<T> encdec;
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    private final SocketChannel chan;

    public ClientConnectionHandler(
            MessageEncoderDecoderInterface<T> reader,
            Protocol<T> protocol,
            SocketChannel chan,
            int connectionId,
            ConnectionsImp<T> connections) throws IOException {
        this.chan = chan;
        chan.configureBlocking(true);
        this.encdec = reader;
        this.protocol = protocol;
        protocol.start(connectionId, connections);
        connections.register(connectionId, this);
    }

    public void continueRead() {
        while (true) {
            ByteBuffer buf = leaseBuffer();

            boolean success = false;
            try {
                success = chan.read(buf) != -1;
            } catch (IOException ex) {
                ex.printStackTrace();
                close();
                break;
            }

            if (success) {
                buf.flip();
                try {
                    while (buf.hasRemaining()) {
                        T nextMessage = encdec.decodeNextByte(buf.get());
                        if (nextMessage != null) {
                            protocol.process(nextMessage);
                        }
                    }
                } finally {
                    releaseBuffer(buf);
                }

            } else {
                releaseBuffer(buf);
            }
            if (protocol.shouldTerminate()) {
                close();
                break;
            }
        }
    }

    public void close() {
        try {
            chan.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isClosed() {
        return !chan.isOpen();
    }

    public void continueWrite() {
            if (!writeQueue.isEmpty()) {
                try {
                    System.out.println("client connection handler continue write has a massage");
                    ByteBuffer top = writeQueue.peek();
                    chan.write(top);
                    if (top.hasRemaining()) {
                        return;
                    } else {
                        synchronized (writeQueue) {
                            writeQueue.remove();
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    close();
                }
            }


            if (protocol.shouldTerminate()) {
                close();
            }
    }

    private static ByteBuffer leaseBuffer() {
        ByteBuffer buff = BUFFER_POOL.poll();
        if (buff == null) {
            return ByteBuffer.allocateDirect(BUFFER_ALLOCATION_SIZE);
        }
        buff.clear();
        return buff;
    }

    private static void releaseBuffer(ByteBuffer buff) {
        BUFFER_POOL.add(buff);
    }

    @Override
    public void send(T msg) {
        synchronized (writeQueue) {
            writeQueue.add(ByteBuffer.wrap(encdec.encode(msg)));
            continueWrite();
        }
    }

    public void start() throws InterruptedException {
        System.out.println("got to start");
        send((T) new StringMessage("Start"));
        Thread thread1= new Thread(this::continueRead);
        thread1.start();
        thread1.join();
    }
}
