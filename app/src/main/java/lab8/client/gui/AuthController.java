package lab8.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lab8.client.gui.localization.LocaleManager;
import lab8.client.gui.localization.LocalizationUtils;
import lab8.client.gui.util.SceneManager;
import lab8.client.network.SharedClient;
import lab8.shared.io.connection.Request;
import lab8.shared.io.connection.Response;
import lab8.shared.io.connection.UserCredentials;
import lombok.Setter;

public class AuthController {

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab loginTab;
    @FXML
    private Tab registerTab;
    @FXML
    private TextField loginUsername;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginStatus;
    @FXML
    private Label usernameLoginLabel;
    @FXML
    private Label passwordLoginLabel;

    @FXML
    private TextField registerUsername;
    @FXML
    private PasswordField registerPassword;
    @FXML
    private PasswordField registerPasswordConfirm;
    @FXML
    private Button registerButton;
    @FXML
    private Label registerStatus;
    @FXML
    private Label usernameRegisterLabel;
    @FXML
    private Label passwordRegisterLabel;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private Label appTitleLabel;
    @FXML
    private Label appSubtitleLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private HBox localeBox;

    @Setter
    private SharedClient client;
    @Setter
    private SceneManager sceneManager;

    @FXML
    private void initialize() {
        initStatus(loginStatus);
        initStatus(registerStatus);

        setupLocalization();
    }

    private void initStatus(Label loginStatus) {
        loginStatus.setVisible(false);
        loginStatus.setManaged(false);
        loginStatus.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean hasText = newValue != null && !newValue.isEmpty();
            loginStatus.setVisible(hasText);
            loginStatus.setManaged(hasText);
        });
    }

    private void setupLocalization() {
        LocalizationUtils.LocaleComboBox localeComboBox = new LocalizationUtils.LocaleComboBox();
        localeComboBox.setPrefWidth(150);
        localeBox.getChildren().add(localeComboBox);

        LocalizationUtils.localizeTab(loginTab, "auth.login.tab");
        LocalizationUtils.localizeTab(registerTab, "auth.register.tab");

        LocalizationUtils.localizeLabel(usernameLoginLabel, "auth.username");
        LocalizationUtils.localizeLabel(passwordLoginLabel, "auth.password");
        LocalizationUtils.localizeButton(loginButton, "auth.login.button");
        loginUsername.promptTextProperty().bind(LocalizationUtils.createStringBinding("auth.username.prompt"));
        loginPassword.promptTextProperty().bind(LocalizationUtils.createStringBinding("auth.password.prompt"));

        LocalizationUtils.localizeLabel(usernameRegisterLabel, "auth.username");
        LocalizationUtils.localizeLabel(passwordRegisterLabel, "auth.password");
        LocalizationUtils.localizeLabel(confirmPasswordLabel, "auth.confirm.password");
        LocalizationUtils.localizeButton(registerButton, "auth.register.button");
        registerUsername.promptTextProperty().bind(LocalizationUtils.createStringBinding("auth.username.prompt"));
        registerPassword.promptTextProperty().bind(LocalizationUtils.createStringBinding("auth.password.prompt"));
        registerPasswordConfirm.promptTextProperty().bind(LocalizationUtils.createStringBinding("auth.confirm.password.prompt"));

        LocalizationUtils.localizeLabel(appTitleLabel, "auth.app.title");
        LocalizationUtils.localizeLabel(appSubtitleLabel, "auth.app.subtitle");

        LocalizationUtils.localizeLabel(languageLabel, "locale.language");
    }

    @FXML
    private void handleLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            String errorText = LocaleManager.getInstance().getString("auth.error.fill.all");
            loginStatus.setText(errorText);
            return;
        }

        UserCredentials credentials = new UserCredentials(username, password);
        Request request = new Request("login", credentials);

        try {
            Response response = client.sendReceive(request);
            if (response != null && response.getMessage() != null &&
                    response.getMessage().contains("Login completed successfully")) {
                String successText = LocaleManager.getInstance().getString("auth.login.success");
                loginStatus.setText(successText);
                openMainWindow(credentials);
            } else {
                String errorText = LocaleManager.getInstance().getString("auth.login.error") +
                        (response != null ? response.getMessage() : LocaleManager.getInstance().getString("status.no_response"));
                loginStatus.setText(errorText);
            }
        } catch (Exception e) {
            String errorText = LocaleManager.getInstance().getString("auth.connection.error") + e.getMessage();
            loginStatus.setText(errorText);
        }
    }

    @FXML
    private void handleRegister() {
        String username = registerUsername.getText().trim();
        String password = registerPassword.getText();
        String passwordConfirm = registerPasswordConfirm.getText();

        if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            String errorText = LocaleManager.getInstance().getString("auth.error.fill.all");
            registerStatus.setText(errorText);
            return;
        }

        if (!password.equals(passwordConfirm)) {
            String errorText = LocaleManager.getInstance().getString("auth.error.passwords.mismatch");
            registerStatus.setText(errorText);
            return;
        }

        UserCredentials credentials = new UserCredentials(username, password);
        Request request = new Request("registration", credentials);

        try {
            Response response = client.sendReceive(request);
            if (response != null && response.getMessage() != null &&
                    response.getMessage().contains("successfully registered")) {
                String successText = LocaleManager.getInstance().getString("auth.register.success");
                registerStatus.setText(successText);
                tabPane.getSelectionModel().select(0);
                loginUsername.setText(username);
                loginPassword.setText(password);
            } else {
                String errorText = LocaleManager.getInstance().getString("auth.register.error") +
                        (response != null ? response.getMessage() : LocaleManager.getInstance().getString("status.no_response"));
                registerStatus.setText(errorText);
            }
        } catch (Exception e) {
            String errorText = LocaleManager.getInstance().getString("auth.connection.error") + e.getMessage();
            registerStatus.setText(errorText);
        }
    }

    private void openMainWindow(UserCredentials credentials) {
        try {
            sceneManager.showScene(
                    "/ui/main/main.fxml",
                    LocaleManager.getInstance().getString("app.title"),
                    javafx.stage.StageStyle.DECORATED,
                    (MainController ctrl) -> {
                        ctrl.setClient(client);
                        ctrl.setCredentials(credentials);
                        ctrl.setSceneManager(sceneManager);
                        ctrl.setStage((sceneManager).getPrimaryStage());
                        ctrl.loadTickets();
                    },
                    false,
                    null
            );
        } catch (Exception e) {
            String errorText = LocaleManager.getInstance().getString("auth.error.main.window") + e.getMessage();
            loginStatus.setText(errorText);
            System.out.println(e.getMessage());
        }
    }
} 