package hu.unideb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class PorusDetectorApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("PorusDetector.fxml")));
        stage.setTitle("Porus Detector");
        stage.setResizable(true);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
