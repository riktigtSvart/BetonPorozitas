package hu.unideb.model;

import org.opencv.core.Core;

import java.io.File;
import java.util.List;
import java.util.Scanner;



public class PorusDetectorModel {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    Scanner scanner = new Scanner(System.in);

    String folderPath = scanner.nextLine();

    public void batchProcessor(Double scale) {
        List<Double> diameters =
                BatchProcessor.processFolder(folderPath, scale);

        if (diameters.isEmpty()) {
            System.out.println("Nem található feldolgozható kép, vagy nem detektált pórust.");
            return;
        }
    }

    public void Statistics(List<Double> diameters) {
        double avg = PorusDetector.average(diameters);

        try {
            String csvPath = new File(folderPath, "results.csv").getAbsolutePath();
            Reports.exportCsv(diameters, csvPath);
            System.out.println("CSV mentve ide: " + csvPath);

            String pdfPath = new File(folderPath, "report.pdf").getAbsolutePath();
            Reports.generatePdf(pdfPath,
                    diameters.size(),
                    avg);

            System.out.println("PDF riport mentve ide: " + pdfPath);

        } catch (Exception e) {
            System.out.println("Hiba a riport generálásakor:");
            System.err.println(e.getMessage());
        }
    }

}
