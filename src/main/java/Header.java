import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public class Header {
    private short messageId;
    private boolean queryResponse;
    private OpCode opCode;
    private boolean authoritativeAnswer;
    private boolean truncation;
    private boolean recursionDesired;
    private boolean recursionAvailable;
    private boolean reserved1;
    private boolean reserved2;
    private boolean reserved3;
    private RCode rCode;
    private int qdCount;
    private int anCount;
    private int nsCount;
    private int arCount;

    public short getMessageId() {
        return messageId;
    }

    public Header messageId(short messageId) {
        this.messageId = messageId;
        return this;
    }

    public boolean isQueryResponse() {
        return queryResponse;
    }

    public Header queryResponse(boolean queryResponse) {
        this.queryResponse = queryResponse;
        return this;
    }

    public OpCode getOpCode() {
        return opCode;
    }

    public Header opCode(OpCode opCode) {
        this.opCode = opCode;
        return this;
    }

    public boolean isAuthoritativeAnswer() {
        return authoritativeAnswer;
    }

    public Header authoritativeAnswer(boolean authoritativeAnswer) {
        this.authoritativeAnswer = authoritativeAnswer;
        return this;
    }

    public boolean isTruncation() {
        return truncation;
    }

    public Header truncation(boolean truncation) {
        this.truncation = truncation;
        return this;
    }

    public boolean isRecursionDesired() {
        return recursionDesired;
    }

    public Header recursionDesired(boolean recursionDesired) {
        this.recursionDesired = recursionDesired;
        return this;
    }

    public boolean isRecursionAvailable() {
        return recursionAvailable;
    }

    public Header recursionAvailable(boolean recursionAvailable) {
        this.recursionAvailable = recursionAvailable;
        return this;
    }

    public boolean isReserved3() {
        return reserved3;
    }

    public Header reserved3(boolean reserved3) {
        this.reserved3 = reserved3;
        return this;
    }

    public boolean isReserved2() {
        return reserved2;
    }

    public Header reserved2(boolean reserved2) {
        this.reserved2 = reserved2;
        return this;
    }

    public boolean isReserved1() {
        return reserved1;
    }

    public Header reserved1(boolean reserved1) {
        this.reserved1 = reserved1;
        return this;
    }

    public RCode getrCode() {
        return rCode;
    }

    public Header rCode(RCode rCode) {
        this.rCode = rCode;
        return this;
    }

    public int getQdCount() {
        return qdCount;
    }

    public Header qdCount(int qdCount) {
        this.qdCount = qdCount & 0xFFFF;
        return this;
    }

    public int getAnCount() {
        return anCount & 0xFFFF;
    }

    public Header anCount(int anCount) {
        this.anCount = anCount & 0xFFFF;
        return this;
    }

    public int getNsCount() {
        return nsCount;
    }

    public Header nsCount(int nsCount) {
        this.nsCount = nsCount & 0xFFFF;
        return this;
    }

    public int getArCount() {
        return arCount;
    }

    public Header arCount(int arCount) {
        this.arCount = arCount & 0xFFFF;
        return this;
    }

    private byte[] getByteArray() {
        BitSet bitSet = new BitSet( 16);
        if (this.isQueryResponse())
            bitSet.flip(15);
        int index = 11;
        for (char c: Integer.toBinaryString(opCode.getCode()).toCharArray()) {
            if (c=='1')
                bitSet.set(index);
            index++;
        }
        if (this.isAuthoritativeAnswer())
            bitSet.set(10);
        if (this.isTruncation())
            bitSet.set(9);
        if (this.isRecursionDesired())
            bitSet.set(8);
        if (this.isRecursionAvailable())
            bitSet.set(7);
        if (this.isReserved1())
            bitSet.set(6);
        if (this.isReserved2())
            bitSet.set(5);
        if (this.isReserved3())
            bitSet.set(4);

//        index = 0;
//        for (char c: Integer.toBinaryString(rCode.getCode()).toCharArray()) {
//            if (c=='1')
//                bitSet.set(index);
//            index++;
//        }
        return bitSet.toByteArray();
    }

    public byte[] convertToByteArray(int capacity) {
        final byte[] bufResponse = ByteBuffer.allocate(capacity)
                .putShort(this.getMessageId())
                .put(this.getByteArray())
                .putShort((short) this.getQdCount())
                .putShort((short) this.getAnCount())
                .putShort((short) this.getNsCount())
                .putShort((short) this.getArCount())
                .order(ByteOrder.BIG_ENDIAN)
                .array();
        return bufResponse;
    }
}
