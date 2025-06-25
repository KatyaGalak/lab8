package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.server.system.database.DatabaseManagerUser;
import lab8.server.system.factories.TicketFactory;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.ticket.Ticket;

/**
 * Command to add a new ticket to the collection if it is the maximum ticket.
 * This command compares the new ticket with the current maximum ticket in the
 * collection
 * and adds it only if it is greater.
 */
public class AddIfMax extends Command {

    /**
     * Constructor for the AddIfMax command.
     * Initializes the command with its name, description, and required arguments.
     */
    public AddIfMax() {
        super("add_if_max", "Add a new element to the collection if its value is greater than the value of the largest element in this collection");
    }

    /**
     * Executes the command to add a ticket to the collection if it is the maximum.
     *
     * @param request The request containing the command arguments.
     * @return Response indicating the result of the command execution.
     */
    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        try {
            if (request.getArgs() == null || request.getArgs().size() != 9) {
                return new Response("Команда 'add_if_max' требует 9 аргументов.", false);
            }

            Ticket newTicket = TicketFactory.create(request.getArgs());

            Long userId = DatabaseManagerUser.getInstance().getUserId(request.getUserCredentials().username());
            newTicket.setCreatorId(userId);

            if (CollectionManager.getInstance().addIfMax(newTicket)) {
                return new Response("Новый билет добавлен, так как он больше максимального.", true);
            } else {
                return new Response("Новый билет не был добавлен, так как он не больше максимального.", true);
            }
        } catch (IllegalArgumentException e) {
            return new Response("Ошибка при создании билета: " + e.getMessage(), false);
        }
    }
}
