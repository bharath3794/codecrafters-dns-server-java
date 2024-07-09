import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Util {

    public static String postPaddedString(String input, char padChar, int maxLength) {
        if (maxLength < input.length()) {
            throw new IllegalArgumentException("maxLength " + maxLength + " cannot be less than length of input string " + input.length());
        }
        int padLength = maxLength - input.length();
        StringBuilder sb = new StringBuilder(maxLength);
        sb.append(input);
        for (int i=0; i<padLength; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    public static String prePaddedString(String input, char padChar, int maxLength) {
        if (maxLength < input.length()) {
            throw new IllegalArgumentException("maxLength " + maxLength + " cannot be less than length of input string " + input.length());
        }
        int padLength = maxLength - input.length();
        StringBuilder sb = new StringBuilder(maxLength);
        for (int i=0; i<padLength; i++) {
            sb.append(padChar);
        }
        sb.append(input);
        return sb.toString();
    }

}
