package hu.unideb;

import hu.unideb.service.BatchProcessor;
import hu.unideb.service.StatisticsService;
import hu.unideb.report.PdfReport;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // 1️⃣ Mappa elérési út
        System.out.println("Add meg a feldolgozandó mappa elérési útját:");
        String folderPath = scanner.nextLine();

        // 2️⃣ Pixel-to-mm skála
        double scale = 0;
        boolean valid = false;

        while (!valid) {
            System.out.println("Add meg a pixel-to-mm átváltási arányt (pl. 0.1):");
            String input = scanner.nextLine();

            try {
                scale = Double.parseDouble(input);
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Érvénytelen bemenet, kérlek adj meg egy számot.");
            }
        }

        System.out.println("Feldolgozás indul...");

        // 3️⃣ Batch feldolgozás
        List<Double> diameters =
                BatchProcessor.processFolder(folderPath, scale);

        if (diameters.isEmpty()) {
            System.out.println("Nem található feldolgozható kép vagy nem detektált pórust.");
            return;
        }

        // 4️⃣ Statisztika
        double avg = StatisticsService.average(diameters);

        System.out.println("=================================");
        System.out.println("Detektált pórusok száma: " + diameters.size());
        System.out.println("Átlag átmérő (mm): " + avg);
        System.out.println("=================================");

        try {
            // 5️⃣ CSV export
            String csvPath = new File(folderPath, "results.csv").getAbsolutePath();
            StatisticsService.exportCsv(diameters, csvPath);
            System.out.println("CSV mentve ide: " + csvPath);

            // 6️⃣ PDF riport
            String pdfPath = new File(folderPath, "report.pdf").getAbsolutePath();
            PdfReport.generate(pdfPath,
                    diameters.size(),
                    avg,
                    0); // Porozitás most nincs összesítve batch-ben

            System.out.println("PDF riport mentve ide: " + pdfPath);

        } catch (Exception e) {
            System.out.println("Hiba a riport generálásakor:");
            e.printStackTrace();
        }

        System.out.println("Feldolgozás kész.");
    }
}