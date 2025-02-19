package SINU.sinu.mapper;

import SINU.sinu.dto.CourseDTO;
import SINU.sinu.dto.ExamDTO;
import SINU.sinu.entities.Professor.Course;
import SINU.sinu.entities.Professor.Exam;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CourseMapper {
    private final ProfessorMapper professorMapper;
    private final StudentMapper studentMapper;
    private final ExamMapper examMapper;


    public CourseMapper(@Lazy ProfessorMapper professorMapper, @Lazy StudentMapper studentMapper, @Lazy ExamMapper examMapper) {
        this.professorMapper = professorMapper;
        this.studentMapper = studentMapper;
        this.examMapper = examMapper;
    }

    public CourseDTO toDTO(Course course) {
        return CourseDTO.builder()
                .name(course.getName())
                .description(course.getDescription())
                .build();
    }

    public Course toEntity(CourseDTO courseDTO) {
        return Course.builder()
                .name(courseDTO.getName())
                .description(courseDTO.getDescription())
                .build();
    }

}
