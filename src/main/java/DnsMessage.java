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
        System.out.println("inputByteBuffer = " + Arrays.toString(inputByteBuffer.array()));
        Header header = Header.decode(inputByteBuffer);
        header.queryResponse(true)
                .authoritativeAnswer(false)
                .truncation(false)
                .recursionAvailable(false)
                .reserved1(false)
                .reserved2(false)
                .reserved3(false)
                .rCode(header.getOpCode() == OpCode.QUERY ? RCode.NO_ERROR : RCode.NOT_IMPLEMENTED);
        header.anCount(header.getQdCount());

        List<Question> questions = new ArrayList<>();
        List<Answer> answers = new ArrayList<>();
        for (int i=0; i<header.getQdCount(); i++) {
            Question question = Question.decode(inputByteBuffer);
            question.qType((short) 1)
                    .qClass((short) 1);
            questions.add(question);
            Answer answer = new Answer();
            answer.anName(question.getqName())
                    .anType((short) 1)
                    .anClass((short) 1)
                    .anTtl(60)
                    .anRLength((short) 4)
                    .anRData(new byte[]{8,8,8,8});
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
        for (int i=0; i<this.questions().size(); i++){
            messages.add(new DnsMessage(this.header(), this.questions().get(i), this.answers().get(i)));
        }
        return messages;
    }
}
