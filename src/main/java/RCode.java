import java.util.Arrays;
import java.util.Optional;

/*
RCode - Response Code
 */
public enum RCode {
    NO_ERROR(0),
    FORMAT_ERROR(1),
    SERVER_FAILURE(2),
    NAME_ERROR(3),
    NOT_IMPLEMENTED(4),
    REFUSED(5);

    private int code;

    RCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Optional<RCode> getRCode(int code) {
        return Arrays.stream(RCode.values()).filter(rCode -> rCode.getCode()==code).findFirst();
    }
}
