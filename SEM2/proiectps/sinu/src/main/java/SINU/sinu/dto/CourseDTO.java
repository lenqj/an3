package SINU.sinu.dto;

import SINU.sinu.entities.Professor.Exam;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private String name;
    private String description;
    private List<StudentDTO> students;
    private List<ExamDTO> exams;
    private ProfessorDTO professorDTO;
}