package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.ticket.TicketType;

/**
 * Command to count the number of tickets in the collection with a type less than the specified type.
 * This command compares the type of each ticket in the collection with the provided type.
 */
public class CountLessThanType extends Command {
    static final String[] args = new String[]{"type"};

    /**
     * Constructor for the CountLessThanType command.
     * Initializes the command with its name, description, and required arguments.
     */
    public CountLessThanType() {
        super("count_less_than_type", "Count the number of elements with a type less than the type specified by the user", args);
    }

    /**
     * Executes the command to count tickets with a type less than the specified type.
     *
     * @param request The request containing the command arguments.
     * @return Response indicating the result of the command execution.
     */
    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        if (request.getArgs().getFirst().isEmpty()) {
            return new Response("Empty ticket type is set");
        }
        try {
            TicketType ticketType = TicketType.valueOf(request.getArgs().getFirst().toUpperCase());

            long cnt = CollectionManager.getInstance().getTicketCollection().stream()
                    .filter(ticket -> ticket.getType().getValue() < ticketType.getValue())
                    .count();

            return new Response("Number of items with a ticket type less than the specified type: " + cnt);
        } catch (IllegalArgumentException e) {
            return new Response("There is no ticket type: " + request.getArgs().getFirst());
        }
    }
}
