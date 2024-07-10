import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

/*
Reference:
    https://www.zytrax.com/books/dns/ch15/#question
    https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.2
15.3 The DNS Question (Question Section)
While it is normal to have only one question per message, it is permissible to have any number defined by QDCOUNT each question has the same format as defined below:

Field Name	Meaning/Use
QNAME	    The domain name being queried
QTYPE	    The resource records being requested
QCLASS	    The Resource Record(s) class being requested, for instance, internet, chaos etc.
Each field has the following format:

Name	Meaning/Use
QNAME	The name being queried, its content will depend upon the QTYPE (below), for example, a request for an A record will typically require a host part, such as, www.example.com, an MX query will only require a base domain name, such as, example.com. The name being queried is split into labels by removing the separating dots. Each label is represented as a length/data pair as follows:
            Value	Meaning/Use
            no. of chars	Single octet defining the number of characters in the label which follows. The top two bits of this number must be 00 (indicates the label format is being used) which gives a maximum domain name length of 63 bytes (octets). A value of zero indicates the end of the name field.
            domain name	A string containing the characters in the label.
            Wow. To illustrate the above we'll use two examples:
            // assume an MX query with a name of mydomain.com
            // the hex representation is
            08 6D 79 64 6F 6D 61 69 6E 03 63 6F 6D 00
            // printable
             !  m  y  d  o  m  a  i  n  !  c  o  m  !
            // note ! = unprintable

            // assume an A query with a name of www.mydomain.com
            // the hex representation is
            03 77 77 77 08 6D 79 64 6F 6D 61 69 6E 03 63 6F 6D 00
            // printable
             !  w  w  w  !   m  y  d  o  m  a  i n  !  c  o  m  !
            // note ! = unprintable
QTYPE	Unsigned 16 bit value. The resource records being requested. These values are assigned by IANA and a complete list of values may be obtained from them. The following are the most commonly used values:
            Value	Meaning/Use
            x'0001 (1)	Requests the A record for the domain name
            x'0002 (2)	Requests the NS record(s) for the domain name
            x'0005 (5)	Requests the CNAME record(s) for the domain name
            x'0006 (6)	Requests the SOA record(s) for the domain name
            x'000B (11)	Requests the WKS record(s) for the domain name
            x'000C (12)	Requests the PTR record(s) for the domain name
            x'000F (15)	Requests the MX record(s) for the domain name
            x'0021 (33)	Requests the SRV record(s) for the domain name
            x'001C (28)	Requests the AAAA record(s) for the domain name
            x'00FF (255)	Requests ANY resource record (typically wants SOA, MX, NS and MX)
QCLASS	Unsigned 16 bit value. The CLASS of resource records being requested e.g. Internet, CHAOS etc. These values are assigned by IANA and a complete list of values may be obtained from them. The following are the most commonly used values:
            Value	Meaning/Use
            x'0001 (1)	IN or Internet
 */
public class Question {
    public static final int START_INDEX = 12;
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

    /*
    In DNS messages, domain names can be compressed to save space. This compression is done by replacing a sequence of labels with a pointer to a previously used sequence. The method readCompressedLabel recursively resolves these pointers to reconstruct the full domain name.

    Detailed Explanation
        Purpose of DNS Name Compression
            DNS Name Compression: To reduce the size of DNS messages, domain names can be compressed. Instead of repeating the same labels multiple times, a pointer can be used to refer to an earlier occurrence of the same labels. This is done by using the two most significant bits of a length byte to indicate a pointer.
        How DNS Name Compression Works
            Label Length and Pointer:
                1. A byte representing the length of the label (0-63).
                2. If the two most significant bits of this byte are 11 (binary), the remaining 14 bits form a pointer to another part of the DNS message.
        Pointer Detection:
            1. Check if the two most significant bits of length are 11 (binary 0xC0).
            2. Calculating Offset: The pointer offset is calculated from the next 14 bits.

     */
    private static String readLabels(ByteBuffer byteBuffer) {
        int curLength;
        StringBuilder sb = new StringBuilder();
        byte[] byteArr = byteBuffer.array();
        int newPosition = 0;
        while (byteBuffer.hasRemaining() && (curLength = byteBuffer.get()) != 0) {
//            System.out.println("byteBuffer.position() = " + byteBuffer.position());
//            System.out.println("curLength = " + curLength);
            // Check if length represents a pointer offset with first two bits set
            // Pointer offset will usually be of 16 bits ~ 2 bytes
            if ((curLength & 0b1100_0000) == 0b1100_0000) {
                // compressed label
                if (!byteBuffer.hasRemaining()) {
                    throw new IndexOutOfBoundsException("ByteBuffer has no next byte to read pointer offset");
                }
                // Pointer offset represents last 6 bits of curLength and next byte (8 bits) with total 14 bits out of 16 bits
                int pointerOffset = ((curLength & 0b0011_1111) << 8) | (byteBuffer.get() & 0b1111_1111);
                if (newPosition == 0)
                    newPosition = byteBuffer.position();
                byteBuffer.position(pointerOffset);
            } else {
                // uncompressed label
                if (
                        !sb.isEmpty()
                    // Below statement also works same
                    // byteBuffer.position() > Question.START_INDEX+1
                ) {
                    sb.append('.');
                }
                sb.append(new String(byteArr, byteBuffer.position(), curLength, StandardCharsets.UTF_8));
                int movePosition = curLength + byteBuffer.position();
//            System.out.println("movePosition = " + movePosition);
                byteBuffer.position(movePosition);
            }

        }
        if (newPosition != 0) {
            byteBuffer.position(newPosition);
        }
        return sb.toString();
    }

    public static Question decodeQuestion(ByteBuffer byteBuffer) {
        Question question = new Question();

        // Position buffer to starting index of header section
        byteBuffer.position(Question.START_INDEX);


//        System.out.println("Set byteBuffer.position() to = " + byteBuffer.position());
        // Extract Labels
        String qName = readLabels(byteBuffer);
//        System.out.println("qName: " + qName);

        // Extract qType
        short qType = byteBuffer.getShort();

        //Extract qClass
        short qClass = byteBuffer.getShort();

        return question.qName(qName)
                .qType(qType)
                .qClass(qClass);
    }
}
