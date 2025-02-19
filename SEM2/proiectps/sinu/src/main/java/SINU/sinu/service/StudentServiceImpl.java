package SINU.sinu.service;

import SINU.sinu.dto.GradeDTO;
import SINU.sinu.dto.StudentDTO;
import SINU.sinu.entities.EmailPayload;
import SINU.sinu.entities.Grade;
import SINU.sinu.entities.Professor.Course;
import SINU.sinu.entities.Professor.Exam;
import SINU.sinu.entities.Student.Student;
import SINU.sinu.generator.CSVFileStrategy;
import SINU.sinu.generator.FileGenerator;
import SINU.sinu.generator.PDFFileStrategy;
import SINU.sinu.generator.TXTFileStrategy;
import SINU.sinu.mapper.GradeMapper;
import SINU.sinu.mapper.StudentMapper;
import SINU.sinu.repository.CourseRepository;
import SINU.sinu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;
    private final GradeMapper gradeMapper;
    private final EmailService emailService;

    @Override
    public StudentDTO createStudent(Student student) {
        return studentMapper.toDTO(studentRepository.save(student));
    }
    @Override
    public StudentDTO findStudentDTOByID(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(studentMapper::toDTO).orElse(null);
    }
    @Override
    public StudentDTO findStudentDTOByEmail(String email) {
        Optional<Student> student = studentRepository.findStudentByEmailAddress(email);
        return student.map(studentMapper::toDTO).orElse(null);
    }
    @Override
    public Optional<Student> findStudentByUsername(String username) {
        return studentRepository.findStudentByUsername(username);
    }
    @Override
    public Optional<Student> findStudentByID(Integer studentId) {
        return studentRepository.findById(studentId);
    }
    @Override
    public List<StudentDTO> findAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toDTO)
                .toList();
    }
    @Override
    public StudentDTO updateStudent(Student student) {
            return studentMapper.toDTO(studentRepository.save(student));
    }
    @Override
    public void deleteStudent(Student student) {
        for (Course course : student.getCourses()) {
            course.getStudents().remove(student);
            courseRepository.save(course);
        }
        studentRepository.delete(student);
    }

    @Override
    public List<Grade> findAllGrades(String username) {
        Optional<Student> optionalStudent = studentRepository.findStudentByUsername(username);
        return optionalStudent.map(Student::getGrades).orElse(null);
    }
    @Override
    public void studentGradesGenerator(String username, String fileType){
        FileGenerator fileGenerator = new FileGenerator();
        if(fileType.equals("TXT")){
            fileGenerator.setFileStrategy(new TXTFileStrategy());
        }else if(fileType.equals("CSV")){
            fileGenerator.setFileStrategy(new CSVFileStrategy());
        }else{
            fileGenerator.setFileStrategy(new PDFFileStrategy());
        }
        Optional<Student> studentOptional = studentRepository.findStudentByUsername(username);
        StudentDTO studentDTO = studentOptional.map(studentMapper::toDTO).orElse(null);
        List<GradeDTO> gradeDTOs = studentOptional.map(student -> student.getGrades().stream()
                        .map(gradeMapper::toDTO)
                        .toList())
                .orElse(null);
        byte[] bytes = fileGenerator.generateData(gradeDTOs, studentDTO);
        EmailPayload emailPayload = new EmailPayload();
        emailPayload.setBody("Email with attach");
        emailPayload.setFirstName(studentDTO.getFirstName());
        emailPayload.setLastName(studentDTO.getLastName());
        emailPayload.setSubject("Mail with attach");
        emailPayload.setRecipient(studentDTO.getEmailAddress());
        emailPayload.setAttachment(bytes);
        emailPayload.setFileType(fileType.toLowerCase());
        emailService.sendEmail(emailPayload);
    }
}
