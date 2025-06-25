package lab8.client.gui.dialog;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import lab8.client.gui.MainController;
import lab8.client.gui.localization.LocaleManager;
import lab8.client.gui.util.SceneManager;
import lab8.client.network.SharedClient;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.io.connection.UserCredentials;
import lab8.shared.ticket.Ticket;
import lombok.Setter;

import java.util.*;

/**
 * Контроллер для окна визуализации коллекции билетов
 */
public class VisualizationController extends AbstractDialogController {
    // Интервал обновления в секундах
    private static final int UPDATE_INTERVAL = 5;
    // Карта соответствия элементов и билетов
    private final Map<Shape, Ticket> shapeTicketMap = new HashMap<>();
    private final Random random = new Random();
    @FXML
    private Pane visualizationPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label infoLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label countLabel;
    @FXML
    private Button closeButton;
    @Setter
    private MainController mainController;
    @Setter
    private SharedClient client;
    @Setter
    private UserCredentials credentials;
    @Setter
    private SceneManager sceneManager;
    private List<Ticket> tickets;
    // Timeline для автоматического обновления
    private Timeline updateTimeline;

    @Override
    public void initialize() {
        // Настраиваем локализацию
        LocaleManager.getInstance().addLocaleChangeListener(locale -> updateLocalization());
        updateLocalization();

        // Добавляем обработчик изменения размеров окна
        visualizationPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawElements());
        visualizationPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawElements());

        // Создаем и запускаем Timeline для автоматического обновления
        setupUpdateTimeline();
    }

    /**
     * Настраивает Timeline для автоматического обновления
     */
    private void setupUpdateTimeline() {
        updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(UPDATE_INTERVAL), event -> refreshTickets())
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    /**
     * Обновляет данные о билетах с сервера
     */
    private void refreshTickets() {
        try {
            Request request = new Request("show", credentials);
            Response response = client.sendReceive(request);

            if (response != null && response.getTickets() != null) {
                setTickets(response.getTickets());
                statusLabel.setText(LocaleManager.getInstance().getString("status.updated") +
                        " " + java.time.LocalTime.now());
            }
        } catch (Exception e) {
            statusLabel.setText(LocaleManager.getInstance().getString("status.error") + e.getMessage());
        }
    }

    /**
     * Останавливает Timeline при закрытии окна
     */
    public void shutdown() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
    }

    /**
     * Обновляет локализацию элементов интерфейса
     */
    private void updateLocalization() {
        titleLabel.setText(LocaleManager.getInstance().getString("visualization.title"));
        closeButton.setText(LocaleManager.getInstance().getString("button.close"));
        infoLabel.setText(LocaleManager.getInstance().getString("visualization.info"));
    }

    /**
     * Устанавливает билеты для визуализации
     *
     * @param tickets список билетов
     */
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        countLabel.setText(LocaleManager.getInstance().getString("visualization.count") + ": " + tickets.size());
        redrawElements();
    }

    /**
     * Перерисовывает элементы визуализации
     */
    private void redrawElements() {
        visualizationPane.getChildren().clear();
        shapeTicketMap.clear();

        if (tickets == null || tickets.isEmpty()) {
            statusLabel.setText(LocaleManager.getInstance().getString("visualization.empty"));
            return;
        }

        double width = visualizationPane.getWidth();
        double height = visualizationPane.getHeight();

        if (width < 50) width = visualizationPane.getPrefWidth();
        if (height < 50) height = visualizationPane.getPrefHeight();

        List<Ticket> randomTickets = new ArrayList<>(tickets);
        Collections.shuffle(randomTickets, random);

        int displayCount = Math.min(20, randomTickets.size());
        randomTickets = randomTickets.subList(0, displayCount);

        countLabel.setText(LocaleManager.getInstance().getString("visualization.count") + ": " +
                displayCount + "/" + tickets.size());

        int itemsPerRow = Math.max(3, (int) Math.sqrt(displayCount) * 2);
        int itemSize = (int) Math.min(width / itemsPerRow, height / (((double) displayCount / itemsPerRow) + 1)) - 10;

        itemSize = Math.min(Math.max(itemSize, 40), 100);

        List<Point2D> positions = generateNonOverlappingPositions(width, height, displayCount, itemSize);

        for (int i = 0; i < displayCount; i++) {
            Ticket ticket = randomTickets.get(i);

            Point2D position = positions.get(i);
            double x = position.getX();
            double y = position.getY();

            // Размер зависит от цены билета (если цена установлена)
            double size = 40;
            if (ticket.getInstalledPrice() != null && ticket.getInstalledPrice()) {
                size = Math.max(30, Math.min(80, 30 + ticket.getPrice() / 10));
            }

            Shape shape = switch (ticket.getType()) {
                case VIP -> createCircle(x, y, size / 2, ticket);
                case USUAL -> createRectangle(x - size / 2, y - size / 2, size, size, ticket);
                case BUDGETARY -> createRectangle(x - size / 2, y - size / 2, size, size * 0.7, ticket);
                default -> createCircle(x, y, size / 3, ticket);
            };

            Text nameText = new Text(ticket.getName());
            nameText.setX(x - size / 2);
            nameText.setY(y + size / 2 + 15);
            nameText.setFill(javafx.scene.paint.Color.WHITE);
            nameText.setWrappingWidth(size);

            String ownerInfo = "ID: " + (ticket.getCreatorId() != null ? ticket.getCreatorId() : "?");
            Text ownerText = new Text(ownerInfo);
            ownerText.setX(x - size / 3);
            ownerText.setY(y);
            ownerText.setFill(javafx.scene.paint.Color.WHITE);
            ownerText.setWrappingWidth(size * 0.8);
            ownerText.setStyle("-fx-font-size: 10px;");

            shape.setOpacity(0);
            nameText.setOpacity(0);
            ownerText.setOpacity(0);

            visualizationPane.getChildren().add(shape);
            visualizationPane.getChildren().add(nameText);
            visualizationPane.getChildren().add(ownerText);

            shapeTicketMap.put(shape, ticket);

            animateElement(shape, nameText, ownerText);
        }
    }

    /**
     * Генерирует позиции для элементов без пересечений
     *
     * @param width    ширина панели
     * @param height   высота панели
     * @param count    количество элементов
     * @param itemSize размер элемента
     * @return список позиций
     */
    private List<Point2D> generateNonOverlappingPositions(double width, double height, int count, int itemSize) {
        List<Point2D> positions = new ArrayList<>();

        double safeDistance = itemSize * 1.5;

        int maxAttempts = 100;

        for (int i = 0; i < count; i++) {
            Point2D position = null;
            int attempts = 0;

            while (position == null && attempts < maxAttempts) {
                // Генерируем случайную позицию с учетом границ
                double x = itemSize + random.nextDouble() * (width - 2 * itemSize);
                double y = itemSize + random.nextDouble() * (height - 2 * itemSize);
                Point2D candidate = new Point2D(x, y);

                // Проверяем, не пересекается ли с уже размещенными фигурами
                boolean overlaps = false;
                for (Point2D existingPos : positions) {
                    if (candidate.distance(existingPos) < safeDistance) {
                        overlaps = true;
                        break;
                    }
                }

                if (!overlaps) {
                    position = candidate;
                }

                ++attempts;
            }

            // Если не удалось найти позицию без пересечений, используем сетку
            if (position == null) {
                int cols = (int) Math.ceil(Math.sqrt(count));
                int rows = (int) Math.ceil((double) count / cols);

                double cellWidth = width / cols;
                double cellHeight = height / rows;

                int row = i / cols;
                int col = i % cols;

                double x = col * cellWidth + cellWidth / 2;
                double y = row * cellHeight + cellHeight / 2;

                position = new Point2D(x, y);
            }

            positions.add(position);
        }

        return positions;
    }

    /**
     * Создает анимацию появления элемента
     */
    private void animateElement(Shape shape, Text nameText, Text ownerText) {
        FadeTransition fadeShape = new FadeTransition(Duration.millis(500), shape);
        fadeShape.setFromValue(0);
        fadeShape.setToValue(1);

        ScaleTransition scaleShape = new ScaleTransition(Duration.millis(500), shape);
        scaleShape.setFromX(0.1);
        scaleShape.setFromY(0.1);
        scaleShape.setToX(1.0);
        scaleShape.setToY(1.0);

        FadeTransition fadeNameText = new FadeTransition(Duration.millis(500), nameText);
        fadeNameText.setFromValue(0);
        fadeNameText.setToValue(1);
        fadeNameText.setDelay(Duration.millis(300));

        FadeTransition fadeOwnerText = new FadeTransition(Duration.millis(500), ownerText);
        fadeOwnerText.setFromValue(0);
        fadeOwnerText.setToValue(1);
        fadeOwnerText.setDelay(Duration.millis(400));

        ParallelTransition parallelTransition = new ParallelTransition(fadeShape, scaleShape);

        parallelTransition.setDelay(Duration.millis(random.nextInt(500)));
        parallelTransition.play();
        fadeNameText.setDelay(Duration.millis(random.nextInt(500) + 300));
        fadeNameText.play();
        fadeOwnerText.setDelay(Duration.millis(random.nextInt(500) + 400));
        fadeOwnerText.play();
    }

    /**
     * Создает круг для визуализации билета
     */
    private Circle createCircle(double centerX, double centerY, double radius, Ticket ticket) {
        Circle circle = new Circle(centerX, centerY, radius);

        Color color = getTicketColor(ticket);
        circle.setFill(color);

        circle.setOnMouseClicked(event -> handleShapeClick(ticket));

        return circle;
    }

    /**
     * Создает прямоугольник для визуализации билета
     */
    private Rectangle createRectangle(double x, double y, double width, double height, Ticket ticket) {
        Rectangle rectangle = new Rectangle(x, y, width, height);

        Color color = getTicketColor(ticket);
        rectangle.setFill(color);

        rectangle.setOnMouseClicked(event -> handleShapeClick(ticket));

        return rectangle;
    }

    /**
     * Определяет цвет для визуализации билета на основе владельца
     */
    private Color getTicketColor(Ticket ticket) {
        if (ticket.getCreatorId() != null) {
            long creatorId = ticket.getCreatorId();

            int r = (int) ((creatorId * 123) % 155) + 100; // от 100 до 255
            int g = (int) ((creatorId * 456) % 155) + 100;
            int b = (int) ((creatorId * 789) % 155) + 100;

            return Color.rgb(r, g, b, 0.8);
        }

        if (ticket.getPerson().getHairColor() != null) {
            return switch (ticket.getPerson().getHairColor()) {
                case GREEN -> Color.rgb(0, 180, 0, 0.8);
                case RED -> Color.rgb(180, 0, 0, 0.8);
                case ORANGE -> Color.rgb(255, 165, 0, 0.8);
            };
        }

        return Color.rgb(
                100 + random.nextInt(155),
                100 + random.nextInt(155),
                100 + random.nextInt(155),
                0.8
        );
    }

    /**
     * Обрабатывает клик по элементу визуализации
     */
    private void handleShapeClick(Ticket ticket) {
        try {
            sceneManager.showScene(
                    "/ui/main/edit.fxml",
                    LocaleManager.getInstance().getString("button.edit"),
                    javafx.stage.StageStyle.DECORATED,
                    (EditTicketController editController) -> {
                        editController.setClient(client);
                        editController.setCredentials(credentials);
                        editController.setMainController(mainController);
                        editController.setSceneManager(sceneManager);
                        editController.setTicket(ticket);
                    },
                    true,
                    sceneManager.getPrimaryStage()
            );
        } catch (Exception e) {
            statusLabel.setText(LocaleManager.getInstance().getString("status.error") + e.getMessage());
        }
    }

    /**
     * Обрабатывает нажатие на кнопку закрытия
     */
    @FXML
    private void handleClose() {
        shutdown();
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
} 