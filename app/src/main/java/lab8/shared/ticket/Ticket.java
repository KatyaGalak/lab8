package lab8.shared.ticket;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The Ticket class represents a ticket with various attributes such as name, coordinates, price,
 * refundable status, type, and associated person. It implements the Comparable interface to allow
 * sorting based on ticket attributes.
 */
@Data
public class Ticket implements Comparable<Ticket>, Serializable {

    @NonNull
    private String name; // Поле не может быть null, Строка не может быть пустой

    @NonNull
    private Coordinates coordinates; // Поле не может быть null

    @NonNull
    @Setter(AccessLevel.NONE)
    private LocalDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться автоматически

    private double price; // Значение поля должно быть больше 0

    private boolean refundable;
    @NonNull
    private TicketType type; // Поле не может быть null
    @NonNull
    private Person person; // Поле не может быть null
    private Long id; // Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private Boolean installedPrice = false;
    private Boolean installedRefundable = false;
    private Long creatorId;


    /**
     * Default constructor for the Ticket class.
     * Initializes a new Ticket object with the current creation date.
     */
    public Ticket() {

        this.creationDate = LocalDateTime.now();
    }

    /**
     * Constructs a Ticket object with the specified parameters.
     *
     * @param name        the name of the ticket
     * @param coordinates the coordinates associated with the ticket
     * @param price       the price of the ticket
     * @param refundable  indicates if the ticket is refundable
     * @param type        the type of the ticket
     * @param person      the person associated with the ticket
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Ticket(String name, Coordinates coordinates,

                  Double price, Boolean refundable, TicketType type, Person person) throws IllegalArgumentException {
        this();
        this.name = name;
        this.coordinates = coordinates;

        if (price != null)
            installedPrice = true;
        this.price = (price == null ? Integer.MIN_VALUE : price);

        if (refundable != null)
            installedRefundable = true;
        this.refundable = (refundable != null && refundable);

        this.type = type;
        this.person = person;

        validate();
    }

    public Ticket(String name, Coordinates coordinates,

                  Double price, Boolean refundable, TicketType type, Person person, LocalDateTime creationTime) throws IllegalArgumentException {
        this.name = name;
        this.coordinates = coordinates;

        if (price != null)
            installedPrice = true;
        this.price = (price == null ? Integer.MIN_VALUE : price);

        if (refundable != null)
            installedRefundable = true;
        this.refundable = (refundable != null && refundable);

        this.type = type;
        this.person = person;

        this.creationDate = creationTime;

        validate();
    }

    public boolean getRefundable() {
        return refundable;
    }

    public void validate() throws IllegalArgumentException {

        if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");

        if (installedPrice && price <= 0) throw new IllegalArgumentException("Price must be >= 0");

        person.validate();

        //if (id <= 0) throw new IllegalArgumentException("ID must be >= 0");
    }

    @Override
    public int compareTo(Ticket ticket) {
        if (ticket == null) return 1;

        int ans = this.name.compareTo(ticket.name);

        if (ans == 0)
            ans = this.person.compareTo(ticket.person);

        if (ans == 0)
            ans = this.creationDate.compareTo(ticket.creationDate);

        if (installedPrice && ans == 0)
            ans = Double.compare(price, ticket.price);

        if (ans == 0) ans = this.type.compareTo(ticket.type);

        if (ans == 0) ans = this.coordinates.compareTo(ticket.coordinates);

        if (ans == 0) ans = Double.compare(price, ticket.price);

        if (installedRefundable && ans == 0)
            ans = Boolean.compare(refundable, ticket.refundable);

        if (ans == 0)
            ans = Long.compare(id, ticket.id);

        return ans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Ticket t))
            return false;

        return Objects.equals(name, t.name) && Objects.equals(id, t.id) &&
                Objects.equals(coordinates, t.coordinates) & Objects.equals(creationDate, t.creationDate) &&
                Objects.equals(price, t.price) && Objects.equals(refundable, t.refundable) &&
                Objects.equals(type, t.type) && Objects.equals(person, t.person);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, coordinates, creationDate, person, type, price, refundable);
    }

    /**
     * Returns a string representation of the Ticket object.
     *
     * @return a string representation of the Ticket object
     */
    @Override
    public String toString() {

        return "Ticket {" +
                "\n\t id = " + id +
                "\n\t name = " + name +
                "\n\t" + coordinates +
                "\n\t creationData = " + creationDate +
                (installedPrice ? "\n\t price = " + price : "") +
                (installedRefundable ? "\n\t refundable = " + refundable : "") +
                "\n\t type = " + type +
                "\n\t" + person + "\n}";
    }

}
