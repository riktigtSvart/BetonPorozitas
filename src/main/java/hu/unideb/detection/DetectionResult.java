package hu.unideb.detection;

import java.util.List;

public class DetectionResult {

    public List<Double> diameters;
    public double totalPorusArea;
    public double imageArea;

    public DetectionResult(List<Double> diameters,
                           double totalPorusArea,
                           double imageArea) {
        this.diameters = diameters;
        this.totalPorusArea = totalPorusArea;
        this.imageArea = imageArea;
    }
}
