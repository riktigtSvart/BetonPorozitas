package hu.unideb.service;

import hu.unideb.detection.DetectionResult;

import java.util.List;

public class StatisticsService {

    public static double average(List<Double> list) {
        return list.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
    }

    public static double porosity(DetectionResult r) {
        return (r.totalPorusArea / r.imageArea) * 100.0;
    }

}
