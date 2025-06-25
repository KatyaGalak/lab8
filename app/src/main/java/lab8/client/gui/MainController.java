package lab8.client.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lab8.client.gui.dialog.AddTicketController;
import lab8.client.gui.dialog.EditTicketController;
import lab8.client.gui.localization.LocaleManager;
import lab8.client.gui.localization.LocalizationUtils;
import lab8.client.gui.util.DialogUtils;
import lab8.client.gui.util.SceneManager;
import lab8.client.network.SharedClient;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.io.connection.UserCredentials;
import lab8.shared.ticket.Ticket;
import lab8.shared.ticket.TicketType;
import lombok.Setter;

import java.util.Date;
import java.util.List;

public class MainController {

    private static final int UPDATE_INTERVAL = 5; // секунд
    private final ObservableList<Ticket> ticketData = FXCollections.observableArrayList();
    @FXML
    private TableView<Ticket> tableView;
    @FXML
    private TableColumn<Ticket, String> idColumn;
    @FXML
    private TableColumn<Ticket, String> nameColumn;
    @FXML
    private TableColumn<Ticket, String> coordinatesColumn;
    @FXML
    private TableColumn<Ticket, String> creationDateColumn;
    @FXML
    private TableColumn<Ticket, String> priceColumn;
    @FXML
    private TableColumn<Ticket, String> refundableColumn;
    @FXML
    private TableColumn<Ticket, String> typeColumn;
    @FXML
    private TableColumn<Ticket, String> passportIdColumn;
    @FXML
    private TableColumn<Ticket, String> hairColorColumn;
    @FXML
    private TableColumn<Ticket, String> birthdayColumn;
    @FXML
    private TableColumn<Ticket, String> creatorColumn;
    @FXML
    private TextArea reportArea;
    @FXML
    private Label userLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label countLabel;
    @FXML
    private Label lastUpdateLabel;
    @FXML
    private Label reportTitleLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private HBox localeBox;
    @FXML
    private Menu commandsMenu;
    @FXML
    private Menu infoMenu;
    @FXML
    private MenuItem clearMenuItem;
    @FXML
    private MenuItem removeGreaterMenuItem;
    @FXML
    private MenuItem addRandomMenuItem;
    @FXML
    private MenuItem addIfMaxMenuItem;
    @FXML
    private MenuItem filterByNameMenuItem;
    @FXML
    private MenuItem countByTypeMenuItem;
    @FXML
    private MenuItem maxByIdMenuItem;
    @FXML
    private MenuItem infoMenuItem;
    @FXML
    private MenuItem historyMenuItem;
    @FXML
    private Button addButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button visualizationButton;
    @Setter
    private SharedClient client;
    private UserCredentials credentials;
    @Setter
    private Stage stage;
    @Setter
    private SceneManager sceneManager;
    private Long userId;

    public void setCredentials(UserCredentials credentials) {
        this.credentials = credentials;
        getUserId();
        updateUserLabel();
    }

    private void getUserId() {
        try {
            Request request = new Request("get_user_id", credentials);
            Response response = client.sendReceive(request);
            if (response != null && response.isSuccess() && response.getMessage() != null) {
                try {
                    userId = Long.parseLong(response.getMessage().trim());
                } catch (NumberFormatException e) {
                    userId = null;
                }
            }
        } catch (Exception e) {
            userId = null;
        }
    }

    private void updateUserLabel() {
        if (credentials != null) {
            String labelText;
            if (userId != null) {
                labelText = LocaleManager.getInstance().getString("user.label") + credentials.username() + " (ID: " + userId + ")";
            } else {
                labelText = LocaleManager.getInstance().getString("user.label") + credentials.username();
            }
            userLabel.setText(labelText);

            LocaleManager.getInstance().addLocaleChangeListener(locale -> {
                String newLabelText;
                if (userId != null) {
                    newLabelText = LocaleManager.getInstance().getString("user.label") + credentials.username() + " (ID: " + userId + ")";
                } else {
                    newLabelText = LocaleManager.getInstance().getString("user.label") + credentials.username();
                }
                userLabel.setText(newLabelText);
            });
        }
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        setupTableSelection();
        setupContextMenu();
        setupLocalization();
        reportArea.setStyle("-fx-font-family: 'monospace';");
        setupAutoUpdate();
    }

