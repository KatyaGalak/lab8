package lab8.client.gui.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import lab8.client.gui.MainController;
import lab8.client.gui.localization.LocaleManager;
import lab8.client.gui.localization.LocalizationUtils;
import lab8.client.network.SharedClient;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.io.connection.UserCredentials;
import lab8.shared.ticket.Color;
import lab8.shared.ticket.TicketType;
import lombok.Setter;

import java.util.List;
import java.util.stream.Stream;

public class AddTicketController extends AbstractDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField coordinateXField;
    @FXML
    private TextField coordinateYField;
    @FXML
    private TextField priceField;
    @FXML
    private CheckBox refundableBox;
    @FXML
    private ComboBox<TicketType> typeBox;
    @FXML
    private TextField passportIdField;
    @FXML
    private ComboBox<Color> hairColorBox;
    @FXML
    private DatePicker birthdayDatePicker;
    @FXML
    private ComboBox<String> commandTypeBox;
    @FXML
    private Text titleText;
    @FXML
    private Label commandTypeLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label coordinateXLabel;
    @FXML
    private Label coordinateYLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label refundableLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label passportIdLabel;
    @FXML
    private Label hairColorLabel;
    @FXML
    private Label birthdayLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;

    @Setter
    private SharedClient client;
    @Setter
    private UserCredentials credentials;
    @Setter
    private MainController mainController;

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        typeBox.getItems().setAll(TicketType.values());
        typeBox.getSelectionModel().selectFirst();
        commandTypeBox.getItems().setAll("add", "add_if_max", "add_if_min");
        commandTypeBox.getSelectionModel().selectFirst();
        hairColorBox.getItems().setAll(Color.values());
        hairColorBox.getSelectionModel().selectFirst();
        setupLocalization();
    }

    private void setupLocalization() {
        titleText.textProperty().bind(LocalizationUtils.createStringBinding("dialog.add.title"));

        LocalizationUtils.localizeLabel(commandTypeLabel, "dialog.add.command_type");
        LocalizationUtils.localizeLabel(nameLabel, "dialog.add.name");
        LocalizationUtils.localizeLabel(coordinateXLabel, "dialog.add.coordinate_x");
        LocalizationUtils.localizeLabel(coordinateYLabel, "dialog.add.coordinate_y");
        LocalizationUtils.localizeLabel(priceLabel, "dialog.add.price");
        LocalizationUtils.localizeLabel(refundableLabel, "dialog.add.refundable");
        LocalizationUtils.localizeLabel(typeLabel, "dialog.add.type");
        LocalizationUtils.localizeLabel(passportIdLabel, "dialog.add.passport_id");
        LocalizationUtils.localizeLabel(hairColorLabel, "dialog.add.color");
        LocalizationUtils.localizeLabel(birthdayLabel, "dialog.add.birthday");

        LocalizationUtils.localizeButton(cancelButton, "button.cancel");
        LocalizationUtils.localizeButton(addButton, "button.add");

        commandTypeBox.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.command_type.prompt"));
        nameField.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.name.prompt"));
        coordinateXField.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.coordinate_x.prompt"));
        coordinateYField.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.coordinate_y.prompt"));
        priceField.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.price.prompt"));
        typeBox.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.type.prompt"));
        passportIdField.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.passport_id.prompt"));
        birthdayDatePicker.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.birthday.prompt"));
    }

    @FXML
    public void handleAdd() {
        try {
            String name = nameField.getText();
            String x = coordinateXField.getText();
            String y = coordinateYField.getText();
            String price = priceField.getText();
            String refundable = String.valueOf(refundableBox.isSelected());
            String type = typeBox.getValue() != null ? typeBox.getValue().name() : null;
            String passportId = passportIdField.getText();
            String hairColor = hairColorBox.getValue() != null ? hairColorBox.getValue().toString() : null;
            String birthday = birthdayDatePicker.getValue() != null ? birthdayDatePicker.getValue().toString() : null;

            String commandType = commandTypeBox.getValue() != null ? commandTypeBox.getValue() : "add";

            if (Stream.of(name, x, y, price, type, passportId, birthday).anyMatch(String::isEmpty)) {
                showStatus(LocaleManager.getInstance().getString("dialog.add.error.fill_fields"));
                return;
            }

            // Проверяем числовые поля
            try {
                Double.parseDouble(x);
                Float.parseFloat(y);
                Double.parseDouble(price);
                if (Double.parseDouble(price) <= 0) {
                    showStatus(LocaleManager.getInstance().getString("dialog.add.error.price_positive"));
                    return;
                }
            } catch (NumberFormatException e) {
                showStatus(LocaleManager.getInstance().getString("dialog.add.error.number_format"));
                return;
            }

            List<String> args = List.of(name, x, y, price, refundable, type, passportId, hairColor, birthday);
            Request request = new Request(commandType, args, credentials);

            Response response = client.sendReceive(request);

            if (response.isSuccess()) {
                mainController.loadTickets(); // Обновляем основную таблицу
                closeDialog();
            } else {
                showStatus(response.getMessage());
            }

        } catch (Exception e) {
            showStatus(LocaleManager.getInstance().getString("dialog.add.error.general") + e.getMessage());
        }
    }
} 