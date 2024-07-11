import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class LocalRequestResolver implements RequestResolver {
    private final DatagramSocket socket;

    public LocalRequestResolver(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void resolve() throws IOException {
        System.out.println("Request assigned to " + this.getClass().getSimpleName());
        byte[] request = new byte[512];
        SocketAddress socketAddress = receive(request);

        DnsMessage dnsMessage = DnsMessage.from(request);

        final ByteBuffer byteBuffer = ByteBuffer.allocate(512)
                .order(ByteOrder.BIG_ENDIAN);
        dnsMessage.encode(byteBuffer);
        send(byteBuffer.array(), socketAddress);
    }

    @Override
    public DatagramSocket getSocket() {
        return this.socket;
    }
}
