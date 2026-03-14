package hu.unideb.service;

import hu.unideb.detection.DetectionResult;
import hu.unideb.detection.PorusDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BatchProcessor {

    private static final Logger logger = Logger.getLogger(BatchProcessor.class.getName());

    public static List<Double> processFolder(String folder, double scale) {

        List<Double> all = new ArrayList<>();
        File dir = new File(folder);

        // Mappa ellenőrzés
        if (!dir.exists() || !dir.isDirectory()) {
            logger.severe("Hibás vagy nem létező mappa: " + folder);
            return all;
        }

        File[] files = dir.listFiles();

        // Olvashatóság ellenőrzés
        if (files == null) {
            logger.severe("A mappa nem olvasható: " + folder);
            return all;
        }

        logger.info("Feldolgozás indul: " + folder);

        for (File f : files) {

            if (!f.isFile()) {
                continue; // Almappákat kihagyjuk
            }

            String name = f.getName().toLowerCase();

            if (name.endsWith(".jpg") ||
                    name.endsWith(".jpeg") ||
                    name.endsWith(".png")) {

                try {
                    logger.info("Feldolgozás: " + f.getName());

                    // OS-független output path
                    String output = new File(folder, "out_" + f.getName()).getAbsolutePath();

                    DetectionResult r = PorusDetector.detect(
                            f.getAbsolutePath(),
                            output,
                            scale
                    );

                    // Csak diameters ellenőrzés szükséges
                    if (r.diameters != null) {
                        all.addAll(r.diameters);
                    }

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Hiba a file feldolgozásakor: " + f.getName(), e);
                }
            }
        }

        logger.info("Feldolgozás kész. Összes detektált pórus: " + all.size());

        return all;
    }
}