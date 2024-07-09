import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public enum OpCode {
    QUERY(0),
    IQUERY(1),
    STATUS(2);

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
