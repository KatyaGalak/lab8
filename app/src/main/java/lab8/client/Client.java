package lab8.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lab8.client.gui.AuthController;
import lab8.client.gui.localization.LocaleManager;
import lab8.client.gui.util.SceneManager;
import lab8.client.gui.util.SceneManagerImpl;
import lab8.client.network.SharedClient;

public class Client extends Application {
    private SharedClient client;
    private SceneManager sceneManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            LocaleManager.getInstance();

            client = new SharedClient();

            sceneManager = new SceneManagerImpl(primaryStage);

            sceneManager.showScene(
                    "/ui/auth/auth.fxml",
                    LocaleManager.getInstance().getString("auth.window.title"),
                    javafx.stage.StageStyle.DECORATED,
                    (AuthController ctrl) -> {
                        ctrl.setClient(client);
                        ctrl.setSceneManager(sceneManager);
                    },
                    false,
                    null
            );
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void stop() {
        if (client != null) {
            client.close();
        }
        Platform.exit();
    }
} 