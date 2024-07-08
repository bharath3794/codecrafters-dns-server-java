import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Answer {
    private String anName;
    private short anType;
    private short anClass;
    private int anTtl;
    private short anRLength;
    private byte[] anRData;

    public String getAnName() {
        return anName;
    }

    public Answer anName(String anName) {
        this.anName = anName;
        return this;
    }

    public short getAnType() {
        return anType;
    }

    public Answer anType(short anType) {
        this.anType = anType;
        return this;
    }

    public short getAnClass() {
        return anClass;
    }

    public Answer anClass(short anClass) {
        this.anClass = anClass;
        return this;
    }

    public int getAnTtl() {
        return anTtl;
    }

    public Answer anTtl(int anTtl) {
        this.anTtl = anTtl;
        return this;
    }

    public short getAnRLength() {
        return anRLength;
    }

    public Answer anRLength(short anRLength) {
        this.anRLength = anRLength;
        return this;
    }

    public byte[] getAnRData() {
        return anRData;
    }

    public Answer anRData(byte[] anRData) {
        if (anRData.length != 4)
            throw new IllegalArgumentException("IPAddress should be 4 bytes of length");
        this.anRData = anRData;
        return this;
    }

    public static byte[] encodedAnRData(byte[] anRData) {
        if (anRData.length != 4) {
            throw new IllegalArgumentException("IPAddress should be 4 bytes of length");
        }
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
            for (byte b: anRData) {
                byteArrayOutputStream.write(b);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadToByteBuffer(ByteBuffer buffer) {
        buffer.put(Question.encodedDomainName(this.getAnName()))
                .putShort(this.getAnType())
                .putShort(this.getAnClass())
                .putInt(this.getAnTtl())
                .putShort(this.getAnRLength())
                .put(Answer.encodedAnRData(this.getAnRData()));
    }
}
