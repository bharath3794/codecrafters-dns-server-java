import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public enum OpCode {
    QUERY(0),
    IQUERY(1),
    STATUS(2),
    RESERVED1(3),
    RESERVED2(4),
    RESERVED3(5),
    RESERVED4(6),
    RESERVED5(7),
    RESERVED6(8),
    RESERVED7(9),
    RESERVED8(10),
    RESERVED9(11),
    RESERVED10(12),
    RESERVED11(13),
    RESERVED12(14),
    RESERVED13(15);

    private int code;

    OpCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Optional<OpCode> getOpCode(int code) {
        return Arrays.stream(OpCode.values()).filter(opCode -> opCode.getCode()==code).findFirst();
    }
}
