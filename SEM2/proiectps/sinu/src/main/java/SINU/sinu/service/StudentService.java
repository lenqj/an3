package SINU.sinu.service;

import SINU.sinu.dto.GradeDTO;
import SINU.sinu.dto.StudentDTO;
import SINU.sinu.entities.Grade;
import SINU.sinu.entities.Student.Student;
import SINU.sinu.generator.FileStrategy;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentDTO createStudent(Student student);
    Optional<Student> findStudentByID(Integer studentId);
    Optional<Student> findStudentByUsername(String username);
    StudentDTO findStudentDTOByID(Integer studentId);
    StudentDTO findStudentDTOByEmail(String email);
    List<StudentDTO> findAllStudents();
    StudentDTO updateStudent(Student student);
    void deleteStudent(Student student);
    List<Grade> findAllGrades(String username);
    void studentGradesGenerator(String username, String fileType);
}
