package SINU.sinu.controller;

import SINU.sinu.dto.ProfessorDTO;
import SINU.sinu.dto.StudentDTO;
import SINU.sinu.entities.MyUser;
import SINU.sinu.entities.Professor.Professor;
import SINU.sinu.entities.Student.Student;
import SINU.sinu.entities.Student.StudentGroup;
import SINU.sinu.entities.Student.StudyYear;
import SINU.sinu.mapper.ProfessorMapper;
import SINU.sinu.mapper.StudentMapper;
import SINU.sinu.service.*;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Controller class responsible for handling admin-related operations.
 */
@Controller
@AllArgsConstructor
@RequestMapping("api/admin")
public class AdminController {
    static final Logger logger = Logger.getLogger(AdminController.class.getName());
    private final StudentService studentService;
    private final MyUserService myUserService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final StudentMapper studentMapper;
    private final ProfessorService professorService;
    private final ProfessorMapper professorMapper;
    private final CourseService courseService;

    /**
     * Initializes some default users upon the creation of the controller.
     */
    @PostConstruct
    public void init() {
        int numberOfStudents = 5;
        for (int i = 1; i <= numberOfStudents; i++) {
            MyUser myUser = MyUser.builder()
                    .username("admin" + i)
                    .firstName("John" + i)
                    .lastName("Doe" + i)
                    .emailAddress("admin" + i + "@example.com")
                    .password(passwordEncoder.encode("Password1234!"))
                    .build();
            myUserService.save(myUser);
        }
        logger.info("Initialized default users");
    }

    /**
     * Displays the index page for the student section in the admin panel.
     *
     * @return the view for the student index page
     */
    @GetMapping()
    public String adminIndex(){
        return "admin/index";
    }



    /**
     * Displays the index page for the student section in the admin panel.
     *
     * @return the view for the student index page
     */
    @GetMapping("student")
    public String studentIndex(){
        return "admin/student/index";
    }

    /**
     * Retrieves all students and adds them to the model, then returns the view for displaying all students.
     *
     * @param model the model to which the list of students is added
     * @return the view for displaying all students
     */
    @GetMapping("student/view-all")
    public String getAllStudents(Model model){
        List<StudentDTO> students = studentService.findAllStudents();
        model.addAttribute("students", students);
        logger.info("Retrieved all students");
        return "admin/student/all";
    }

    /**
     * Displays the form for creating a new student.
     *
     * @param model the model to which the student object and other necessary attributes are added
     * @return the view for creating a new student
     */
    @GetMapping("student/create")
    public String createStudent(Model model){
        model.addAttribute("student", new Student());
        model.addAttribute("groups", StudentGroup.values());
        model.addAttribute("years", StudyYear.values());
        return "admin/student/create";
    }

    /**
     * Processes the creation of a new student.
     * If successful, redirects to the admin panel page; otherwise, displays the creation form with errors.
     *
     * @param student the student object to be created
     * @param errors the binding result containing potential validation errors
     * @param model the model to which errors and attributes are added
     * @return the view for creating a new student or redirects to the admin panel page
     */
    @PostMapping("student/create")
    public String createStudent(@ModelAttribute @Valid Student student, BindingResult errors, Model model){
        try {
            studentService.createStudent(student);
            logger.info("Student created successfully: " + student.getUsername());
        }
        catch (DataIntegrityViolationException ex) {
            if(ex.getMessage().contains("my_user_username_key"))  {
                errors.rejectValue("username", "error.student.create", "Username is already in use!");
                logger.info("Username address is already in use!");
            }else{
                if (ex.getMessage().contains("my_user_email_key")) {
                    errors.rejectValue("email", "error.student.create", "Email address is already in use!");
                    logger.info("Email address is already in use!");
                }
            }
        }
        if (errors.hasErrors()) {
            model.addAttribute("errors", errors);
            model.addAttribute("groups", StudentGroup.values());
            model.addAttribute("years", StudyYear.values());
            return "admin/student/create";
        }
        return "redirect:/api/admin/student/view-all";
    }

    /**
     * Displays the form for deleting students.
     *
     * @param model the model to which the list of students is added
     * @return the view for deleting students
     */
    @GetMapping("student/delete")
    public String deleteStudent(Model model){
        model.addAttribute("students", studentService.findAllStudents());
        return "admin/student/delete";
    }

