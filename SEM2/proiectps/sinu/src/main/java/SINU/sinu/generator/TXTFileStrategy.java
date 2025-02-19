package SINU.sinu.generator;

import SINU.sinu.dto.GradeDTO;
import SINU.sinu.dto.StudentDTO;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TXTFileStrategy implements FileStrategy {
    @Override
    public byte[] generateData(List<GradeDTO> gradeDTOList, StudentDTO studentDTO) {
        try (FileWriter writer = new FileWriter(studentDTO.getUsername() + "_grades.txt")) {
            writer.append(gradeDTOList.toString()).append("\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path pathToFile = Paths.get(studentDTO.getUsername() + "_grades.txt");
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