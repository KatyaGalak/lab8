package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.server.system.database.DatabaseManagerUser;
import lab8.server.system.factories.TicketFactory;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.ticket.Ticket;

/**
 * Command to add a new ticket to the collection if it is the minimum ticket.
 * This command compares the new ticket with the current minimum ticket in the
 * collection
 * and adds it only if it is smaller.
 */
public class AddIfMin extends Command {

    /**
     * Constructor for the AddIfMin command.
     * Initializes the command with its name, description, and required arguments.
     */
    public AddIfMin() {
        super("add_if_min", "Add a new element to the collection if its value is less than that of the smallest element in this collection");
    }

    /**
     * Executes the command to add a ticket to the collection if it is the minimum.
     *
     * @param request The request containing the command arguments.
     * @return Response indicating the result of the command execution.
     */
    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        try {
            if (request.getArgs() == null || request.getArgs().size() != 9) {
                return new Response("Команда 'add_if_min' требует 9 аргументов.", false);
            }

            Ticket newTicket = TicketFactory.create(request.getArgs());

            Long userId = DatabaseManagerUser.getInstance().getUserId(request.getUserCredentials().username());
            newTicket.setCreatorId(userId);

            if (CollectionManager.getInstance().addIfMin(newTicket)) {
                return new Response("Новый билет добавлен, так как он меньше минимального.", true);
            } else {
                return new Response("Новый билет не был добавлен, так как он не меньше минимального.", true);
            }
        } catch (IllegalArgumentException e) {
            return new Response("Ошибка при создании билета: " + e.getMessage(), false);
        }
    }
}
