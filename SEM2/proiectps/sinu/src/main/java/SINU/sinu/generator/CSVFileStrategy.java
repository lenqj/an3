package SINU.sinu.generator;

import SINU.sinu.dto.GradeDTO;
import SINU.sinu.dto.StudentDTO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CSVFileStrategy implements FileStrategy {

    @Override
    public byte[] generateData(List<GradeDTO> gradeDTOList, StudentDTO studentDTO) {
        try (FileWriter writer = new FileWriter(studentDTO.getUsername() + "_grades.csv")) {
            writer.append("Exam,Grade");
            writer.append("\n");
            if (!gradeDTOList.isEmpty()) {
                for (GradeDTO grade : gradeDTOList) {
                    writer.append(grade.getExamDTO().getName()).append(",").append(String.valueOf(grade.getGrade()));
                    writer.append("\n");
                }
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path pathToFile = Paths.get(studentDTO.getUsername() + "_grades.csv");
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
