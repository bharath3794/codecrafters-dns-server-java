import java.io.IOException;
import java.net.SocketAddress;

public interface RequestReceiver {
    SocketAddress receive(byte[] buffer) throws IOException;
    void receive(byte[] buffer, SocketAddress address) throws IOException;
}
