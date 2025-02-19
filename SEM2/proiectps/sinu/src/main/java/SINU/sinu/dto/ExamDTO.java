package SINU.sinu.dto;

import SINU.sinu.entities.Grade;
import SINU.sinu.entities.Professor.Course;
import SINU.sinu.entities.Professor.Professor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO {
    private String name;
    private CourseDTO course;
    private List<GradeDTO> grades;
}
