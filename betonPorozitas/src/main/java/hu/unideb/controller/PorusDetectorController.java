package hu.unideb.controller;

import hu.unideb.model.DetectionResult;
import hu.unideb.model.PorusDetectorModel;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;

import java.io.File;

public class PorusDetectorController {
    private PorusDetectorModel porusDetector;

    @FXML
    private Button choose;

    @FXML
    private Button execute;

    @FXML
    private Label result;

    @FXML
    private ImageView originalImage;

    @FXML
    private ImageView processedImage;

    @FXML
    private TextField fileLocation;

    @FXML
    private TextField pxToMm;

    private File selectedFile;

    @FXML
    public void initialize() {
        porusDetector = new PorusDetectorModel();
    }

    @FXML
    public void handleChoose(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.png", "*.jpeg", "*.gif", "*.bmp")
        );

        selectedFile = fileChooser.showOpenDialog(originalImage.getScene().getWindow());

        if (selectedFile != null) {
            fileLocation.setText(selectedFile.getAbsolutePath());

            Image image = new Image(selectedFile.toURI().toString());
            originalImage.setImage(image);
        }
    }

    public double getScale() {
        try {
            return Double.parseDouble(pxToMm.getText());
        } catch (Exception e) {
            result.setText("Hibás pixel/mm érték!");
            return 0.1; // default
        }
    }

    @FXML
    public void handleExecute(ActionEvent event) {

        if (selectedFile == null) {
            result.setText("Nincs kiválasztott kép!");
            return;
        }

        double scale = getScale();

        DetectionResult detectionResult =
                porusDetector.processSingleFile(
                        selectedFile.getAbsolutePath(),
                        scale
                );

        if (detectionResult == null) {
            result.setText("Hiba történt!");
            return;
        }

        Image processed = new Image(
                new File(detectionResult.getOutputPath())
                        .toURI().toString()
        );
        processedImage.setImage(processed);

        porusDetector.statistics(
                selectedFile.getParent(),
                detectionResult.diameters()
        );

        result.setText(
                "Pórusok száma: " + detectionResult.diameters().size()
                        + "\n CSV + PDF generálva"
        );
    }

    public void handleQuit(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Biztosan ki akar lépni?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Kilépés megerősítése");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Platform.exit();
            }
        });
    }
}
