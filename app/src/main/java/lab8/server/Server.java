package lab8.server;

import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final int NUM_READ_THREAD_POOL = 5;
    private static final int MAX_PROCESSING_THREADS = 100;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final static ConcurrentHashMap<InetSocketAddress, BlockingQueue<ServerRequest>> clientRequestQueues = new ConcurrentHashMap<>();
    private static final Semaphore processingSemaphore = new Semaphore(MAX_PROCESSING_THREADS);
    private static Server instance;
    private final Map<InetSocketAddress, SharedConsoleServer> clientConsoles = new ConcurrentHashMap<>();
    private final Set<InetSocketAddress> interactiveClients = ConcurrentHashMap.newKeySet();
    private final ExecutorService readThreadPool;
    private NetworkServer server;

    private Server() {
        readThreadPool = Executors.newFixedThreadPool(NUM_READ_THREAD_POOL);
    }

    public static synchronized Server getInstance() {
        return instance == null ? instance = new Server() : instance;
    }

    public void shutdown() {
        logger.info("[SERVER SHUTDOWN] Shutting down server resources...");

        if (server != null) {
            server.shutdown();
        }

        readThreadPool.shutdown();
        try {
            if (!readThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                readThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            readThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 3. Очищаем ресурсы
        clientConsoles.clear();
        clientRequestQueues.clear();
        interactiveClients.clear();

        logger.info("[SERVER SHUTDOWN] All resources have been released.");
    }

    public void run() {
        try {
            server = new NetworkServer();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "[SERVER] Failed to start NetworkServer: ", e);
            return;
        }

        for (int i = 0; i < NUM_READ_THREAD_POOL; ++i) {
            readThreadPool.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        ServerRequest serverRequest = server.receive();
                        if (serverRequest == null) {
                            continue; // Тайм-аут, просто ждем дальше
                        }
                        processRequest(serverRequest);
                    } catch (SocketException | ClosedChannelException e) {
                        // Это ожидаемое исключение при закрытии сокета. Просто выходим из цикла.
                        logger.info("Сокет был закрыт. Поток получения запросов завершает работу.");
                        break;
                    } catch (Exception e) {
                        if (!Thread.currentThread().isInterrupted()) {
                            logger.log(Level.SEVERE, "Непредвиденная ошибка в потоке получения запросов: " + Thread.currentThread().getName(), e);
                        }
                    }
                }
                logger.info("Поток " + Thread.currentThread().getName() + " завершен.");
            });
        }
    }

    private void processRequest(ServerRequest serverRequest) {
        InetSocketAddress clientAddress = serverRequest.clientAddress();
        Request request = serverRequest.request();
        logger.info("[THREAD] Received request: " + request + " from " + clientAddress);

        try {
            processingSemaphore.acquire();
            new Thread(() -> {
                try {
                    SharedConsoleServer console = new SharedConsoleServer(server);
                    console.setClientAddress(clientAddress);

                    Router router = new Router(console, this);
                    Response response = router.route(request);

                    if (response != null) {
                        logger.info("[SERVER] Sending response: " + response + " to " + clientAddress);
                        server.send(response, clientAddress, serverRequest.sessionId());
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error processing request: " + request, e);
                } finally {
                    processingSemaphore.release();
                }
            }).start();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Interrupted while acquiring processing semaphore");
        }
    }
}
