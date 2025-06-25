package lab8.shared.io.connection;

import lab8.shared.ticket.Ticket;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Response class represents a response to a command request in the application.
 * It encapsulates a message, an optional script, and a list of Ticket objects.
 */
@Getter
@Setter
@ToString
public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 988645730547401797L;

    private boolean success;
    private String message;
    private String script;
    private List<Ticket> tickets = new ArrayList<>();
    private Mark mark;
    private String command;
    private Integer list_index;

    /**
     * Constructs a new Response with the specified message, script, and an empty list of tickets.
     *
     * @param message the response message
     * @param script  the script associated with the response
     */
    public Response(String message, String script) {
        this.message = message;
        this.script = script;
        this.success = (message == null || message.isEmpty());
    }

    /**
     * Constructs a new Response with the specified message and list of tickets.
     *
     * @param message the response message
     * @param tickets the list of Ticket objects associated with the response
     */
    public Response(String message, List<Ticket> tickets) {
        this.message = message;
        this.tickets = tickets;
        this.success = (message == null || message.isEmpty());
    }

    /**
     * Constructs a new Response with the specified message and an array of Ticket objects.
     *
     * @param message the response message
     * @param tickets the Ticket objects associated with the response
     */
    public Response(String message, Ticket... tickets) {
        this(message, List.of(tickets));
    }

    /**
     * Constructs a new Response with the specified message and an empty list of tickets.
     *
     * @param message the response message
     */
    public Response(String message) {
        this.message = message;
        this.success = (message == null || message.isEmpty());
    }

    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public Response(Mark mark, String message) {
        this.mark = mark;
        this.message = message;
        this.success = (message == null || message.isEmpty());
    }

    public Response(Mark mark, String command, String message, List<Ticket> tickets) {
        this(message, tickets);
        this.mark = mark;
    }

    /**
     * Creates an empty Response instance.
     *
     * @return an empty Response
     */
    public static Response empty() {
        return new Response(null);
    }

    public boolean isSuccess() {
        return success;
    }
}
