package lab8.server;

import lab8.server.system.commands.*;
import lab8.server.system.commands.util.HistoryManager;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;

public class Router {
    private final SharedConsoleServer console;
    private final Server server;

    public Router(SharedConsoleServer console, Server server) {
        this.console = console;
        this.server = server;
    }

    public Response route(Request request) {
        if (request == null || request.getCommand() == null || request.getCommand().isBlank()) {
            return Response.empty();
        }

        if (request.getCommand().equalsIgnoreCase("Login"))
            return new Login().execute(request, console);

        if (request.getCommand().equalsIgnoreCase("Registration"))
            return new Registration().execute(request, console);

        if (request.getCommand().equalsIgnoreCase("get_user_id"))
            return new GetUserId().execute(request, console);

        return AddedCommands.getAddedCommands().stream()
                .filter(name -> name.getName().equalsIgnoreCase(request.getCommand()))
                .findFirst()
                .map(temp -> {

                    if (temp.getName().equalsIgnoreCase("show")) {
                        return new Show(server).execute(request, console);
                    }

                    HistoryManager.getInstance().addCommand(temp.getName());

                    return temp.execute(request, console);
                }).orElse(new Response("Command not found"));
    }
}