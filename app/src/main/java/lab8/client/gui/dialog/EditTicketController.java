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
import lab8.shared.ticket.Ticket;
import lab8.shared.ticket.TicketType;
import lombok.Setter;

import java.util.List;
import java.util.stream.Stream;

public class EditTicketController extends AbstractDialogController {

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
    private Text titleText;
    @FXML
    private javafx.scene.control.Label nameLabel;
    @FXML
    private javafx.scene.control.Label coordinateXLabel;
    @FXML
    private javafx.scene.control.Label coordinateYLabel;
    @FXML
    private javafx.scene.control.Label priceLabel;
    @FXML
    private javafx.scene.control.Label refundableLabel;
    @FXML
    private javafx.scene.control.Label typeLabel;
    @FXML
    private javafx.scene.control.Label passportIdLabel;
    @FXML
    private javafx.scene.control.Label hairColorLabel;
    @FXML
    private javafx.scene.control.Label birthdayLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    @Setter
    private SharedClient client;
    @Setter
    private UserCredentials credentials;
    @Setter
    private MainController mainController;
    private Ticket ticket;

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        typeBox.getItems().setAll(TicketType.values());
        typeBox.getSelectionModel().selectFirst();
        statusScrollPane.managedProperty().bind(statusScrollPane.visibleProperty());
        hairColorBox.getItems().setAll(Color.values());
        hairColorBox.getSelectionModel().selectFirst();
        setupLocalization();
    }

    private void setupLocalization() {
        titleText.textProperty().bind(LocalizationUtils.createStringBinding("dialog.edit.title"));

        // Локализация меток
        LocalizationUtils.localizeLabel(nameLabel, "dialog.add.name");
        LocalizationUtils.localizeLabel(coordinateXLabel, "dialog.add.coordinate_x");
        LocalizationUtils.localizeLabel(coordinateYLabel, "dialog.add.coordinate_y");
        LocalizationUtils.localizeLabel(priceLabel, "dialog.add.price");
        LocalizationUtils.localizeLabel(refundableLabel, "dialog.add.refundable");
        LocalizationUtils.localizeLabel(typeLabel, "dialog.add.type");
        LocalizationUtils.localizeLabel(passportIdLabel, "dialog.add.passport_id");
        LocalizationUtils.localizeLabel(hairColorLabel, "dialog.add.color");
        LocalizationUtils.localizeLabel(birthdayLabel, "dialog.add.birthday");

        // Локализация кнопок
        LocalizationUtils.localizeButton(cancelButton, "button.cancel");
        LocalizationUtils.localizeButton(saveButton, "button.save");

        // Локализация подсказок
        nameField.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.name.prompt"));
        coordinateXField.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.coordinate_x.prompt"));
        coordinateYField.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.coordinate_y.prompt"));
        priceField.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.price.prompt"));
        typeBox.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.type.prompt"));
        passportIdField.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.passport_id.prompt"));
        birthdayDatePicker.promptTextProperty().bind(LocalizationUtils.createStringBinding("dialog.add.birthday.prompt"));
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        populateFields();
    }

    private void populateFields() {
        if (ticket != null) {
            nameField.setText(ticket.getName());
            coordinateXField.setText(String.valueOf(ticket.getCoordinates().getX()));
            coordinateYField.setText(String.valueOf(ticket.getCoordinates().getY()));
            priceField.setText(String.valueOf(ticket.getPrice()));
            refundableBox.setSelected(ticket.getRefundable());
            typeBox.setValue(ticket.getType());
            passportIdField.setText(ticket.getPerson().getPassportID());
            hairColorBox.setValue(ticket.getPerson().getHairColor());
            birthdayDatePicker.setValue(ticket.getPerson().getBirthday().toLocalDate());
        }
    }

    @FXML
    public void handleSave() {
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

            if (Stream.of(name, x, y, price, type, passportId).anyMatch(String::isEmpty)) {
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

            List<String> args = List.of(
                    String.valueOf(ticket.getId()),
                    name, x, y, price, refundable, type, passportId, hairColor, birthday
            );
            Request request = new Request("update", args, credentials);

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