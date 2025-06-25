package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;

/**
 * Command to save the current state of the collection to a file.
 */
public class Save extends Command {
    /**
     * Constructor for the Save command.
     * Initializes the command with its name and description.
     */


    public Save() {
        super("save", "Save the collection to file");
    }

    /**
     * Executes the command to save the current state of the collection to a file.
     *
     * @param request The request containing the necessary information for saving.
     * @return A response indicating the result of the save operation.
     */

    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        CollectionManager.getInstance().saveCollection();
        return new Response("Collection saved :)");
    }

}
