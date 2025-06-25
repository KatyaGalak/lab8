package lab8.shared.ticket;

import lombok.Getter;

/**
 * The TicketType enum represents the various types of tickets available.
 * Each ticket type has an associated value that indicates its priority or category.
 */
@Getter
public enum TicketType {
    VIP(4),
    USUAL(3),
    BUDGETARY(2),
    CHEAP(1);

    private final int value;

    TicketType(int value) {

        this.value = value;
    }
}
