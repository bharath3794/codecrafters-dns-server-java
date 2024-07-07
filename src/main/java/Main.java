import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    //
     try(DatagramSocket serverSocket = new DatagramSocket(2053)) {
       while(true) {
         final byte[] buf = new byte[512];
         final DatagramPacket packet = new DatagramPacket(buf, buf.length);
         serverSocket.receive(packet);
         System.out.println("Received data");

//           final BitSet bitSet = new BitSet(8);
//           bitSet.flip(7);
//           System.out.println(bitSet);
//           final byte[] bufResponse = ByteBuffer.allocate(512)
//                   .putShort((short)1234)
//                   .put(bitSet.toByteArray())
//                   .put((byte)0)
//                   .putShort((short)0)
//                   .putShort((short)0)
//                   .putShort((short)0)
//                   .putShort((short)0)
//                   .order(ByteOrder.BIG_ENDIAN)
//                   .array();

         Header header = new Header();
         header.messageId((short) 1234)
                 .queryResponse(true)
                 .opCode(OpCode.QUERY)
                 .authoritativeAnswer(false)
                 .truncation(false)
                 .recursionDesired(false)
                 .recursionAvailable(false)
                 .reserved1(false)
                 .reserved2(false)
                 .reserved3(false)
                 .rCode(RCode.NO_ERROR)
                 .qdCount(0)
                 .anCount(0)
                 .nsCount(0)
                 .arCount(0);

         final byte[] bufResponse = header.convertToByteArray(512);
         final DatagramPacket packetResponse = new DatagramPacket(bufResponse, bufResponse.length, packet.getSocketAddress());
         serverSocket.send(packetResponse);
       }
     } catch (IOException e) {
         System.out.println("IOException: " + e.getMessage());
     }
  }
}
