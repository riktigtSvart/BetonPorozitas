package hu.unideb.report;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;

public class PdfReport {

    public static void generate(String path,
                                int count,
                                double avg,
                                double porosity)
            throws Exception {

        Document doc = new Document();
        PdfWriter.getInstance(doc,
                new FileOutputStream(path));

        doc.open();

        doc.add(new Paragraph(
                "BETON PÓRUS MINŐSÉGELLENŐRZÉSI RIPORT"));
        doc.add(new Paragraph(
                "Detektált pórusok száma: " + count));
        doc.add(new Paragraph(
                "Átlag átmérő (mm): " + avg));
        doc.add(new Paragraph(
                "Porozitás (%): " + porosity));

        doc.close();
    }
}
