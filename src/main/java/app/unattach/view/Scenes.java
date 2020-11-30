package app.unattach.view;

import app.unattach.model.Constants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public enum Scenes {
  MAIN, SIGN_IN;

  private static Stage stage;

  public static void init(Stage stage) {
    Scenes.stage = stage;
  }

  public static void setScene(Scenes scenes) throws IOException {
    Scene scene = switch (scenes) {
      case MAIN -> loadScene("/main.view.fxml");
      case SIGN_IN -> loadScene("/sign-in.view.fxml");
    };
    Stage newStage = createNewStage();
    newStage.setScene(scene);
    stage.hide();
    stage = newStage;
    showAndPreventMakingSmaller(stage);
  }

  private static Stage createNewStage() {
    return createNewStage(null);
  }

  static Stage createNewStage(String title) {
    if (title == null) {
      title = Constants.PRODUCT_NAME + " (" + Constants.VERSION + ")";
    }
    Stage newStage = new Stage();
    newStage.setTitle(title);
    newStage.getIcons().add(new Image(Scenes.class.getResourceAsStream("/logo-256.png")));
    return newStage;
  }

  static void showAndPreventMakingSmaller(Stage stage) {
    stage.show();
    stage.setMinHeight(stage.getHeight());
    stage.setMinWidth(stage.getWidth());
  }

  static Scene loadScene(String resource) throws IOException {
    FXMLLoader loader = new FXMLLoader(Scenes.class.getResource(resource));
    Parent root = loader.load();
    Scene scene = new Scene(root);
    scene.setUserData(loader.getController());
    return scene;
  }
}
