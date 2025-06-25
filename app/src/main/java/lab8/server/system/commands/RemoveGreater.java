package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.server.system.database.DatabaseManagerUser;
import lab8.server.system.factories.TicketFactory;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.ticket.Ticket;

import java.util.List;

/**
 * Command to remove tickets from the collection that are greater than the
 * specified ticket.
 */
public class RemoveGreater extends Command {
    static final String[] args = new String[]{"name", "x", "y", "price", "refundable", "type", "person"};

    /**
     * Constructor for the RemoveGreater command.
     * Initializes the command with its name and description.
     */
    public RemoveGreater() {
        super("remove_greater", "Delete items from the collection that are greater than the specified item");
    }

    /**
     * Executes the command to remove tickets from the collection that are greater
     * than the specified ticket.
     *
     * @param request The request containing the ticket information.
     * @return A response indicating the result of the removal operation.
     */
    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        if (request.getArgs() == null || request.getArgs().size() != 7) {
            return new Response("Команда 'remove_greater' требует 7 аргументов для создания эталонного билета.", false);
        }

        try {
            Ticket thresholdTicket = TicketFactory.create(request.getArgs());
            long userId = DatabaseManagerUser.getInstance().getUserId(request.getUserCredentials().username());

            List<Ticket> toRemove = CollectionManager.getInstance().getTicketCollection().stream()
                    .filter(ticket -> ticket.getCreatorId() == userId && ticket.compareTo(thresholdTicket) > 0)
                    .toList();

            if (toRemove.isEmpty()) {
                return new Response("Не найдено билетов, превышающих заданный, которые принадлежат вам.", true);
            }

            int removedCount = 0;
            for (Ticket ticket : toRemove) {
                if (CollectionManager.getInstance().deleteByID(userId, ticket.getId())) {
                    removedCount++;
                }
            }

            CollectionManager.getInstance().loadCollection();
            return new Response("Удалено " + removedCount + " из " + toRemove.size() + " найденных билетов.", true);

        } catch (IllegalArgumentException e) {
            return new Response("Ошибка при создании эталонного билета: " + e.getMessage(), false);
        }
    }
}
