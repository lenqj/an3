package SINU.sinu.mapper;

import SINU.sinu.dto.MyUserAuthDTO;
import SINU.sinu.dto.MyUserDTO;
import SINU.sinu.dto.StudentDTO;
import SINU.sinu.entities.Student.Student;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
public class StudentMapper {
    private final GradeMapper gradeMapper;
    private final CourseMapper courseMapper;


    public StudentMapper(GradeMapper gradeMapper, CourseMapper courseMapper) {
        this.gradeMapper = gradeMapper;
        this.courseMapper = courseMapper;
    }

    public StudentDTO toDTO(Student student) {
        return StudentDTO.builder()
                .username(student.getUsername())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .emailAddress(student.getEmailAddress())
                .studentGroup(student.getStudentGroup())
                .studyYear(student.getStudyYear())
                .build();
    }
    public Student toEntity(StudentDTO studentDTO, String password) {
        return Student.builder()
                .username(studentDTO.getUsername())
                .firstName(studentDTO.getFirstName())
                .lastName(studentDTO.getLastName())
                .emailAddress(studentDTO.getEmailAddress())
                .studentGroup(studentDTO.getStudentGroup())
                .studyYear(studentDTO.getStudyYear())
                .password(password)
                .build();
    }

    public StudentDTO myUserAuthToStudentDTO(MyUserAuthDTO myUserAuthDTO) {
        return StudentDTO.builder()
                .username(myUserAuthDTO.getUsername())
                .firstName(myUserAuthDTO.getFirstName())
                .lastName(myUserAuthDTO.getLastName())
                .emailAddress(myUserAuthDTO.getEmailAddress())
                .build();
    }

    public StudentDTO toDTOWithCoursesAndGrades(Student student) {
        StudentDTO studentDTO = toDTO(student);
        studentDTO.setCourseDTOList(student.getCourses() == null ? null : student.getCourses().stream().map(courseMapper::toDTO).toList());
        studentDTO.setGradeDTOList(student.getGrades() == null ? null : student.getGrades().stream().map(gradeMapper::toDTO).toList());
        return studentDTO;
    }
    public Student toEntityWithCoursesAndGrades(StudentDTO studentDTO, String password) {
        Student student = toEntity(studentDTO, password);
        student.setCourses(studentDTO.getCourseDTOList() == null ? null : studentDTO.getCourseDTOList().stream().map(courseMapper::toEntity).toList());
        student.setGrades(studentDTO.getGradeDTOList() == null ? null : studentDTO.getGradeDTOList().stream().map(gradeDTO -> gradeMapper.toEntity(gradeDTO, password)).toList());
        return student;
    }
}
