package hu.unideb.model;

import java.util.List;

public class DetectionResult {

    public List<Double> diameters;
    public double totalArea;
    public double imageArea;

    public DetectionResult(List<Double> diameters,
                           double totalArea,
                           double imageArea) {
        this.diameters = diameters;
        this.totalArea = totalArea;
        this.imageArea = imageArea;
    }
}
