package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.ticket.Ticket;

import java.util.TreeSet;

/**
 * Command to retrieve and display information about the collection.
 */
public class Info extends Command {
    /**
     * Constructor for the Info command.
     * Initializes the command with its name and description.
     */


    public Info() {
        super("info", "Gets information about the collection");
    }

    /**
     * Executes the command to retrieve information about the collection.
     *
     * @param request The request containing the arguments for the command.
     * @return A response containing the type, initialization date, and size of the collection.
     */
    @Override
    public Response execute(Request request, SharedConsoleServer console) {

        TreeSet<Ticket> collection = CollectionManager.getInstance().getTicketCollection();

        return new Response(String.join(System.lineSeparator(), new String[]{"type: " + collection.getClass(),
                "initialization date: " + CollectionManager.getInstance().getCreationDate(),
                "size: " + collection.size()}));
    }
}
