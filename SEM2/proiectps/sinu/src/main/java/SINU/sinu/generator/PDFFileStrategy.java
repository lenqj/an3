package SINU.sinu.generator;
import SINU.sinu.dto.GradeDTO;
import SINU.sinu.dto.StudentDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PDFFileStrategy implements FileStrategy {
    @Override
    public byte[] generateData(List<GradeDTO> gradeDTOList, StudentDTO studentDTO) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(studentDTO.getUsername() + "_grades.pdf"));
            document.open();
            document.add(new Paragraph("Exam : Grade"));

            if (!gradeDTOList.isEmpty()){
                for (GradeDTO grade : gradeDTOList) {
                    document.add(new Paragraph(grade.getExamDTO().getName() + ": " + grade.getGrade()));
                }
            }
            document.close();
        } catch (DocumentException | IOException e) {
            System.err.println("Error generating PDF: " + e.getMessage());
        }
        Path pathToFile = Paths.get(studentDTO.getUsername() + "_grades.pdf");
        try (InputStream inputStream = Files.newInputStream(pathToFile)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
