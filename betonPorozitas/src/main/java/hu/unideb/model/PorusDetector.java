package hu.unideb.model;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PorusDetector {

    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static final Logger logger = Logger.getLogger(PorusDetector.class.getName());

    private static final double MIN_AREA = 20;
    private static final double MAX_AREA = 150;
    private static final Scalar RECT_COLOR = new Scalar(0, 0, 255);
    private static final int RECT_THICKNESS = 2;

    public static DetectionResult detect(String inputPath,
                                         String outputPath,
                                         double pixelToMm) {

        Mat image = Imgcodecs.imread(inputPath);

        if (image.empty()) {
            logger.severe("Nem sikerült beolvasni a képet: " + inputPath);
            return new DetectionResult(new ArrayList<>());
        }

        double imageArea = image.width() * image.height();

        Mat processed = preprocess(image);

        List<Double> diameters = new ArrayList<>();
        double totalArea = 0;

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(processed, contours, new Mat(),
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);

            if (area >= MIN_AREA && area <= MAX_AREA) {
                totalArea += area;
                double diameterPx = 2 * Math.sqrt(area / Math.PI);
                diameters.add(diameterPx * pixelToMm);

                Rect r = Imgproc.boundingRect(contour);
                Imgproc.rectangle(image, r, RECT_COLOR, RECT_THICKNESS);
            }
        }

        boolean saved = Imgcodecs.imwrite(outputPath, image);
        if (!saved) {
            logger.warning("Nem sikerült menteni a képet: " + outputPath);
        }

        logger.info("Detektálás kész. Kontúrok száma: " + contours.size());

        return new DetectionResult(diameters);
    }

    private static Mat preprocess(Mat image) {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

        Mat thresh = new Mat();
        Imgproc.adaptiveThreshold(gray, thresh, 255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY_INV,
                11, 2);

        return thresh;
    }

    public static double average(List<Double> list) {
        return list.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
    }
}
