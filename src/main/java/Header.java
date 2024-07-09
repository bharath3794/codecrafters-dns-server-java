import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;


/*
Reference: https://www.zytrax.com/books/dns/ch15/#header
15.2 The Message Header
Present in all messages. Never empty. Contains various flags and values which control the transaction. If you are not comfortable with bits, bytes and hex values take up origami or read this quick memory jogger. And while you are in this receptive mode you may want remind yourself that bit numbering standards are a real mess.

0 |	1   2	3	4 |	5  | 6	|  7	|  8	|  9    |  10   |  11   | 12    13  14  15
                                        Message ID
QR|	   OPCODE	  | AA | TC	|  RD	|  RA	|  res1 |  res2	|  res3	|    RCODE
QDCOUNT (No. of items in Question Section)
ANCOUNT (No. of items in Answer Section)
NSCOUNT (No. of items in Authority Section)
ARCOUNT (No. of items in Additional Section)

Message ID	16 bit message ID supplied by the requestion (the questioner) and reflected back unchanged by the responder (answerer). Identifies the transaction.
QR	Query - Response bit. Set to 0 by the questioner (query) and to 1 in the response (answer).
OPCODE	Identifies the request/operation type. Currently assigned values are:
            Value	Meaning/Use
            0	QUERY. standard query.
            1	IQUERY. Inverse query. Optional support by DNS
            2	STATUS. DNS status request
AA	Authoritative Answer. Valid in responses only. Because of aliases multiple owners may exists so the AA bit corresponds to the name which matches the query name, OR the first owner name in the answer section.
TC	TrunCation - specifies that this message was truncated due to length greater than that permitted on the transmission channel. Set on all truncated messages except the last one.
RD	Recursion Desired - this bit may be set in a query and is copied into the response if recursion supported by this Name Server. If Recursion is rejected by this Name Server, for example it has been configured as Authoritative Only, the response (answer) does not have this bit set. Recursive query support is optional.
RA	Recursion Available - this bit is valid in a response (answer) and denotes whether recursive query support is available (1) or not (0) in the name server.
RCODE	Identifies the response type to the query. Ignored on a request (question). Currently assigned values:
            Value	Meaning/Use
            0	No error condition.
            1	Format error - The name server was unable to interpret the query.
            2	Server failure - The name server was unable to process this query due to a problem with the name server.
            3	Name Error - Meaningful only for responses from an authoritative name server, this code signifies that the domain name referenced in the query does not exist.
            4	Not Implemented - The name server does not support the requested kind of query.
            5	Refused - The name server refuses to perform the specified operation for policy reasons. For example, a name server may not wish to provide the information to the particular requester, or a name server may not wish to perform a particular operation (e.g., zone transfer) for particular data.
QDCOUNT	Unsigned 16 bit integer specifying the number of entries in the Question Section.
ANCOUNT	Unsigned 16 bit integer specifying the number of resource records in the Answer Section. May be 0 in which case no answer record is present in the message.
NSCOUNT	Unsigned 16 bit integer specifying the number of name server resource records in the Authority Section. May be 0 in which case no authority record(s) is(are) present in the message.
ARCOUNT	Unsigned 16 bit integer specifying the number of resource records in the Additional Section. May be 0 in which case no addtional record(s) is(are) present in the message.
 */
public class Header {
    public static final int START_INDEX_HEADER_SECTION = 0;
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

    public RCode getRCode() {
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

    public static Header decodeHeader(ByteBuffer byteBuffer) {
        Header header = new Header();
        // Position buffer to starting index of header section
        byteBuffer.position(Header.START_INDEX_HEADER_SECTION);

        // Read ID which is first byte
        final short packetId = byteBuffer.getShort();

        short flags = byteBuffer.getShort();
        // Extract Query Response
        byte queryResponse =(byte) (flags >> 15 & 1);
        // Extract OpCode
        byte opCode = (byte) (flags >> 11 & 0b0_1111);
        //Extract Authoritative Answer
        byte authoritativeAnswer = (byte) (flags >> 10 & 1);
        //Extract Truncation
        byte truncation = (byte) (flags >> 9 & 1);
        //Extract Recursion Desired
        byte recursionDesired = (byte) (flags >> 8 & 1);
        //Extract Recursion Available
        byte recursionAvailable = (byte) (flags >> 7 & 1);
        // Extract Reserved1
        byte reserved1 = (byte) (flags >> 6 & 1);
        // Extract Reserved2
        byte reserved2 = (byte) (flags >> 5 & 1);
        // Extract Reserved3
        byte reserved3 = (byte) (flags >> 4 & 1);
        // Extract RCode
        byte rCode = (byte) (flags & 0b1111);

        short qdCount = byteBuffer.getShort();

        short anCount = byteBuffer.getShort();

        short nsCount = byteBuffer.getShort();

        short arCount = byteBuffer.getShort();

        header.messageId(packetId)
                .queryResponse(queryResponse == 1)
                .opCode(OpCode.getOpCode(opCode).orElse(OpCode.QUERY))
                .authoritativeAnswer(authoritativeAnswer == 1)
                .truncation(truncation == 1)
                .recursionDesired(recursionDesired == 1)
                .recursionAvailable(recursionAvailable == 1)
                .reserved1(reserved1 == 1)
                .reserved2(reserved2 == 1)
                .reserved3(reserved3 == 1)
                .rCode(RCode.getRCode(rCode).orElse(RCode.NO_ERROR))
                .qdCount(qdCount)
                .anCount(anCount)
                .nsCount(nsCount)
                .arCount(arCount);
        return header;
    }
}
