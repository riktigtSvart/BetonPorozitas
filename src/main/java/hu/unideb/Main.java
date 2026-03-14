package hu.unideb;

import hu.unideb.report.Reports;
import hu.unideb.service.BatchProcessor;
import hu.unideb.service.StatisticsService;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Mappa elérési út
        System.out.println("Add meg a feldolgozandó mappa elérési útját:");
        String folderPath = scanner.nextLine();

        // Pixel-to-mm skála
        double scale = 0;
        boolean valid = false;

        while (!valid) {
            System.out.println("Add meg a pixel/mm átváltási arányt (pl. 0.1):");
            String input = scanner.nextLine();

            try {
                scale = Double.parseDouble(input);
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Érvénytelen bemenet, kérlek adj meg egy számot.");
            }
        }

        System.out.println("Feldolgozás indul...");

        // Batch feldolgozás
        List<Double> diameters =
                BatchProcessor.processFolder(folderPath, scale);

        if (diameters.isEmpty()) {
            System.out.println("Nem található feldolgozható kép, vagy nem detektált pórust.");
            return;
        }

        // Statisztika
        double avg = StatisticsService.average(diameters);

        System.out.println("=================================");
        System.out.println("Detektált pórusok száma: " + diameters.size());
        System.out.println("Átlag átmérő (mm): " + avg);
        System.out.println("=================================");

        try {
            // CSV export
            String csvPath = new File(folderPath, "results.csv").getAbsolutePath();
            Reports.exportCsv(diameters, csvPath);
            System.out.println("CSV mentve ide: " + csvPath);

            // PDF riport
            String pdfPath = new File(folderPath, "report.pdf").getAbsolutePath();
            Reports.generatePdf(pdfPath,
                    diameters.size(),
                    avg,
                    0); // Porozitás most nincs összesítve batch-ben

            System.out.println("PDF riport mentve ide: " + pdfPath);

        } catch (Exception e) {
            System.out.println("Hiba a riport generálásakor:");
            System.err.println(e.getMessage());
            //e.printStackTrace();
        }

        System.out.println("Feldolgozás kész.");
    }
}