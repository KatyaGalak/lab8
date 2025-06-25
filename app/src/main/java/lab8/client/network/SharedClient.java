package lab8.client.network;

import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.io.connection.TransportLayer;
import lab8.shared.io.console.StandartConsole;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class SharedClient extends StandartConsole {
    private static final Logger logger = Logger.getLogger(SharedClient.class.getName());
    private static final String SERVER_IP = "192.168.10.80";
    private static final int SERVER_PORT = 2223;
    private final TransportLayer transport;

    public SharedClient() throws Exception {
        this.transport = new TransportLayer();
        logger.info("SharedClient initialized, connecting to " + SERVER_IP + ":" + SERVER_PORT);
    }

    public Response sendReceive(Request request) {
        try {
            logger.info("Sending request: " + request.getCommand() + " to " + SERVER_IP + ":" + SERVER_PORT);

            InetSocketAddress serverAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);
            transport.send(request, serverAddress);

            logger.info("Request sent, waiting for response...");
            TransportLayer.ReceivedData receivedData = transport.receive();

            if (receivedData == null) {
                logger.warning("No response received from server (timeout)");
                return new Response("Ошибка: Сервер не ответил в течение таймаута");
            }

            logger.info("Response received from " + receivedData.sender());
            return (Response) receivedData.data();

        } catch (Exception e) {
            logger.severe("Error in sendReceive: " + e.getMessage());
            return new Response("Произошла ошибка при обмене данными с сервером: " + e.getMessage());
        }
    }

    public void close() {
        if (transport != null) {
            transport.close();
            logger.info("SharedClient closed");
        }
    }
}