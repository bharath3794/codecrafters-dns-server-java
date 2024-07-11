import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record DnsMessage(Header header, List<Question> questions, List<Answer> answers) {

    public DnsMessage(Header header, Question question, Answer answer) {
        this(header, List.of(question), List.of(answer));
    }

    public static DnsMessage from(byte[] buffer) {
        ByteBuffer inputByteBuffer = ByteBuffer.wrap(buffer);
//        System.out.println("inputByteBuffer = " + Arrays.toString(inputByteBuffer.array()));
        Header header = Header.decode(inputByteBuffer);

        List<Question> questions = new ArrayList<>();
        List<Answer> answers = new ArrayList<>();
        for (int i=0; i<header.getQdCount(); i++) {
            Question question = Question.decode(inputByteBuffer);
            questions.add(question);
        }
        for (int i=0; i<header.getAnCount(); i++) {
            Answer answer = Answer.decode(inputByteBuffer, inputByteBuffer.position());
            System.out.println("Answer IPV4 " + Arrays.toString(answer.getAnRData()));
            answers.add(answer);
        }
        return new DnsMessage(header, questions, answers);
    }

    public void encode(ByteBuffer byteBuffer) {
        this.header().encode(byteBuffer);
        this.questions().stream().forEach(question -> question.encode(byteBuffer));
        this.answers().stream().forEach(answer -> answer.encode(byteBuffer));
    }

    public List<DnsMessage> splitQuestions() {
        List<DnsMessage> messages = new ArrayList<>();
        if (this.questions().size() <= 1) {
            messages.add(this);
            return messages;
        }
        for (int i=0; i<this.questions().size(); i++){
            messages.add(new DnsMessage(this.header(), this.questions().get(i), this.answers().get(i)));
        }
        return messages;
    }
}
