package SINU.sinu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDTO {
    private ExamDTO examDTO;
    private StudentDTO studentDTO;
    private Double grade;
}
