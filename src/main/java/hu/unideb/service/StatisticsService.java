package hu.unideb.service;

import hu.unideb.detection.DetectionResult;

import java.io.FileWriter;
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

    public static void exportCsv(List<Double> diameters,
                                 String path) throws Exception {

        FileWriter writer = new FileWriter(path);
        writer.write("Diameter_mm\n");

        for (double d : diameters) {
            writer.write(d + "\n");
        }
        writer.close();
    }
}
