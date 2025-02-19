package SINU.sinu.dto;

import SINU.sinu.entities.Professor.Course;
import SINU.sinu.entities.Professor.Exam;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDTO extends MyUserDTO {
    private List<CourseDTO> courseList;
}
