package lab8.server;

import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.io.connection.TransportLayer;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkServer {
    private static final int PORT = 2223;
    private static final Logger logger = Logger.getLogger(NetworkServer.class.getName());
    private final TransportLayer transport;

    public NetworkServer() throws Exception {
        this.transport = new TransportLayer(PORT);
        logger.info("[NETWORK SERVER] Server is listening on port " + PORT);
    }

    public void send(Response response, InetSocketAddress clientAddress, UUID sessionId) {
        try {
            if (sessionId == null) {
                sessionId = UUID.randomUUID();
            }
            transport.sendResponse(response, clientAddress, sessionId);
        } catch (Exception e) {
            logger.severe("Error sending response to " + clientAddress + ": " + e.getMessage());
        }
    }

    public ServerRequest receive() throws SocketException, ClosedChannelException {
        try {
            TransportLayer.ReceivedData receivedData = transport.receive();
            if (receivedData == null) {
                return null;
            }
            Request request = (Request) receivedData.data();
            return new ServerRequest(request, receivedData.sender(), receivedData.sessionId());
        } catch (SocketException | ClosedChannelException e) {
            // Пробрасываем исключения, связанные с закрытием сокета, чтобы главный цикл мог их обработать
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error receiving data", e);
            return null;
        }
    }

    public void shutdown() {
        logger.info("[NETWORK SERVER] Shutting down transport layer");
        if (transport != null) {
            transport.close();
        }
    }
}