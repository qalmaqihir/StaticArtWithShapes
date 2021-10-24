package CanvasArtShape;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PaneLauncher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ArtPane.fxml"));

        Scene scene = new Scene(root, 900,550);
        stage.setTitle("Static Art with Pane Node");
        stage.setScene(scene);
        stage.show();

    }
}
