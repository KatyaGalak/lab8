package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.ticket.Ticket;

import java.util.Comparator;

/**
 * Command to retrieve and display the ticket with the maximum ID.
 */
public class MaxById extends Command {
    /**
     * Constructor for the MaxById command.
     * Initializes the command with its name and description.
     */


    public MaxById() {
        super("max_by_id", "Get an item with the max ID");
    }

    /**
     * Executes the command to retrieve the ticket with the maximum ID.
     *
     * @param request The request containing the arguments for the command.
     * @return A response containing the ticket with the maximum ID or a message if the collection is empty.
     */
    @Override
    public Response execute(Request request, SharedConsoleServer console) {

        var maxElemOptional = CollectionManager.getInstance().getTicketCollection().stream().max(Comparator.comparing(Ticket::getId));

        if (maxElemOptional.isEmpty()) {
            return new Response("The collection is empty (no element with max ID)");
        }

        Ticket maxElem = maxElemOptional.get();

        return new Response("Element with max ID: ", maxElem);
    }
}
