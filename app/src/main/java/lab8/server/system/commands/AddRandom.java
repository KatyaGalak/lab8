package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.collection.CollectionManager;
import lab8.server.system.commands.util.CreateRandomTicket;
import lab8.server.system.database.DatabaseManagerUser;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.ticket.Ticket;

/**
 * Command to add a specified number of random tickets to the collection.
 * This command generates random tickets and adds them to the existing collection.
 */
public class AddRandom extends Command {
    static final String[] args = new String[]{"cnt"};

    /**
     * Constructor for the AddRandom command.
     * Initializes the command with its name and description.
     */
    public AddRandom() {
        super("add_random", "Add a set number of random tickets", args);
    }

    /**
     * Executes the command, adding random tickets to the collection.
     *
     * @param request The request containing the command arguments.
     * @return Response with the result of the command execution.
     */
    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        int cntTickets = 1;

        if (request.getArgs() != null && !request.getArgs().isEmpty()) {
            try {
                cntTickets = Integer.parseInt(request.getArgs().getFirst());
            } catch (NumberFormatException e) {
                return new Response("Аргумент должен быть числом.", false);
            }
        }

        if (cntTickets <= 0) {
            return new Response("Количество билетов должно быть положительным числом.", false);
        }
        if (cntTickets > 50) {
            return new Response("Вы не можете добавить более 50 билетов за раз.", false);
        }

        try {
            long userId = DatabaseManagerUser.getInstance().getUserId(request.getUserCredentials().username());
            int addedCount = 0;
            for (int i = 0; i < cntTickets; i++) {
                Ticket randomTicket = CreateRandomTicket.generate();
                randomTicket.setCreatorId(userId);
                if (CollectionManager.getInstance().add(randomTicket)) {
                    addedCount++;
                }
            }
            return new Response("Успешно добавлено " + addedCount + " из " + cntTickets + " случайных билетов.", true);
        } catch (Exception e) {
            return new Response("Произошла ошибка при добавлении случайных билетов: " + e.getMessage(), false);
        }
    }
}
