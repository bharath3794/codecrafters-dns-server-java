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
}
