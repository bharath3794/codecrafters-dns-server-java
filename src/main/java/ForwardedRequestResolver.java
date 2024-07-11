import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ForwardedRequestResolver implements RequestResolver {
    private final DatagramSocket socket;
    private final SocketAddress forwardAddress;


    public ForwardedRequestResolver(DatagramSocket socket, SocketAddress forwardAddress) {
        this.socket = socket;
        this.forwardAddress = forwardAddress;
    }


    @Override
    public void resolve() throws IOException {
        byte[] request = new byte[512];
        SocketAddress socketAddress = receive(request);

        DnsMessage dnsMessage = DnsMessage.from(request);
        for (DnsMessage message : dnsMessage.splitQuestions()) {
           final ByteBuffer byteBuffer = ByteBuffer.allocate(512)
                   .order(ByteOrder.BIG_ENDIAN);
           dnsMessage.encode(byteBuffer);
           send(byteBuffer.array(), forwardAddress);

           byte[] received = new byte[512];
           receive(received, forwardAddress);
           dnsMessage.answers().addAll(DnsMessage.from(received).answers());
        }

        final ByteBuffer byteBuffer = ByteBuffer.allocate(512)
                .order(ByteOrder.BIG_ENDIAN);
        dnsMessage.encode(byteBuffer);
       send(byteBuffer.array(), socketAddress);
    }

    @Override
    public DatagramSocket getSocket() {
        return socket;
    }
}
