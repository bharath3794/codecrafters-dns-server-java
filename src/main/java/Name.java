import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class Name {
    private String qName;
    private short qType;
    private short qClass;

    public String getqName() {
        return qName;
    }

    public Name qName(String qName) {
        this.qName = qName;
        return this;
    }

    public short getqType() {
        return qType;
    }

    public Name qType(short qType) {
        this.qType = qType;
        return this;
    }

    public short getqClass() {
        return qClass;
    }

    public Name qClass(short qClass) {
        this.qClass = qClass;
        return this;
    }

    private byte[] encodedDomainName() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
            for (String s: this.getqName().split("\\.")) {
                byteArrayOutputStream.write(s.length());
                byteArrayOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
            }
            byteArrayOutputStream.write(0);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadToByteBuffer(ByteBuffer buffer) {
        buffer.put(this.encodedDomainName())
                .putShort(this.getqType())
                .putShort(this.getqClass())
                .order(ByteOrder.BIG_ENDIAN);
    }
}
