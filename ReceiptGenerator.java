
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.List;

public class ReceiptGenerator {
    public static void generatePDF(List<String> billLines, double total) {
        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream("receipt.pdf"));
            doc.open();

            doc.add(new Paragraph("Grocery Bill Receipt\n\n"));
            for (String line : billLines) {
                doc.add(new Paragraph(line));
            }

            doc.add(new Paragraph("\nTotal: ₹" + total));
            doc.add(new Paragraph("Thank you for shopping!"));
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
