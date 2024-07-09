import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/*
Reference:
    https://www.zytrax.com/books/dns/ch15/#answer
    https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.3

15.4 The DNS Answer (Answer Section)
The Answer, Authority and Additional Section all comprise RRs and hence share the same format. The section the record appears in determines its type e.g. an A RR can appear in the Answer or Additional section. So far this stuff has been relatively straightforward if messy - take a deep breath before reading on. The format of these records is:

Field Name	Meaning/Use
NAME	    The name being returned e.g. www or ns1.example.net If the name is in the same domain as the question then typically only the host part (label) is returned, if not then a FQDN is returned.
TYPE	    The RR type, for example, SOA or AAAA
CLASS	    The RR class, for instance, Internet, Chaos etc.
TTL         The TTL in seconds of the RR, say, 2800
RLENGTH	    The length of RR specific data in octets, for example, 27
RDATA	    The RR specific data (see Binary RR Formats below) whose length is defined by RDLENGTH, for instance, 192.168.254.2

The various fields have the following meanings:

Name	Meaning/Use
NAME	This name reflects the QNAME of the question i.e. any may take one of TWO formats. The first format is the label format defined for QNAME above. The second format is a pointer (in the interests of data compression which to fair to the original authors was far more important then than now). A pointer is an unsigned 16-bit value with the following format (the top two bits of 11 indicate the pointer format):

            0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15
            1	1	The offset in octets (bytes) from the start of the whole message. Must point to a label format record to derive name length.


            Note: Pointers, if used, terminate names. The name field may consist of a label (or sequence of labels) terminated with a zero length record OR a single pointer OR a label (or label sequence) terminated with a pointer.
TYPE	Unsigned 16 bit value. The resource record types - determines the content of the RDATA field. These values are assigned by IANA and a complete list of values may be obtained from them. The following are the most commonly used values:

                Value	Meaning/Use
                x'0001 (1)	An A record for the domain name
                x'0002 (2)	A NS record( for the domain name
                x'0005 (5)	A CNAME record for the domain name
                x'0006 (6)	A SOA record for the domain name
                x'000B (11)	A WKS record(s) for the domain name
                x'000C (12)	A PTR record(s) for the domain name
                x'000F (15)	A MX record for the domain name
                x'0021 (33)	A SRV record(s) for the domain name
                x'001C (28)	An AAAA record(s) for the domain name
CLASS	Unsigned 16 bit value. The CLASS of resource records being requested, for example, Internet, CHAOS etc. These values are assigned by IANA and a complete list of values may be obtained from them. The following are the most commonly used values:

                Value	Meaning/Use
                x'0001 (1)	IN or Internet
TTL	        Unsigned 32 bit value. The time in seconds that the record may be cached. A value of 0 indicates the record should not be cached.
RDLENGTH	Unsigned 16-bit value that defines the length in bytes (octets) of the RDATA record.
RDATA	    Each (or rather most) resource record types have a specific RDATA format which reflect their resource record format as defined below:
                SOA
                Value	Meaning/Use
                Primary NS	Variable length. The name of the Primary Master for the domain. May be a label, pointer or any combination.
                Admin MB	Variable length. The administrator's mailbox. May be a label, pointer or any combination.
                Serial Number	Unsigned 32-bit integer.
                Refresh interval	Unsigned 32-bit integer.
                Retry Interval	Unsigned 32-bit integer.
                Expiration Limit	Unsigned 32-bit integer.
                Minimum TTL	Unsigned 32-bit integer.
                MX
                Value	Meaning/Use
                Preference	Unsigned 16-bit integer.
                Mail Exchanger	The name host name that provides the service. May be a label, pointer or any combination.
                A
                Value	Meaning/Use
                IP Address	Unsigned 32-bit value representing the IP address
                AAAA
                Value	Meaning/Use
                IP Address	16 octets representing the IP address
                PTR, NS
                Value	Meaning/Use
                Name	The host name that represents the supplied IP address (in the case of a PTR) or the NS name for the supplied domain (in the case of NS). May be a label, pointer or any combination.
 */
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
