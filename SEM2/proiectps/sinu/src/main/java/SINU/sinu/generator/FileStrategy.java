package SINU.sinu.generator;


import SINU.sinu.dto.GradeDTO;
import SINU.sinu.dto.StudentDTO;

import java.util.List;


public interface FileStrategy {
    byte[] generateData(List<GradeDTO> gradeDTOList, StudentDTO studentDTO);
}

