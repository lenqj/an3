package SINU.sinu.mapper;

import SINU.sinu.dto.GradeDTO;
import SINU.sinu.entities.Grade;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class GradeMapper {
    private final ExamMapper examMapper;
    private final StudentMapper studentMapper;

    public GradeMapper(@Lazy ExamMapper examMapper, @Lazy StudentMapper studentMapper) {
        this.examMapper = examMapper;
        this.studentMapper = studentMapper;
    }

    public GradeDTO toDTO(Grade grade) {
        return GradeDTO.builder()
                .examDTO(examMapper.toDTO(grade.getExam()))
                .studentDTO(studentMapper.toDTO(grade.getStudent()))
                .grade(grade.getGrade())
                .build();
    }

    public Grade toEntity(GradeDTO gradeDTO, String password) {
        return Grade.builder()
                .exam(examMapper.toEntity(gradeDTO.getExamDTO()))
                .student(studentMapper.toEntity(gradeDTO.getStudentDTO(), password))
                .grade(gradeDTO.getGrade())
                .build();
    }
}
