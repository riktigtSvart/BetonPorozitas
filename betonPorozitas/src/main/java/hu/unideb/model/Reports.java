package hu.unideb.model;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;

public class Reports {

    public static void generatePdf(String path,
                                int count,
                                double avg)
            throws Exception {

        Document doc = new Document();
        PdfWriter.getInstance(doc,
                new FileOutputStream(path));

        doc.open();

        doc.add(new Paragraph(
                "BETON PÓRUS MINŐSÉGELLENŐRZÉSI JELENTÉS"));
        doc.add(new Paragraph(
                "Detektált pórusok száma: " + count));
        doc.add(new Paragraph(
                "Átlag átmérő (mm): " + avg));

        doc.close();
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
