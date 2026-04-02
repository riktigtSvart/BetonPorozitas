package hu.unideb.controller;

import hu.unideb.model.PorusDetectorModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;

public class PorusDetectorController {

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
    private PorusDetectorModel porusDetector;

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
            return 0.1;
        }
    }

    @FXML
    public void handleExecute(ActionEvent event) {

        if (selectedFile == null) {
            result.setText("Nincs kiválasztott kép!");
            return;
        }

        Image image = originalImage.getImage();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader reader = image.getPixelReader();
        WritableImage output = new WritableImage(width, height);
        PixelWriter writer = output.getPixelWriter();

        int pores = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color color = reader.getColor(x, y);

                double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

                if (gray < 0.5) {
                    writer.setColor(x, y, Color.BLACK);
                    pores++;
                } else {
                    writer.setColor(x, y, Color.WHITE);
                }
            }
        }

        processedImage.setImage(output);

        double scale = getScale();

        result.setText("Pórusok száma: " + pores + " | scale: " + scale);
    }
}
