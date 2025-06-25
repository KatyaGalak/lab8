package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.server.system.database.DatabaseManagerUser;
import lab8.server.system.factories.TicketFactory;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.ticket.Ticket;

public class Add extends Command {

    public Add() {
        super("add", "Add a new element to the collection");
    }


    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        try {
            if (request.getArgs() == null || request.getArgs().size() != 9) {
                return new Response("Команда 'add' требует 9 аргументов.", false);
            }

            Ticket ticket = TicketFactory.create(request.getArgs());

            Long userId = DatabaseManagerUser.getInstance().getUserId(request.getUserCredentials().username());
            ticket.setCreatorId(userId);

            if (CollectionManager.getInstance().add(ticket)) {
                return new Response("Билет успешно добавлен.", true);
            } else {
                return new Response("Не удалось добавить билет в коллекцию (возможно, он не прошел валидацию или произошла ошибка БД).", false);
            }
        } catch (IllegalArgumentException e) {
            return new Response("Ошибка при создании билета: " + e.getMessage(), false);
        }
    }
}