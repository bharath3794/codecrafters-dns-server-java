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
    private short qdCount;
    private short anCount;
    private short nsCount;
    private short arCount;

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

    public short getQdCount() {
        return qdCount;
    }

    public Header qdCount(short qdCount) {
        this.qdCount = qdCount;
        return this;
    }

    public short getAnCount() {
        return anCount;
    }

    public Header anCount(short anCount) {
        this.anCount = anCount;
        return this;
    }

    public short getNsCount() {
        return nsCount;
    }

    public Header nsCount(short nsCount) {
        this.nsCount = nsCount;
        return this;
    }

    public short getArCount() {
        return arCount;
    }

    public Header arCount(short arCount) {
        this.arCount = arCount;
        return this;
    }

    private short getFlags() {
        int flags = 0b0_0000_0_0_0_0_0_0_0_0000;
        if (this.isQueryResponse())
            flags |= 0b1_0000_0_0_0_0_0_0_0_0000;
        String opCodeBits = Integer.toBinaryString(opCode.getCode());
        opCodeBits = Util.prePaddedString(opCodeBits, '0', 5);
        opCodeBits = Util.postPaddedString(opCodeBits, '0', 16);
        flags |= Integer.parseInt(opCodeBits, 2);
        if (this.isAuthoritativeAnswer())
            flags |= 0b0_0000_1_0_0_0_0_0_0_0000;
        if (this.isTruncation())
            flags |= 0b0_0000_0_1_0_0_0_0_0_0000;
        if (this.isRecursionDesired())
            flags |= 0b0_0000_0_0_1_0_0_0_0_0000;
        if (this.isRecursionAvailable())
            flags |= 0b0_0000_0_0_0_1_0_0_0_0000;
        if (this.isReserved1())
            flags |= 0b0_0000_0_0_0_0_1_0_0_0000;
        if (this.isReserved2())
            flags |= 0b0_0000_0_0_0_0_0_1_0_0000;
        if (this.isReserved3())
            flags |= 0b0_0000_0_0_0_0_0_0_1_0000;

        String rCodeBits = Integer.toBinaryString(rCode.getCode());
        rCodeBits = Util.prePaddedString(rCodeBits, '0', 16);
        flags |= Integer.parseInt(rCodeBits, 2);
        return (short) flags;
    }

    public void loadToByteBuffer(ByteBuffer buffer) {
        buffer.putShort(this.getMessageId())
                .putShort(getFlags())
                .putShort(this.getQdCount())
                .putShort(this.getAnCount())
                .putShort(this.getNsCount())
                .putShort(this.getArCount());
    }
}
