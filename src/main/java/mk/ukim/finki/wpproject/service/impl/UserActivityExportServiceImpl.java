package mk.ukim.finki.wpproject.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import mk.ukim.finki.wpproject.model.dto.UserActivityDto;
import mk.ukim.finki.wpproject.service.UserActivityExportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserActivityExportServiceImpl implements UserActivityExportService {

    @Override
    public void exportToCSV(List<UserActivityDto> userActivities, Writer writer) throws IOException {
        CSVFormat csvFormat = CSVFormat.DEFAULT
                .withHeader("Username", "Full Name", "Entries Created", "Last Activity", 
                           "Labels Used", "Entities Used", "Type Breakdown", "Tone Breakdown");

        try (CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            for (UserActivityDto activity : userActivities) {
                String typeBreakdown = activity.typeBreakdown()
                        .stream()
                        .map(t -> t.name() + ": " + t.count())
                        .collect(Collectors.joining("; "));

                String toneBreakdown = activity.toneBreakdown()
                        .stream()
                        .map(t -> t.name() + ": " + t.count())
                        .collect(Collectors.joining("; "));

                printer.printRecord(
                        activity.username(),
                        activity.fullName(),
                        activity.entriesCreated(),
                        activity.lastActivityDate() != null ? activity.lastActivityDate().toString() : "—",
                        activity.labelsUsed(),
                        activity.entitiesUsed(),
                        typeBreakdown.isEmpty() ? "—" : typeBreakdown,
                        toneBreakdown.isEmpty() ? "—" : toneBreakdown
                );
            }
        }
    }

    @Override
    public byte[] exportToPDF(List<UserActivityDto> userActivities) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Paragraph title = new Paragraph("User Activity Report", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Create table
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);

            // Headers
            String[] headers = {"Username", "Full Name", "Entries", "Last Activity", "Labels", "Entities", "Type Breakdown", "Tone Breakdown"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Rows
            for (UserActivityDto activity : userActivities) {
                String typeBreakdown = activity.typeBreakdown()
                        .stream()
                        .map(t -> t.name() + ": " + t.count())
                        .collect(Collectors.joining("\n"));

                String toneBreakdown = activity.toneBreakdown()
                        .stream()
                        .map(t -> t.name() + ": " + t.count())
                        .collect(Collectors.joining("\n"));

                table.addCell(activity.username());
                table.addCell(activity.fullName());
                table.addCell(String.valueOf(activity.entriesCreated()));
                table.addCell(activity.lastActivityDate() != null ? activity.lastActivityDate().toString() : "—");
                table.addCell(String.valueOf(activity.labelsUsed()));
                table.addCell(String.valueOf(activity.entitiesUsed()));
                table.addCell(typeBreakdown.isEmpty() ? "—" : typeBreakdown);
                table.addCell(toneBreakdown.isEmpty() ? "—" : toneBreakdown);
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new IOException("Error generating PDF", e);
        }

        return baos.toByteArray();
    }
}
