package lab8.server.system.factories;

import lab8.shared.ticket.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TicketFactory {

    public static Ticket create(List<String> args) throws IllegalArgumentException {
        if (args.size() != 9) {
            throw new IllegalArgumentException("Неверное количество аргументов для создания билета. Ожидается 9, получено " + args.size());
        }

        try {
            String name = args.getFirst();
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Имя не может быть пустым.");
            }

            double x = Double.parseDouble(args.get(1));
            float y = Float.parseFloat(args.get(2));
            Coordinates coordinates = new Coordinates(x, y);

            double price = Double.parseDouble(args.get(3));
            if (price <= 0) {
                throw new IllegalArgumentException("Цена должна быть больше нуля.");
            }

            boolean refundable = Boolean.parseBoolean(args.get(4));

            TicketType type = TicketType.valueOf(args.get(5).toUpperCase());

            String passportId = args.get(6);
            if (passportId == null || passportId.isEmpty()) {
                throw new IllegalArgumentException("ID паспорта не может быть пустым.");
            }

            if (args.get(7) == null) {
                throw new IllegalArgumentException("Цвет волос не может быть null.");
            }
            Color hairColor = Color.valueOf(args.get(7).toUpperCase());
            if (args.get(8) == null) {
                throw new IllegalArgumentException("Дата рождения не может быть null.");
            }
            LocalDateTime birthday = LocalDate.parse(args.get(8)).atStartOfDay();

            Person person = new Person(birthday, passportId, hairColor);

            return new Ticket(name, coordinates, price, refundable, type, person);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка парсинга числового значения: " + e.getMessage());
        }
    }
} 