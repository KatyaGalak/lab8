package lab8.shared.io.connection;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record Packet(UUID sessionId, int sequenceNumber, int totalChunks, byte[] data,
                     boolean isRequest) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

}