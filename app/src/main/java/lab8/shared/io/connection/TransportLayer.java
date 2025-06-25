package lab8.shared.io.connection;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Transport layer that provides reliable UDP communication by automatically
 * fragmenting large messages into packets and reassembling them on the receiving side.
 * This class abstracts away the complexity of packet management from the application layer.
 */
public class TransportLayer {
    private static final Logger logger = Logger.getLogger(TransportLayer.class.getName());
    private static final int MAX_PACKET_SIZE = 4096;
    private static final int DATA_SIZE = MAX_PACKET_SIZE - 256; // Reserve space for headers
    private static final int TIMEOUT_MS = 5000;

    private final DatagramSocket socket;
    private final Map<UUID, PacketBuffer> incomingBuffers = new ConcurrentHashMap<>();

    public TransportLayer(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        socket.setSoTimeout(TIMEOUT_MS);
    }

    public TransportLayer() throws SocketException {
        this.socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT_MS);
    }

    /**
     * Sends any serializable object to the specified address.
     * Automatically fragments large objects into multiple packets.
     */
    public void send(Serializable data, InetSocketAddress destination) throws IOException {
        byte[] serializedData = serialize(data);
        UUID sessionId = UUID.randomUUID();
        int totalChunks = (int) Math.ceil((double) serializedData.length / DATA_SIZE);

        logger.info("Sending " + totalChunks + " packets for session " + sessionId);

        for (int i = 0; i < totalChunks; i++) {
            int start = i * DATA_SIZE;
            int length = Math.min(serializedData.length - start, DATA_SIZE);
            byte[] chunk = Arrays.copyOfRange(serializedData, start, start + length);

            Packet packet = new Packet(sessionId, i, totalChunks, chunk, true);
            byte[] packetData = serialize(packet);

            DatagramPacket datagramPacket = new DatagramPacket(packetData, packetData.length, destination);
            socket.send(datagramPacket);
        }
    }

    /**
     * Receives and reassembles a complete object from incoming packets.
     * Returns null if no data is received within the timeout period.
     */
    public ReceivedData receive() throws IOException, ClassNotFoundException {
        return receive(TIMEOUT_MS);
    }

    /**
     * Receives and reassembles a complete object from incoming packets.
     * Returns null if no data is received within the specified timeout.
     */
    public ReceivedData receive(int timeoutMs) throws IOException, ClassNotFoundException {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                byte[] buffer = new byte[MAX_PACKET_SIZE];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(datagramPacket);

                Packet packet = (Packet) deserialize(datagramPacket.getData());
                UUID sessionId = packet.sessionId();

                PacketBuffer packetBuffer = incomingBuffers.computeIfAbsent(sessionId, k -> new PacketBuffer());
                packetBuffer.addPacket(packet);

                if (packetBuffer.isComplete()) {
                    incomingBuffers.remove(sessionId);
                    byte[] completeData = packetBuffer.assemble();
                    Serializable object = (Serializable) deserialize(completeData);
                    return new ReceivedData(object, (InetSocketAddress) datagramPacket.getSocketAddress(), sessionId);
                }
            } catch (SocketTimeoutException ignored) {
            }
        }

        return null;
    }

    /**
     * Sends a response to a specific session (for request-response pattern).
     */
    public void sendResponse(Serializable data, InetSocketAddress destination, UUID sessionId) throws IOException {
        byte[] serializedData = serialize(data);
        int totalChunks = (int) Math.ceil((double) serializedData.length / DATA_SIZE);

        logger.info("Sending response: " + totalChunks + " packets for session " + sessionId);

        for (int i = 0; i < totalChunks; i++) {
            int start = i * DATA_SIZE;
            int length = Math.min(serializedData.length - start, DATA_SIZE);
            byte[] chunk = Arrays.copyOfRange(serializedData, start, start + length);

            Packet packet = new Packet(sessionId, i, totalChunks, chunk, false);
            byte[] packetData = serialize(packet);

            DatagramPacket datagramPacket = new DatagramPacket(packetData, packetData.length, destination);
            socket.send(datagramPacket);
        }
    }

    private byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
         * Container for received data with sender information.
         */
        public record ReceivedData(Serializable data, InetSocketAddress sender, UUID sessionId) {
    }

    /**
     * Buffer for reassembling packets from a single session.
     */
    private static class PacketBuffer {
        private final Map<Integer, byte[]> chunks = new TreeMap<>();
        private int totalChunks = -1;

        public void addPacket(Packet packet) {
            chunks.put(packet.sequenceNumber(), packet.data());
            if (totalChunks == -1) {
                totalChunks = packet.totalChunks();
            }
        }

        public boolean isComplete() {
            return totalChunks != -1 && chunks.size() == totalChunks;
        }

        public byte[] assemble() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (byte[] chunk : chunks.values()) {
                try {
                    baos.write(chunk);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to assemble packets", e);
                }
            }
            return baos.toByteArray();
        }
    }
} 