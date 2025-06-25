package lab8.server.system.commands;

import java.util.List;

import lab8.server.Server;
import lombok.Getter;

/**
 * A utility class that holds a list of all added command instances.
 * This class provides a method to retrieve the list of commands that can be executed.
 */
public final class AddedCommands {
    /**
     * -- GETTER --
     *  Retrieves the list of added commands.
     *
     * @return A list of Command instances that have been added.
     */
    @Getter
    private static List<Command> addedCommands = List.of(
        new Add(),
        new AddIfMax(),
        new AddIfMin(),
        new Clear(),
        new CountLessThanType(),
        new Exit(),
        new FilterContainsName(),
        new GetUserId(),
        new History(),
        new Info(),
        new MaxById(),
        new RemoveById(),
        new RemoveGreater(),
        new Show(Server.getInstance()),
        new UpdateById(),
        new AddRandom()
    );

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AddedCommands() {}
}
