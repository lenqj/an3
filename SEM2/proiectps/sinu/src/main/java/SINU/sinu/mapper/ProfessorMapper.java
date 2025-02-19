package SINU.sinu.mapper;

import SINU.sinu.dto.ProfessorDTO;
import SINU.sinu.entities.Professor.Professor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProfessorMapper {
    private final CourseMapper courseMapper;

    public ProfessorMapper(@Lazy CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    public ProfessorDTO toDTO(Professor professor) {
        return ProfessorDTO.builder()
                .username(professor.getUsername())
                .firstName(professor.getFirstName())
                .lastName(professor.getLastName())
                .emailAddress(professor.getEmailAddress())
                .courseList(professor.getCourseList() != null ? professor.getCourseList().stream()
                        .map(courseMapper::toDTO)
                        .toList() : null)
                .build();
    }

    public Professor toEntity(ProfessorDTO professorDTO, String password) {
        return Professor.builder()
                .username(professorDTO.getUsername())
                .firstName(professorDTO.getFirstName())
                .lastName(professorDTO.getLastName())
                .emailAddress(professorDTO.getEmailAddress())
                .password(password)
                .courseList(professorDTO.getCourseList() != null ? professorDTO.getCourseList().stream()
                        .map(courseMapper::toEntity)
                        .toList() : null)
                .build();
    }
}
