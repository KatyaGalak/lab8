package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;

/**
 * Command to exit the application.
 * This command terminates the program when executed.
 */
public class Exit extends Command {

    /**
     * Constructor for the Exit command.
     * Initializes the command with its name and description.
     */
    public Exit() {
        super("exit", "Program shutdown");
    }

    /**
     * Executes the command to terminate the application.
     *
     * @param request The request containing the command arguments.
     * @return Response indicating the result of the command execution.
     */
    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        new Save().execute(null, null);
        return new Response("Command Exit (Exiting the program)");
    }
}
