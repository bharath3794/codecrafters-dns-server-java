import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLOutput;
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
        System.out.println("Request assigned to " + this.getClass().getSimpleName());
        byte[] request = new byte[512];
        SocketAddress socketAddress = receive(request);

        DnsMessage dnsMessage = DnsMessage.from(request);
        Short qdCount = dnsMessage.header().getQdCount();
        dnsMessage.answers().clear();
        List<Answer> answers = new ArrayList<>();
        for (DnsMessage message : dnsMessage.splitQuestions()) {
            message.header().queryResponse(false).qdCount((short) 1);
            message.answers().clear();
           final ByteBuffer byteBuffer = ByteBuffer.allocate(512)
                   .order(ByteOrder.BIG_ENDIAN);
           message.encode(byteBuffer);
            System.out.println("Sending to forward address");
           send(byteBuffer.array(), forwardAddress);

           byte[] received = new byte[512];
           receive(received, forwardAddress);
            System.out.println("Received from forward address");
            answers.addAll(DnsMessage.from(received).answers());
        }

        final ByteBuffer byteBuffer = ByteBuffer.allocate(512)
                .order(ByteOrder.BIG_ENDIAN);

        Header requestHeader = dnsMessage.header();
        requestHeader.queryResponse(true).qdCount(qdCount).anCount(qdCount);
        new DnsMessage(requestHeader, dnsMessage.questions(), answers).encode(byteBuffer);
        System.out.println("Sending to Local");
        send(byteBuffer.array(), socketAddress);
    }

    @Override
    public DatagramSocket getSocket() {
        return this.socket;
    }
}
