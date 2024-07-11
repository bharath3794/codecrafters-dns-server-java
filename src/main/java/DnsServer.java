import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class DnsServer implements Server {
    public static final int DEFAULT_PORT = 2053;
    private final int port;
    private DatagramSocket socket;
    private SocketAddress forwardServerAddress = null;

    public DnsServer() {
        this.port = DnsServer.DEFAULT_PORT;
    }

    public DnsServer(int port) {
        this.port = port;
    }

    public DnsServer(int port, String forwardServerAddress) {
        this.port = port;
        this.forwardServerAddress = parseSocketAddress(forwardServerAddress);
    }

    private SocketAddress parseSocketAddress(String socketAddress) {
        if (!socketAddress.contains(":") || socketAddress.substring(socketAddress.indexOf(":")).length()==1) {
            throw new IllegalArgumentException("Invalid socket address");
        }
        return new InetSocketAddress(socketAddress.substring(0, socketAddress.indexOf(':')), Integer.parseInt(socketAddress.substring(socketAddress.indexOf(":")+1)));
    }

    private RequestResolver createRequestResolver() {
        if (socket == null) {
            throw new IllegalStateException("Socket is not initialized");
        }
        if (forwardServerAddress == null) {
            return new LocalRequestResolver(socket);
        }
        return new ForwardedRequestResolver(socket, forwardServerAddress);
    }

    @Override
    public void start() {
        try {
            this.socket = new DatagramSocket(port);
            RequestResolver requestResolver = createRequestResolver();
            while(!socket.isClosed()) {
                requestResolver.resolve();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        if (!socket.isClosed()) {
            socket.close();
        }
    }
}
