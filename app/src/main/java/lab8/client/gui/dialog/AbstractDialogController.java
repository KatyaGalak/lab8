package lab8.client.gui.dialog;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import lab8.client.gui.util.DialogController;
import lab8.client.gui.util.SceneManager;
import lab8.client.network.SharedClient;
import lombok.Setter;

/**
 * Абстрактный класс для контроллеров диалоговых окон
 */
public abstract class AbstractDialogController implements DialogController {

    @Setter
    protected SharedClient client;
    @Setter
    protected SceneManager sceneManager;

    @FXML
    protected ScrollPane statusScrollPane;
    @FXML
    protected Label statusLabel;

    protected Stage dialogStage;

    /**
     * Инициализирует диалоговое окно
     */
    @FXML
    public void initialize() {
        if (statusScrollPane != null) {
            statusScrollPane.managedProperty().bind(statusScrollPane.visibleProperty());
            statusScrollPane.setVisible(false);
        }
        applyDialogStyle();
    }

    /**
     * Устанавливает сцену для диалогового окна
     *
     * @param scene Сцена диалогового окна
     */
    public void setDialogStage(Scene scene) {
        this.dialogStage = (Stage) scene.getWindow();
    }

    /**
     * Применяет стиль к диалоговому окну
     */
    @Override
    public void applyDialogStyle() {
        // По умолчанию ничего не делаем
    }

    /**
     * Показывает сообщение о статусе
     *
     * @param message Сообщение для отображения
     */
    protected void showStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            if (statusScrollPane != null) {
                statusScrollPane.setVisible(true);
            }
        }
    }

    /**
     * Закрывает диалоговое окно
     */
    protected void closeDialog() {
        sceneManager.closeModalWindow();
    }

    /**
     * Обработчик кнопки отмены
     */
    @FXML
    public void handleCancel() {
        closeDialog();
    }
} 