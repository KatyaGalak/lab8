package lab8.client.gui.util;

import lab8.client.gui.MainController;
import lab8.client.network.SharedClient;
import lab8.shared.io.connection.UserCredentials;

/**
 * Общий интерфейс для контроллеров диалоговых окон
 */
public interface DialogController {
    
    /**
     * Устанавливает клиент для взаимодействия с сервером
     * @param client Клиент для взаимодействия с сервером
     */
    void setClient(SharedClient client);
    
    /**
     * Устанавливает учетные данные пользователя
     * @param credentials Учетные данные пользователя
     */
    void setCredentials(UserCredentials credentials);
    
    /**
     * Устанавливает менеджер сцен
     * @param sceneManager Менеджер сцен
     */
    void setSceneManager(SceneManager sceneManager);
    
    /**
     * Устанавливает контроллер основного окна
     * @param mainController Контроллер основного окна
     */
    void setMainController(MainController mainController);
    
    /**
     * Инициализирует диалоговое окно
     */
    void initialize();
    
    /**
     * Применяет стиль к диалоговому окну
     */
    default void applyDialogStyle() {
        // Реализация по умолчанию пустая
    }
} 