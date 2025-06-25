package lab8.server.system.commands.util;

import lab8.shared.ticket.*;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class CreateRandomTicket {
    
    public static Ticket generate() {
        Random random = new Random();

        String name = "RandomTicket_" + random.nextInt(1000);
        Coordinates coordinates = new Coordinates(random.nextDouble() * 1000, random.nextFloat() * 1000);
        
        double price = random.nextDouble() * 500;
        if (price <= 0) {
            price = 1.0;
        }

        boolean refundable = random.nextBoolean();
        TicketType type = TicketType.values()[random.nextInt(TicketType.values().length)];
        
        LocalDateTime birthday = LocalDateTime.now().minusYears(random.nextInt(50) + 18);
        String passportID = UUID.randomUUID().toString().substring(0, 10); // Make it shorter
        Color hairColor = Color.values()[random.nextInt(Color.values().length)];
        Person person = new Person(birthday, passportID, hairColor);
        
        Ticket ticket = new Ticket(name, coordinates, price, refundable, type, person);
        return ticket;
    }
}
