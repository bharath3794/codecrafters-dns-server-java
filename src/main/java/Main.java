import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

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

         ByteBuffer inputByteBuffer = ByteBuffer.wrap(buf);

         Header header = Header.decodeHeader(inputByteBuffer);
         header.queryResponse(true)
                 .authoritativeAnswer(false)
                 .truncation(false)
                 .recursionAvailable(false)
                 .reserved1(false)
                 .reserved2(false)
                 .reserved3(false)
                 .rCode(header.getOpCode() == OpCode.QUERY ? RCode.NO_ERROR : RCode.NOT_IMPLEMENTED);

         String domainName = "codecrafters.io";
         Question question = new Question();
         question.qName(domainName)
                 .qType((short) 1)
                 .qClass((short) 1);
         header.qdCount((short) 1);

         Answer answer = new Answer();
         answer.anName(domainName)
                 .anType((short) 1)
                 .anClass((short) 1)
                 .anTtl(60)
                 .anRLength((short) 4)
                 .anRData(new byte[]{8,8,8,8});
         header.anCount((short) 1);

         final ByteBuffer byteBuffer = ByteBuffer.allocate(512)
                 .order(ByteOrder.BIG_ENDIAN);
         header.loadToByteBuffer(byteBuffer);
         question.loadToByteBuffer(byteBuffer);
         answer.loadToByteBuffer(byteBuffer);

         final byte[] bufResponse = byteBuffer.array();

         final DatagramPacket packetResponse = new DatagramPacket(bufResponse, bufResponse.length, packet.getSocketAddress());
         serverSocket.send(packetResponse);
       }
     } catch (IOException e) {
         System.out.println("IOException: " + e.getMessage());
     }
  }
}
