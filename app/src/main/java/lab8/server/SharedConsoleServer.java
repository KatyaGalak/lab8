package lab8.server;

import lab8.shared.io.console.StandartConsole;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

@Getter
public class SharedConsoleServer extends StandartConsole {
    private static final Logger logger = Logger.getLogger(SharedConsoleServer.class.getName());
    private final NetworkServer network;
    @Setter
    private InetSocketAddress clientAddress;

    public SharedConsoleServer(NetworkServer network) {
        this.network = network;
    }

    @Override
    public void write(String message) {
        if (clientAddress != null) {
            logger.info("[SHARED CONSOLE] Writing message to " + clientAddress + ": " + message);
        } else {
            logger.warning("[SHARED CONSOLE] No client address available for sending message");
        }
    }

    @Override
    public void writeln(String message) {
        write(message + "\\n");
    }
}
