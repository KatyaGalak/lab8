package lab8.shared.io.connection;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
/**
 * The Request class represents a user command request in the application. It encapsulates
 * the command string, its associated arguments, and a console instance for output.
 */
public class Request implements Serializable {

    @Serial
    private static final long serialVersionUID = 4190619474367174092L;

    private final String command;

    private List<String> args;

    private Mark mark;

    private final UserCredentials userCredentials;


    /**
     * Constructs a new Request with the specified command, arguments, and console.
     *
     * @param command the command string
     * @param args the list of arguments associated with the command
     */
    public Request(final String command, final List<String> args, UserCredentials userCredentials) {
        this.command = command;
        this.args = args;
        this.userCredentials = userCredentials;
    }

    public Request(Mark mark, String command, List<String> input, UserCredentials userCredentials) {
        this.mark = mark;
        this.args = input;
        this.command = command;
        this.userCredentials = userCredentials;
    }

    /**
     * Constructs a new Request with the specified command and console.
     *
     * @param command the command string
     */
    public Request(final String command, UserCredentials userCredentials) {
        this.command = command;
        this.userCredentials = userCredentials;
    }
}
