package SINU.sinu.generator;


import SINU.sinu.dto.GradeDTO;
import SINU.sinu.dto.StudentDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@RequiredArgsConstructor
public class FileGenerator implements FileStrategy {
    private FileStrategy fileStrategy;
    public byte[] generateData(List<GradeDTO> gradeDTOList, StudentDTO studentDTO){
        return fileStrategy.generateData(gradeDTOList, studentDTO);
    }
}
