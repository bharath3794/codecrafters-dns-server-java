import java.io.IOException;
import java.net.SocketAddress;

public interface RequestSender {
    void send(byte[] request, SocketAddress address) throws IOException;
}
