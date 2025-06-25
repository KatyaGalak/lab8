package lab8.client.gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lab8.client.gui.dialog.AbstractDialogController;
import lombok.Getter;

import java.util.function.Consumer;

public class SceneManagerImpl implements SceneManager {
    @Getter
    private final Stage primaryStage;
    private boolean wasShown = false;
    private Stage currentModalStage;

    public SceneManagerImpl(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public <T> void showScene(String fxmlPath, String title, StageStyle style, Consumer<T> controllerSetup, boolean modal, Stage owner) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        if (controllerSetup != null) {
            controllerSetup.accept(controller);
        }

        if (modal) {
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initStyle(style);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(true);

            stage.sizeToScene();

            double prefWidth = root.prefWidth(-1) + 50;  // +50 для рамок и отступов
            double prefHeight = root.prefHeight(-1) + 50;

            stage.setMinWidth(Math.min(prefWidth, 450));
            stage.setMinHeight(Math.min(prefHeight, 550));

            if (owner != null) {
                stage.initOwner(owner);
            }

            stage.sizeToScene();

            stage.initModality(Modality.WINDOW_MODAL);
            this.currentModalStage = stage;

            if (controller instanceof AbstractDialogController) {
                ((AbstractDialogController) controller).setDialogStage(scene);
            }

            stage.setOnCloseRequest(event -> {
                if (currentModalStage == stage) {
                    currentModalStage = null;
                }
            });

            stage.showAndWait();
        } else {
            primaryStage.setTitle(title);
            if (!wasShown) {
                primaryStage.initStyle(style);
                wasShown = true;
            }
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
    }

    @Override
    public void closeCurrent() {
        primaryStage.close();
    }

    @Override
    public void closeModalWindow() {
        if (currentModalStage != null) {
            currentModalStage.close();
            currentModalStage = null;
        }
    }
} 