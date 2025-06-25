package lab8.server.system.commands;

import lab8.server.SharedConsoleServer;
import lab8.server.system.database.DatabaseManagerUser;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;

/**
 * Команда для получения ID пользователя по его имени
 */
public class GetUserId extends Command {

    public GetUserId() {
        super("get_user_id", "Get user ID by username");
    }

    @Override
    public Response execute(Request request, SharedConsoleServer console) {
        if (request.getUserCredentials() == null || request.getUserCredentials().username() == null) {
            return new Response("No user credentials provided", false);
        }

        String username = request.getUserCredentials().username();
        long userId = DatabaseManagerUser.getInstance().getUserId(username);
        
        if (userId == -1) {
            return new Response("User not found", false);
        }
        
        return new Response(String.valueOf(userId), true);
    }
} 