    /**
     * Processes the deletion of selected students.
     *
     * @param studentUsernames the array of usernames of the students to be deleted
     * @return redirects to the admin panel page
     */
    @PostMapping("student/delete")
    public String deleteStudent(@RequestParam(required = false) String[] studentUsernames) {
        if(studentUsernames != null){
            for(String username : studentUsernames){
                Optional<Student> student = studentService.findStudentByUsername(username);
                student.ifPresent(studentService::deleteStudent);
            }
            logger.info("Selected students deleted successfully");
        }else{
            logger.info("No students selected for deletion");
        }
        return "redirect:/api/admin/student/view-all";
    }

    /**
     * Displays the form for updating a specific student.
     *
     * @param studentUsername the username of the student to be updated
     * @param model the model to which the student object and other necessary attributes are added
     * @return the view for updating a student
     */
    @RequestMapping(value = "student/update/{studentUsername}", method = {RequestMethod.PUT, RequestMethod.GET, RequestMethod.POST})
    public String updateStudent(@PathVariable("studentUsername") String studentUsername, Model model) {
        model.addAttribute("student", studentService.findStudentByUsername(studentUsername));
        model.addAttribute("groups", StudentGroup.values());
        model.addAttribute("years", StudyYear.values());
        return "admin/student/update";
    }

    /**
     * Processes the update of a student.
     *
     * @param studentDTO the DTO object containing the updated student information
     * @param errors the binding result containing potential validation errors
     * @param userPassword the password provided for the update
     * @param model the model to which errors and attributes are added
     * @return redirects to the admin panel page
     */
    @RequestMapping("student/update")
    public String createStudent(@ModelAttribute @Valid StudentDTO studentDTO, BindingResult errors, @RequestParam(required = false) String userPassword, Model model) {
        Optional<Student> optionalStudentForId = studentService.findStudentByUsername(studentDTO.getUsername());
        if(optionalStudentForId.isPresent()) {
            Student studentForId = optionalStudentForId.get();
            Student studentToUpdate = studentMapper.toEntity(studentDTO, userPassword == null ? studentForId.getPassword() : passwordEncoder.encode(userPassword));
            studentToUpdate.setID(studentForId.getID());

            try {
                studentService.updateStudent(studentToUpdate);
                logger.info("Student updated successfully: " + studentDTO.getUsername());
            } catch (DataIntegrityViolationException ex) {
                if (ex.getMessage().contains("student_username_key")) {
                    errors.rejectValue("username", "error.student.update", "Username is already in use!");
                    logger.info("Username address is already in use!");
                } else {
                    if (ex.getMessage().contains("student_email_key")) {
                        errors.rejectValue("email", "error.student.update", "Email address is already in use!");
                        logger.info("Email address is already in use!");
                    }
                }
            }
            if (errors.hasErrors()) {
                model.addAttribute("errors", errors);
                model.addAttribute("student", studentDTO);
                model.addAttribute("groups", StudentGroup.values());
                model.addAttribute("years", StudyYear.values());
                return "admin/student/update";
            }
        }
        return "redirect:/api/admin/student/view-all";
    }

    /**
     * Displays the index page for the professor section in the admin panel.
     *
     * @return the view for the professor index page
     */
    @GetMapping("professor")
    public String indexProfessor(){
        return "admin/professor/index";
    }

    /**
     * Retrieves all professors and adds them to the model, then returns the view for displaying all professors.
     *
     * @param model the model to which the list of professors is added
     * @return the view for displaying all professors
     */
    @GetMapping("professor/view-all")
    public String getAllProfessors(Model model){
        List<ProfessorDTO> professors = professorService.findAllProfessors();
        model.addAttribute("professors", professors);
        return "admin/professor/all";
    }

    /**
     * Displays the form for creating a new professor.
     *
     * @param model the model to which the professor object and other necessary attributes are added
     * @return the view for creating a new professor
     */
    @GetMapping("professor/create")
    public String createProfessor(Model model){
        model.addAttribute("professor", new Professor());
        model.addAttribute("courses", courseService.findAllCourses());
        return "admin/professor/create";
    }

