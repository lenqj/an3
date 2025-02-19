package SINU.sinu.mapper;

import SINU.sinu.dto.ExamDTO;
import SINU.sinu.entities.Professor.Exam;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ExamMapper {

    private final CourseMapper courseMapper;
    private final GradeMapper gradeMapper;

    public ExamMapper(@Lazy CourseMapper courseMapper, @Lazy GradeMapper gradeMapper) {
        this.courseMapper = courseMapper;
        this.gradeMapper = gradeMapper;
    }

    public ExamDTO toDTO(Exam exam) {
        return ExamDTO.builder()
                .name(exam.getName())
                .course(exam.getCourse() != null ? courseMapper.toDTO(exam.getCourse()) : null)
                .build();
    }

    public Exam toEntity(ExamDTO examDTO) {
        return Exam.builder()
                .name(examDTO.getName())
                .course(examDTO.getCourse() != null ? courseMapper.toEntity(examDTO.getCourse()) : null)
                .build();
    }
}
