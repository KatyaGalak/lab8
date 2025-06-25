package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.server.system.database.DatabaseManagerTicket;
import lab8.server.system.database.DatabaseManagerUser;
import lab8.server.system.factories.TicketFactory;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.ticket.Ticket;

/**
 * Command to update an existing ticket in the collection by its ID.
 */
public class UpdateById extends Command {

    public UpdateById() {
        super("update", "Update the item with the passed ID.");
    }

    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        if (request.getArgs() == null || request.getArgs().size() != 10) {
            return new Response("Команда 'update' требует 10 аргументов (ID и 9 полей билета).", false);
        }

        long ticketId;
        try {
            ticketId = Long.parseLong(request.getArgs().getFirst());
        } catch (NumberFormatException e) {
            return new Response("ID должен быть числом.", false);
        }

        Ticket existingTicket = CollectionManager.getInstance().getTicketCollection().stream()
                .filter(ticket -> ticket.getId() == ticketId)
                .findFirst()
                .orElse(null);

        if (existingTicket == null) {
            return new Response("Билета с ID " + ticketId + " не существует.", false);
        }

        long userId = DatabaseManagerUser.getInstance().getUserId(request.getUserCredentials().username());
        if (!DatabaseManagerTicket.getInstance().checkIsOwnerForTicket(userId, ticketId)) {
            return new Response("Вы не можете обновить билет, который вам не принадлежит.", false);
        }

        try {
            Ticket newTicket = TicketFactory.create(request.getArgs().subList(1, 10));
            newTicket.setId(ticketId);
            newTicket.setCreatorId(userId);

            if (CollectionManager.getInstance().updateTicketByID(existingTicket, newTicket)) {
                return new Response("Билет с ID " + ticketId + " успешно обновлен.", true);
            } else {
                return new Response("Не удалось обновить билет в коллекции.", false);
            }
        } catch (IllegalArgumentException e) {
            return new Response("Ошибка при создании билета для обновления: " + e.getMessage(), false);
        }
    }
}