    /**
     * Processes the creation of a new professor.
     * If successful, redirects to the admin panel page; otherwise, displays the creation form with errors.
     *
     * @param professor the professor object to be created
     * @param errors the binding result containing potential validation errors
     * @param model the model to which errors and attributes are added
     * @return the view for creating a new professor or redirects to the admin panel page
     */
    @PostMapping("professor/create")
    public String createProfessor(@ModelAttribute @Valid Professor professor, BindingResult errors, @RequestParam String selectedCourse, Model model){
        try {
            professor.addCourse(courseService.findCourseByName(selectedCourse).orElse(null));
            professorService.createProfessor(professor);
            logger.info("Professor created successfully: " + professor.getUsername());
        }
        catch (DataIntegrityViolationException ex) {
            if(ex.getMessage().contains("my_user_username_key"))  {
                errors.rejectValue("username", "error.professor.create", "Username is already in use!");
                logger.info("Username address is already in use!");
            }else{
                if (ex.getMessage().contains("my_user_email_key")) {
                    errors.rejectValue("email", "error.professor.create", "Email address is already in use!");
                    logger.info("Email address is already in use!");
                }
            }
        }
        if (errors.hasErrors()) {
            model.addAttribute("errors", errors);
            model.addAttribute("courses", courseService.findAllCourses());
            return "admin/professor/create";
        }
        return "redirect:/api/admin/professor/view-all";

    }

    /**
     * Displays the form for deleting professors.
     *
     * @param model the model to which the list of professors is added
     * @return the view for deleting professors
     */
    @GetMapping("professor/delete")
    public String deleteProfessor(Model model){
        model.addAttribute("professors", professorService.findAllProfessors());
        return "admin/professor/delete";
    }

    /**
     * Processes the deletion of selected professors.
     *
     * @param professorUsernames the array of usernames of the professors to be deleted
     * @return redirects to the admin panel page
     */
    @PostMapping("professor/delete")
    public String deleteProfessor(@RequestParam(required = false) String[] professorUsernames) {
        if (professorUsernames != null) {
            for (String username : professorUsernames) {
                Optional<Professor> professor = professorService.findProfessorByUsername(username);
                professor.ifPresent(professorService::deleteProfessor);
            }
            logger.info("Selected professors deleted successfully");
        } else {
            logger.info("No professors selected for deletion");
        }
        return "redirect:/api/admin/professor/view-all";
    }

    /**
     * Displays the form for updating a specific professor.
     *
     * @param professorUsername the username of the professor to be updated
     * @param model the model to which the professor object and other necessary attributes are added
     * @return the view for updating a professor
     */
    @RequestMapping(value = "professor/update/{professorUsername}", method = {RequestMethod.PUT, RequestMethod.GET, RequestMethod.POST})
    public String updateProfessor(@PathVariable("professorUsername") String professorUsername, Model model) {
        model.addAttribute("professor", professorService.findProfessorByUsername(professorUsername));
        model.addAttribute("courses", courseService.findAllCourses());
        return "admin/professor/update";
    }

    /**
     * Processes the update of a professor.
     *
     * @param professorDTO the DTO object containing the updated professor information
     * @param errors the binding result containing potential validation errors
     * @param userPassword the password provided for the update
     * @param model the model to which errors and attributes are added
     * @return redirects to the admin panel page
     */
    @RequestMapping("professor/update")
    public String createProfessor(@ModelAttribute @Valid ProfessorDTO professorDTO, BindingResult errors, @RequestParam(required = false) String userPassword, @RequestParam String selectedCourse, Model model) {
        Optional<Professor> optionalProfessorForId = professorService.findProfessorByUsername(professorDTO.getUsername());
        if(optionalProfessorForId.isPresent()) {
            Professor professorForId = optionalProfessorForId.get();
            Professor professorToUpdate = professorMapper.toEntity(professorDTO, userPassword == null ? professorForId.getPassword() : userPassword);
            professorToUpdate.setID(professorForId.getID());
            professorToUpdate.addCourse(courseService.findCourseByName(selectedCourse).orElse(null));
            try {
                professorService.updateProfessor(professorToUpdate);
                logger.info("Professor updated successfully: " + professorDTO.getUsername());
            } catch (DataIntegrityViolationException ex) {
                if (ex.getMessage().contains("professor_username_key")) {
                    errors.rejectValue("username", "error.professor.update", "Username is already in use!");
                    logger.info("Username address is already in use!");
                } else {
                    if (ex.getMessage().contains("professor_email_key")) {
                        errors.rejectValue("email", "error.professor.update", "Email address is already in use!");
                        logger.info("Email address is already in use!");
                    }
                }
            }
            if (errors.hasErrors()) {
                model.addAttribute("errors", errors);
                model.addAttribute("professor", professorDTO);
                model.addAttribute("courses", courseService.findAllCourses());
                return "admin/professor/update";
            }
        }
        return "redirect:/api/admin/professor/view-all";
    }
}
