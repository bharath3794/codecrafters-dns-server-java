import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Question {
    private String qName;
    private short qType;
    private short qClass;

    public String getqName() {
        return qName;
    }

    public Question qName(String qName) {
        this.qName = qName;
        return this;
    }

    public short getqType() {
        return qType;
    }

    public Question qType(short qType) {
        this.qType = qType;
        return this;
    }

    public short getqClass() {
        return qClass;
    }

    public Question qClass(short qClass) {
        this.qClass = qClass;
        return this;
    }

    public static byte[] encodedDomainName(String domainName) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
            for (String s: domainName.split("\\.")) {
                byteArrayOutputStream.write(s.length());
                byteArrayOutputStream.write(s.getBytes());
            }
            byteArrayOutputStream.write(0);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadToByteBuffer(ByteBuffer buffer) {
        buffer.put(Question.encodedDomainName(this.getqName()))
                .putShort(this.getqType())
                .putShort(this.getqClass());
    }
}
