package lab8.client.gui.util;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.function.Consumer;
 
public interface SceneManager {
    <T> void showScene(String fxmlPath, String title, StageStyle style, Consumer<T> controllerSetup, boolean modal, Stage owner) throws Exception;
    void closeCurrent();
    void closeModalWindow();
    Stage getPrimaryStage();
} 