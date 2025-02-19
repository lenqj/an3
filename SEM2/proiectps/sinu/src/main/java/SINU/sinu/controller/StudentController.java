package SINU.sinu.controller;

import SINU.sinu.dto.GradeDTO;
import SINU.sinu.dto.StudentDTO;
import SINU.sinu.entities.EmailPayload;
import SINU.sinu.entities.Grade;
import SINU.sinu.entities.MyUser;
import SINU.sinu.entities.Student.Student;
import SINU.sinu.mapper.GradeMapper;
import SINU.sinu.mapper.StudentMapper;
import SINU.sinu.repository.GradeRepository;
import SINU.sinu.service.EmailService;
import SINU.sinu.service.MyUserService;
import SINU.sinu.service.StudentService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Controller class responsible for handling user-related operations.
 */
@Controller
@AllArgsConstructor
@RequestMapping("api/student")
public class StudentController {
    static final Logger logger = Logger.getLogger(StudentController.class.getName());
    private final StudentService studentService;
    private final MyUserService myUserService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;

    /**
     * Initializes some default users upon the creation of the controller.
     */
    @PostConstruct
    public void init() {
        int numberOfStudents = 5;
        for (int i = 1; i <= numberOfStudents; i++) {
            Student student = Student.builder()
                    .username("student" + i)
                    .firstName("John" + i)
                    .lastName("Doe" + i)
                    .emailAddress("student" + i + "@example.com")
                    .password(passwordEncoder.encode("Password1234!"))
                    .build();
            myUserService.save(student);
        }
    }

    /**
     * Displays the index page for the student section.
     */
    @GetMapping()
    public String indexForStudent(){
        return "student/index";
    }

    /**
     * Retrieves all users and adds them to the model, then returns the view for displaying all users.
     */
    @GetMapping("view-all")
    public String findAllStudents(Model model){
        model.addAttribute("students", studentService.findAllStudents());
        return "student/all";
    }

    /**
     * Retrieves the grades for a specific student and adds them to the model, then returns the view for displaying grades.
     */
    @GetMapping("view-grades")
    public String studentGrades(Model model){
        String userUsername = myUserService.getLoginUser().getUsername();
        if(userUsername == null)
            return "redirect:/api/student/view-all";
        model.addAttribute("grades", studentService.findAllGrades(userUsername));
        model.addAttribute("username", userUsername);
        return "student/grade/index";
    }

    @PostMapping("/generate-grades")
    public String  generateGrades(@RequestParam String username, @RequestParam String fileType) {
        try {
            studentService.studentGradesGenerator(username, fileType);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return "redirect:/api/student/view-all";
        }
    }
    @GetMapping("all-grades")
    @ResponseBody
    public ResponseEntity getAllGrades() {
        List<GradeDTO> gradeDTOList = gradeRepository.findAll().stream()
                .map(gradeMapper::toDTO)
                .toList();
        if (gradeDTOList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Grades not found");
        } else {
            return ResponseEntity.ok(gradeDTOList);
        }
    }
}

