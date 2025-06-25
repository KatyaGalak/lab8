package lab8.server.system.commands;

import lab8.server.Server;
import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.logging.Logger;

/**
 * Command to display the items in the collection.
 * This command retrieves the string representation of all items in the
 * collection.
 */
@Setter
@Getter
public class Show extends Command {
    private static final Logger logger = Logger.getLogger(Show.class.getName());
    private int count = 10;

    /**
     * Constructor for the Show command.
     * Initializes the command with its name and description.
     */
    public Show(Server server) {
        super("show", "Gets collection items in a string representation");

    }

    /**
     * Executes the command to show the items in the collection.
     *
     * @return Response indicating the result of the command execution.
     */
    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        if (CollectionManager.getInstance().getTicketCollection().isEmpty()) {
            return new Response("Collection is empty");
        }

        return new Response("All collection items",
                new java.util.ArrayList<>(CollectionManager.getInstance().getTicketCollection()));
    }
}
