package lab8.server;

import lab8.shared.io.connection.Request;

import java.net.InetSocketAddress;
import java.util.UUID;

public record ServerRequest(Request request, InetSocketAddress clientAddress, UUID sessionId) {

    @Override
    public String toString() {
        return request.toString() + " " + clientAddress.toString() + " (Session: " + sessionId + ")";
    }
}