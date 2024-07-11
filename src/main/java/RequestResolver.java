import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;

public interface RequestResolver extends RequestSender, RequestReceiver {
    void resolve() throws IOException;
    DatagramSocket getSocket();

    @Override
    default SocketAddress receive(byte[] buffer) throws IOException {
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        getSocket().receive(packet);
//        System.out.println("Received data");
//
//        System.out.println("Read Header Section from Received Packet: " + Arrays.toString(Arrays.copyOfRange(packet.getData(), 0, 12)));
//
//        System.out.println("Read Question Section from Received Packet: " + Arrays.toString(Arrays.copyOfRange(packet.getData(), 12, packet.getLength())));
        return packet.getSocketAddress();
    }

    @Override
    default void receive(byte[] buffer, SocketAddress address) throws IOException {
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address);
        getSocket().receive(packet);
    }

    @Override
    default void send(byte[] request, SocketAddress address) throws IOException {
        final DatagramPacket packetResponse = new DatagramPacket(request, request.length, address);
        getSocket().send(packetResponse);
    }
}