    private void setupLocalization() {
        LocalizationUtils.LocaleComboBox localeComboBox = new LocalizationUtils.LocaleComboBox();
        localeComboBox.setPrefWidth(150);
        localeBox.getChildren().add(localeComboBox);

        LocalizationUtils.localizeMenu(commandsMenu, "menu.commands");
        LocalizationUtils.localizeMenu(infoMenu, "menu.info");

        LocalizationUtils.localizeMenuItem(clearMenuItem, "menu.clear");
        LocalizationUtils.localizeMenuItem(removeGreaterMenuItem, "menu.remove_greater");
        LocalizationUtils.localizeMenuItem(addRandomMenuItem, "menu.add_random");
        LocalizationUtils.localizeMenuItem(addIfMaxMenuItem, "menu.add_if_max");
        LocalizationUtils.localizeMenuItem(filterByNameMenuItem, "menu.filter_by_name");
        LocalizationUtils.localizeMenuItem(countByTypeMenuItem, "menu.count_by_type");
        LocalizationUtils.localizeMenuItem(maxByIdMenuItem, "menu.max_by_id");
        LocalizationUtils.localizeMenuItem(infoMenuItem, "menu.info_command");
        LocalizationUtils.localizeMenuItem(historyMenuItem, "menu.history");

        LocalizationUtils.localizeButton(addButton, "button.add");
        LocalizationUtils.localizeButton(logoutButton, "button.logout");
        LocalizationUtils.localizeButton(visualizationButton, "button.visualization");

        LocalizationUtils.localizeLabel(languageLabel, "locale.language");
        LocalizationUtils.localizeLabel(reportTitleLabel, "report.title");

        statusLabel.setText(LocaleManager.getInstance().getString("status.ready"));

        LocaleManager.getInstance().addLocaleChangeListener(locale -> {
            if (statusLabel.getText().equals(LocaleManager.getInstance().getString("status.ready")) ||
                    statusLabel.getText().startsWith(LocaleManager.getInstance().getString("status.ready"))) {
                statusLabel.setText(LocaleManager.getInstance().getString("status.ready"));
            }

            if (countLabel.getText().matches("^.*\\d+$")) {
                String count = countLabel.getText().replaceAll("^.*?(\\d+)$", "$1");
                countLabel.setText(LocaleManager.getInstance().getString("status.total_records") + count);
            }

            if (!lastUpdateLabel.getText().isEmpty()) {
                // Получаем текущее значение даты и переформатируем его
                try {
                    // Если у нас уже установлена дата, обновляем её форматирование
                    Date lastUpdate = new Date(); // Используем текущую дату как запасной вариант
                    updateLastUpdateLabel(lastUpdate);
                } catch (Exception e) {
                    // Если не удалось распарсить дату, просто обновляем префикс
                    String currentText = lastUpdateLabel.getText();
                    int colonIndex = currentText.indexOf(":");
                    if (colonIndex > 0 && colonIndex < currentText.length() - 1) {
                        String dateValue = currentText.substring(colonIndex + 1).trim();
                        lastUpdateLabel.setText(LocaleManager.getInstance().getString("status.status_bar_date") + dateValue);
                    }
                }
            }
        });

        LocalizationUtils.localizeTableColumn(idColumn, "table.id");
        LocalizationUtils.localizeTableColumn(nameColumn, "table.name");
        LocalizationUtils.localizeTableColumn(coordinatesColumn, "table.coordinates");
        LocalizationUtils.localizeTableColumn(creationDateColumn, "table.creation_date");
        LocalizationUtils.localizeTableColumn(priceColumn, "table.price");
        LocalizationUtils.localizeTableColumn(refundableColumn, "table.refundable");
        LocalizationUtils.localizeTableColumn(typeColumn, "table.type");
        LocalizationUtils.localizeTableColumn(passportIdColumn, "table.passport_id");
        LocalizationUtils.localizeTableColumn(hairColorColumn, "table.hair_color");
        LocalizationUtils.localizeTableColumn(birthdayColumn, "table.birthday");
        LocalizationUtils.localizeTableColumn(creatorColumn, "table.creator");
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            return new SimpleStringProperty(ticket != null ? String.valueOf(ticket.getId()) : "");
        });
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            return new SimpleStringProperty(ticket != null ? LocalizationUtils.formatNumber(ticket.getPrice()) : "");
        });
        refundableColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            return new SimpleStringProperty(ticket != null ? LocalizationUtils.formatBoolean(ticket.getRefundable()) : "");
        });
        creatorColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            return new SimpleStringProperty(ticket != null ? String.valueOf(ticket.getCreatorId()) : "");
        });

        coordinatesColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            if (ticket != null) {
                String coords = String.format("(%s; %s)",
                        LocalizationUtils.formatNumber(ticket.getCoordinates().getX()),
                        LocalizationUtils.formatNumber(ticket.getCoordinates().getY()));
                return new SimpleStringProperty(coords);
            }
            return new SimpleStringProperty("");
        });
        creationDateColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            if (ticket != null) {
                return new SimpleStringProperty(
                        LocalizationUtils.formatDate(ticket.getCreationDate())
                );
            }
            return new SimpleStringProperty("");
        });
        typeColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            if (ticket != null) {
                String key = "table.type." + ticket.getType().name();
                return new SimpleStringProperty(LocalizationUtils.formatEnum(key));
            }
            return new SimpleStringProperty("");
        });
        passportIdColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            if (ticket != null) {
                return new SimpleStringProperty(ticket.getPerson().getPassportID());
            }
            return new SimpleStringProperty("");
        });
        hairColorColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            if (ticket != null && ticket.getPerson().getHairColor() != null) {
                String key = "table.hair_color." + ticket.getPerson().getHairColor().name();
                return new SimpleStringProperty(LocalizationUtils.formatEnum(key));
            }
            return new SimpleStringProperty("");
        });
        birthdayColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            if (ticket != null && ticket.getPerson().getBirthday() != null) {
                return new SimpleStringProperty(LocalizationUtils.formatDate(ticket.getPerson().getBirthday()));
            }
            return new SimpleStringProperty("");
        });

        tableView.setItems(ticketData);
    }

    private void setupTableSelection() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.getSelectionModel().setCellSelectionEnabled(false);

        tableView.setEditable(false);
        tableView.setFocusTraversable(true);

        // Добавляем обработчик двойного клика для редактирования
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Ticket clickedTicket = tableView.getSelectionModel().getSelectedItem();
                if (clickedTicket != null) {
                    handleEdit();
                }
            }
        });
    }

    @FXML
    private void handleAdd() {
        try {
            sceneManager.showScene(
                    "/ui/main/add.fxml",
                    LocaleManager.getInstance().getString("button.add"),
                    javafx.stage.StageStyle.DECORATED,
                    (AddTicketController addController) -> {
                        addController.setClient(client);
                        addController.setCredentials(credentials);
                        addController.setMainController(this);
                        addController.setSceneManager(sceneManager);
                    },
                    true,
                    stage
            );
        } catch (Exception e) {
            statusLabel.setText(LocaleManager.getInstance().getString("status.error") + e.getMessage());
        }
    }

    @FXML
    private void handleEdit() {
        Ticket selectedTicket = tableView.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            statusLabel.setText(LocaleManager.getInstance().getString("dialog.error.select_ticket"));
            return;
        }

        try {
            sceneManager.showScene(
                    "/ui/main/edit.fxml",
                    LocaleManager.getInstance().getString("button.edit"),
                    javafx.stage.StageStyle.DECORATED,
                    (EditTicketController editController) -> {
                        editController.setClient(client);
                        editController.setCredentials(credentials);
                        editController.setMainController(this);
                        editController.setSceneManager(sceneManager);
                        editController.setTicket(selectedTicket);
                    },
                    true,
                    stage
            );
        } catch (Exception e) {
            statusLabel.setText(LocaleManager.getInstance().getString("status.error") + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            sceneManager.showScene(
                    "/ui/auth/auth.fxml",
                    LocaleManager.getInstance().getString("auth.window.title.full"),
                    javafx.stage.StageStyle.DECORATED,
                    (AuthController ctrl) -> {
                        ctrl.setClient(client);
                        ctrl.setSceneManager(sceneManager);
                    },
                    false,
                    null
            );
        } catch (Exception e) {
            statusLabel.setText(LocaleManager.getInstance().getString("auth.error.logout") + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        boolean confirmed = DialogUtils.showConfirmationDialog(
                stage,
                "dialog.clear.title",
                "dialog.clear.title",
                "dialog.clear.message"
        );
        if (confirmed) {
            executeCommand("clear", null);
        }
    }

    @FXML
    private void handleAddRandom() {
        String result = DialogUtils.showTextInputDialog(
                stage,
                "dialog.add_random.title",
                "dialog.add_random.header",
                "dialog.add_random.message",
                "1"
        );

        if (result != null) {
            try {
                int count = Integer.parseInt(result);
                if (count > 0) {
                    executeCommand("add_random", List.of(String.valueOf(count)));
                } else {
                    statusLabel.setText(LocaleManager.getInstance().getString("dialog.error.positive_number"));
                }
            } catch (NumberFormatException e) {
                statusLabel.setText(LocaleManager.getInstance().getString("dialog.error.invalid_number"));
            }
        }
    }

    @FXML
    private void handleRemoveGreater() {
        Ticket selectedTicket = tableView.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            statusLabel.setText(LocaleManager.getInstance().getString("dialog.error.select_compare"));
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                stage,
                "dialog.remove_greater.title",
                "dialog.remove_greater.header",
                "dialog.remove_greater.message"
        );

        if (confirmed) {
            executeCommand("remove_greater", List.of(selectedTicket.getName(),
                    Double.toString(selectedTicket.getCoordinates().getX()),
                    Float.toString(selectedTicket.getCoordinates().getY()),
                    Double.toString(selectedTicket.getPrice()),
                    Boolean.toString(selectedTicket.getRefundable()),
                    selectedTicket.getType().toString(),
                    selectedTicket.getPerson().getPassportID()));
        }
    }

    @FXML
    private void handleFilterByName() {
        String result = DialogUtils.showTextInputDialog(
                stage,
                "dialog.filter.title",
                "dialog.filter.header",
                "dialog.filter.message",
                ""
        );

        if (result != null) {
            tableView.setItems(ticketData.filtered(t -> t.getName().contains(result)));
        } else tableView.setItems(ticketData);
    }

    @FXML
    private void handleCountByType() {
        TicketType result = DialogUtils.showChoiceDialog(
                stage,
                "dialog.count.title",
                "dialog.count.header",
                "dialog.count.message",
                TicketType.VIP,
                TicketType.values()
        );

        if (result != null) {
            executeCommand("count_less_than_type", List.of(result.name()));
        }
    }

    @FXML
    private void handleMaxById() {
        executeCommand("max_by_id", null);
    }

    @FXML
    private void handleInfo() {
        executeCommand("info", null);
    }

    @FXML
    private void handleHistory() {
        executeCommand("history", null);
    }

    @FXML
    private void handleAddIfMax() {
        String name = DialogUtils.showTextInputDialog(
                stage,
                "dialog.add_if_max.title",
                "dialog.add_if_max.header",
                "dialog.add_if_max.message",
                ""
        );

        if (name != null && !name.trim().isEmpty()) {
            executeCommand("add_if_max", List.of(name));
        }
    }

    @FXML
    private void handleVisualization() {
        try {
            sceneManager.showScene(
                    "/ui/main/visualization.fxml",
                    LocaleManager.getInstance().getString("button.visualization"),
                    javafx.stage.StageStyle.DECORATED,
                    (lab8.client.gui.dialog.VisualizationController controller) -> {
                        controller.setClient(client);
                        controller.setCredentials(credentials);
                        controller.setMainController(this);
                        controller.setSceneManager(sceneManager);
                        controller.setTickets(ticketData);
                    },
                    true,
                    stage
            );
        } catch (Exception e) {
            statusLabel.setText(LocaleManager.getInstance().getString("status.error") + e.getMessage());
        }
    }

    public void loadTickets() {
        statusLabel.setText(LocaleManager.getInstance().getString("status.loading_data"));

        try {
            Request request = new Request("show", credentials);
            Response response = client.sendReceive(request);

            if (response != null && response.getTickets() != null) {
                ticketData.setAll(response.getTickets());
                statusLabel.setText(LocaleManager.getInstance().getString("status.data_loaded"));
                countLabel.setText(LocaleManager.getInstance().getString("status.total_records") + ticketData.size());
                updateLastUpdateLabel(new Date());
            } else {
                ticketData.clear();
                statusLabel.setText(response != null ?
                        LocaleManager.getInstance().getString("status.error_loading") + response.getMessage() :
                        LocaleManager.getInstance().getString("status.error_no_response"));
                countLabel.setText(LocaleManager.getInstance().getString("status.total_records") + "0");
            }
        } catch (Exception e) {
            ticketData.clear();
            statusLabel.setText(LocaleManager.getInstance().getString("status.connection_error") + e.getMessage());
            countLabel.setText(LocaleManager.getInstance().getString("status.total_records") + "0");
        }
    }

    private void updateLastUpdateLabel(Date date) {
        try {
            // Получаем формат даты из локализации
            String pattern = LocaleManager.getInstance().getString("status.date_format");
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(
                    pattern, LocaleManager.getInstance().getCurrentLocale()
            );
            lastUpdateLabel.setText(LocaleManager.getInstance().getString("status.status_bar_date") +
                    dateFormat.format(date));
        } catch (Exception e) {
            // В случае ошибки формата используем стандартный формат
            lastUpdateLabel.setText(LocaleManager.getInstance().getString("status.status_bar_date") +
                    date.toString());
        }
    }

    private void executeCommand(String command, List<String> args) {
        statusLabel.setText(LocaleManager.getInstance().getString("status.executing_command"));

        try {
            Request request = new Request(command, args, credentials);
            Response response = client.sendReceive(request);

            if (response != null) {
                // Отображаем результат в области отчетов
                if (response.getMessage() != null && !response.getMessage().isEmpty()) {
                    if (response.getTickets() != null) {
                        reportArea.setText(response.getMessage() +
                                String.join("\n", response.getTickets().stream().map(Ticket::toString).toList()));
                    } else reportArea.setText(response.getMessage());
                    statusLabel.setText(LocaleManager.getInstance().getString("status.command_executed") + command);
                } else {
                    statusLabel.setText(LocaleManager.getInstance().getString("status.command_executed_no_msg"));
                }

                if (response.isSuccess()) {
                    loadTickets();
                }
            } else {
                statusLabel.setText(LocaleManager.getInstance().getString("status.no_response"));
            }
        } catch (Exception e) {
            statusLabel.setText(LocaleManager.getInstance().getString("status.command_error") + e.getMessage());
        }
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem();
        LocalizationUtils.localizeMenuItem(editItem, "context.edit");
        editItem.setOnAction(e -> handleEdit());

        MenuItem deleteItem = new MenuItem();
        LocalizationUtils.localizeMenuItem(deleteItem, "context.delete");
        deleteItem.setOnAction(e -> {
            Ticket selectedTicket = tableView.getSelectionModel().getSelectedItem();
            if (selectedTicket != null) {
                boolean confirmed = DialogUtils.showConfirmationDialog(
                        stage,
                        "dialog.delete.title",
                        "dialog.delete.header",
                        LocaleManager.getInstance().getString("dialog.delete.message") + selectedTicket.getId() + "?"
                );

                if (confirmed) {
                    executeCommand("remove_by_id", List.of(String.valueOf(selectedTicket.getId())));
                }
            }
        });

        contextMenu.getItems().setAll(editItem, deleteItem);
        tableView.setContextMenu(contextMenu);
    }

    private void setupAutoUpdate() {
        Timeline updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(UPDATE_INTERVAL), event -> loadTickets())
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }
} 