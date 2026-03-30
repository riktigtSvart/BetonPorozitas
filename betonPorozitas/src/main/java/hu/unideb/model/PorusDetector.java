package hu.unideb.model;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class PorusDetector {

    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static DetectionResult detect(String input,
                                         String output,
                                         double pixelToMm) {

        Mat image = Imgcodecs.imread(input);

        if (image.empty()) {
            System.err.println("Nem sikerült beolvasni a képet: " + input);
            return new DetectionResult(new ArrayList<>(), 0, 0);
        }

        double imageArea = image.width() * image.height();

        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(5,5), 0);

        Mat thresh = new Mat();
        Imgproc.adaptiveThreshold(gray, thresh, 255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY_INV,
                11, 2);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(thresh, contours,
                new Mat(),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE);

        List<Double> diameters = new ArrayList<>();
        double totalArea = 0;

        for (MatOfPoint contour : contours) {

            double area = Imgproc.contourArea(contour);

            if (area > 30 && area < 5000) {

                totalArea += area;

                double diameterPx =
                        2 * Math.sqrt(area / Math.PI);

                diameters.add(diameterPx * pixelToMm);

                Rect r = Imgproc.boundingRect(contour);
                Imgproc.rectangle(image, r,
                        new Scalar(0,0,255), 2);
            }
        }

        Imgcodecs.imwrite(output, image);

        return new DetectionResult(
                diameters, totalArea, imageArea);
    }
    public static double average(List<Double> list) {
        return list.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
    }
}
