package SINU.sinu.dto;

import SINU.sinu.entities.Student.StudentGroup;
import SINU.sinu.entities.Student.StudyYear;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO extends MyUserDTO{
    private StudentGroup studentGroup;
    private StudyYear studyYear;
    private List<CourseDTO> courseDTOList;
    private List<GradeDTO> gradeDTOList;
}
