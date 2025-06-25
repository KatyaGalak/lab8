package lab8.server.system.commands;

//import java.util.Collection;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.server.system.database.DatabaseManagerUser;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
//import lab6.shared.ticket.Ticket;

/**
 * Command to clear the collection of tickets.
 * This command removes all tickets from the collection, effectively resetting it.
 */
public class Clear extends Command {

    /**
     * Constructor for the Clear command.
     * Initializes the command with its name and description.
     */
    public Clear() {
        super("clear", "Clear the collection of your items");
    }

    /**
     * Executes the command to clear the ticket collection.
     *
     * @param request The request containing the command arguments.
     * @return Response indicating the result of the command execution.
     */
    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        //Collection<Ticket> tickets = CollectionManager.getInstance().getTicketCollection();

        long userID = DatabaseManagerUser.getInstance().getUserId(request.getUserCredentials().username());

        if (CollectionManager.getInstance().deleteByUser(userID)) {
            CollectionManager.getInstance().loadCollection();
            return new Response("Все ваши билеты были удалены из коллекции.", true);
        } else {
            return new Response("Произошла ошибка при удалении ваших билетов.", false);
        }
    }
}
