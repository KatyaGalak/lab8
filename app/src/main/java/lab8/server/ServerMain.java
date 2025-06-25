package lab8.server;

import lab8.shared.io.console.StandartConsole;

public class ServerMain {
    public static void main(String[] args) {
        StandartConsole stdConsole = new StandartConsole();

        try {
            Server server = Server.getInstance();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nПолучен сигнал завершения. Начинаю остановку сервера...");
                server.shutdown();
                System.out.println("Сервер успешно остановлен.");
            }));

            server.run();

        } catch (Exception e) {
            stdConsole.writeln(e.toString());
        }
    }
}