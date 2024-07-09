import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/*
Reference: https://www.zytrax.com/books/dns/ch15/#question
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
