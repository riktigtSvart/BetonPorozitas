package hu.unideb.model;

import org.opencv.core.Core;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PorusDetectorModel {

    private static final Logger logger = Logger.getLogger(PorusDetectorModel.class.getName());

    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            logger.info("OpenCV betöltve: " + Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            logger.log(Level.SEVERE, "Nem sikerült betölteni az OpenCV-t!", e);
        }
    }

    public DetectionResult processSingleFile(String inputPath, double scale) {

        File input = new File(inputPath);

        if (!input.exists()) {
            logger.severe("A fájl nem létezik: " + inputPath);
            return null;
        }

        String outputPath = new File(
                input.getParent(),
                "processed_" + input.getName()
        ).getAbsolutePath();

        logger.info("Feldolgozás indul: " + inputPath);

        DetectionResult result = PorusDetector.detect(
                inputPath,
                outputPath,
                scale
        );

        result.setOutputPath(outputPath);

        if (result.diameters() == null || result.diameters().isEmpty()) {
            logger.warning("Nem detektált pórusokat.");
        } else {
            logger.info("Detektált pórusok száma: " + result.diameters().size());
        }

        return result;
    }

    public List<Double> batchProcessor(String folderPath, double scale) {

        List<Double> diameters = BatchProcessor.processFolder(folderPath, scale);

        if (diameters.isEmpty()) {
            logger.warning("Nem található feldolgozható kép, vagy nincs pórus.");
        } else {
            logger.info("Batch kész. Összes pórus: " + diameters.size());
        }

        return diameters;
    }

    public void statistics(String folderPath, List<Double> diameters) {

        if (diameters == null || diameters.isEmpty()) {
            logger.warning("Nincs adat statisztikához.");
            return;
        }

        double avg = PorusDetector.average(diameters);

        try {
            String csvPath = new File(folderPath, "results.csv").getAbsolutePath();
            Reports.exportCsv(diameters, csvPath);
            logger.info("CSV mentve: " + csvPath);

            String pdfPath = new File(folderPath, "report.pdf").getAbsolutePath();
            Reports.generatePdf(pdfPath, diameters.size(), avg);
            logger.info("PDF mentve: " + pdfPath);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Hiba riport generáláskor", e);
        }
    }
}
