package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.database.DatabaseManagerUser;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;

public class Login extends Command {


    public Login() {
        super("login", "User login (registration has already been completed)");
    }

    @Override
    public Response execute(Request request, SharedConsoleServer console) {

        if (DatabaseManagerUser.getInstance().getUserId(request.getUserCredentials().username()) == -1)
            return new Response("Login Error:" + " there is no user with name "
                    + request.getUserCredentials().username());

        boolean checkPassword = DatabaseManagerUser.getInstance().checkPassword(request.getUserCredentials());

        if (checkPassword) {
            return new Response("Login completed successfully");
        }

        return new Response("Login Error: " + "password doesn't match");
    }
}