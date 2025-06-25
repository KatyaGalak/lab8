package lab8.client.network;

import lab8.shared.io.console.StandartConsole;
import lombok.Getter;

public class SharedConsoleClient extends StandartConsole {
    @Getter
    private final SharedClient client;
    private final StandartConsole localConsole;

    public SharedConsoleClient(SharedClient client) {
        this.client = client;
        this.localConsole = new StandartConsole();
    }

    @Override
    public void write(String message) {
        localConsole.write(message);
    }

    @Override
    public void writeln(String message) {
        localConsole.writeln(message);
    }

    public String read(String prompt) {
        return localConsole.read(prompt);
    }
}