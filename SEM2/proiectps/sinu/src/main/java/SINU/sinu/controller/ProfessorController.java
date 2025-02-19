package SINU.sinu.controller;

import SINU.sinu.dto.CourseDTO;
import SINU.sinu.dto.ExamDTO;
import SINU.sinu.dto.GradeDTO;
import SINU.sinu.dto.StudentDTO;
import SINU.sinu.entities.Grade;
import SINU.sinu.entities.Professor.Course;
import SINU.sinu.entities.Professor.Exam;
import SINU.sinu.entities.Professor.Professor;
import SINU.sinu.entities.Student.Student;
import SINU.sinu.entities.Student.StudentGroup;
import SINU.sinu.entities.Student.StudyYear;
import SINU.sinu.mapper.CourseMapper;
import SINU.sinu.mapper.ExamMapper;
import SINU.sinu.mapper.ProfessorMapper;
import SINU.sinu.mapper.StudentMapper;
import SINU.sinu.repository.GradeRepository;
import SINU.sinu.repository.StudentRepository;
import SINU.sinu.service.*;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Controller class responsible for handling professor-related operations in the admin panel.
 */
@Controller
@AllArgsConstructor
@RequestMapping("api/professor")
public class ProfessorController {
    static final Logger logger = Logger.getLogger(AdminController.class.getName());
    private final CourseService courseService;
    private final ExamService examService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final ExamMapper examMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CourseMapper courseMapper;
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;


    /**
     * Initializes the controller by creating sample courses, professors, and exams.
     */
    @PostConstruct
    public void init() {
        int numberOfCourses = 5;
        for (int i = 1; i <= numberOfCourses; i++) {
            Professor professor = Professor.builder()
                    .username("professor" + i)
                    .firstName("John" + i)
                    .lastName("Doe" + i)
                    .emailAddress("professor" + i + "@example.com")
                    .password(passwordEncoder.encode("Password1234!"))
                    .build();
            professorService.createProfessor(professor);
            Course course = Course.builder()
                    .name("course" + i)
                    .description("description" + i)
                    .professor(professor)
                    .build();
            courseService.createCourse(course);
            Exam exam = Exam.builder()
                    .name("Exam" + i)
                    .course(course)
                    .build();
            examService.createExam(exam);
        }
    }

    /**
     * Displays the index page for the professor section.
     */
    @GetMapping("")
    public String professorIndex(){
        return "professor/index";
    }

    /**
     * Displays the index page for the professor course section.
     */
    @GetMapping("/course")
    public String professorCourseIndex(){
        return "professor/course/index";
    }

    /**
     * Retrieves all courses and adds them to the model, then returns the view for displaying all courses.
     */
    @GetMapping("/course/view-all")
    public String getAllCourses(Model model) {
        List<Course> courses = courseService.findAllCourses();
        model.addAttribute("courses", courses);
        return "professor/course/all";
    }

    /**
     * Displays the form for creating a new course.
     */
    @GetMapping("/course/create")
    public String createCourse(Model model) {
        model.addAttribute("courseDTO", new CourseDTO());
        model.addAttribute("professors", professorService.findAllProfessors());
        return "professor/course/create";
    }

    /**
     * Processes the creation of a new course.
     */
    @PostMapping("/course/create")
    public String createCourse(@ModelAttribute @Valid CourseDTO courseDTO, BindingResult errors, Model model, @RequestParam String courseProfessor) {

        Optional<Professor> professor = professorService.findProfessorByUsername(courseProfessor);
        if(!errors.hasErrors() && professor.isPresent()) {
            Course course = courseMapper.toEntity(courseDTO);
            course.setProfessor(professor.get());
            try {
                courseService.createCourse(course);
                logger.info("Course created with success!");
            }
            catch (DataIntegrityViolationException ex) {
                if(ex.getMessage().contains("course_name_key"))  {
                    errors.rejectValue("name", "error.course.create", "Name is already in use!");
                    logger.info("Name is already in use!");
                }
            }
        }
        else {
            errors.rejectValue("professor", "error.course.create", "You must select a professor!");
            logger.warning("Professor not selected");
        }
        if (errors.hasErrors()) {
            model.addAttribute("errors", errors);
            model.addAttribute("courseDTO", courseDTO);
            model.addAttribute("professors", professorService.findAllProfessors());
            return "professor/course/create";
        }
        return "redirect:/api/professor/course/view-all";
    }

    /**
     * Displays the form for deleting courses.
     */
    @GetMapping("/course/delete")
    public String deleteCourse(Model model) {
        List<Course> courses = courseService.findAllCourses();
        model.addAttribute("courses", courses);
        return "professor/course/delete";
    }

    /**
     * Processes the deletion of selected courses.
     */
    @PostMapping("/course/delete")
    public String deleteCourse(@RequestParam(required = false) String[] courseNames) {
        if (courseNames != null) {
            for (String courseName : courseNames) {
                Optional<Course> course = courseService.findCourseByName(courseName);
                course.ifPresent(courseService::deleteCourse);
                logger.info("Selected courses deleted with success!");

            }
        }else{
            logger.info("You must select a course");
        }
        return "redirect:/api/professor/course/view-all";
    }

    /**
     * Displays the form for updating a specific course.
     */
    @RequestMapping(value = "/course/update/{courseName}", method = {RequestMethod.GET, RequestMethod.POST})
    public String updateCourse(@PathVariable("courseName") String courseName, Model model) {
        model.addAttribute("course", courseService.findCourseByName(courseName));
        model.addAttribute("professors", professorService.findAllProfessors());
        model.addAttribute("students", studentService.findAllStudents());
        return "professor/course/update";
    }

    /**
     * Processes the update of a course.
     */
    @PostMapping("/course/update")
    public String updateCourse(@ModelAttribute @Valid Course course, BindingResult errors, Model model, @RequestParam String courseProfessor, @RequestParam List<String> courseStudents) {
        Optional<Course> optionalCourse = courseService.findCourseByName(course.getName());
        Optional<Professor> professor = professorService.findProfessorByUsername(courseProfessor);
        List<Student> students = new ArrayList<>();
        for(String studentName : courseStudents){
            Optional<Student> student = studentRepository.findStudentByUsername(studentName);
            students.add(student.orElse(null));
        }
        if(professor.isPresent()) {
            if(optionalCourse.isPresent()) {
                Course courseToUpdate = optionalCourse.get();
                courseToUpdate.setDescription(course.getDescription());
                courseToUpdate.setProfessor(professor.get());
                courseToUpdate.setStudents(students);
                try {
                    courseService.updateCourse(courseToUpdate);
                    logger.info("Course update with success!");
                } catch (DataIntegrityViolationException ex) {
                    if (ex.getMessage().contains("course_name_key")) {
                        errors.rejectValue("name", "error.course.create", "Name is already in use!");
                        logger.info("Name is already in use!");
                    }
                }
            }else{
                errors.rejectValue("course", "error.course.create", "Course not found!");
                logger.info("Course not found!");
            }
        }else{
            logger.info("You must select a professor first!");
            errors.rejectValue("professor", "error.course.create", "You must select a professor first!");
        }
        if (errors.hasErrors()) {
            model.addAttribute("errors", errors);
            model.addAttribute("course", course);
            model.addAttribute("professors", professorService.findAllProfessors());
            model.addAttribute("students", studentService.findAllStudents());
            return "professor/course/update";
        }
        return "redirect:/api/professor/course/view-all";
    }

    /**
     * Displays the index page for the professor exam section.
     */
    @GetMapping("/exam")
    public String professorExamIndex(){
        return "professor/exam/index";
    }

    /**
     * Retrieves all exams and adds them to the model, then returns the view for displaying all exams.
     */
    @GetMapping("/exam/view-all")
    public String getAllExams(Model model) {
        List<ExamDTO> exams = examService.findAllExams();
        model.addAttribute("exams", exams);
        return "professor/exam/all";
    }

    /**
     * Displays the form for creating a new exam.
     */
    @GetMapping("/exam/create")
    public String createExam(Model model) {
        model.addAttribute("exam", new Exam());
        model.addAttribute("courses", courseService.findAllCourses());
        return "professor/exam/create";
    }

    /**
     * Processes the creation of a new exam.
     */
    @PostMapping("/exam/create")
    public String createExam(@ModelAttribute @Valid Exam exam, BindingResult errors, Model model, @RequestParam String examCourse) {
        Optional<Course> course = courseService.findCourseByName(examCourse);
        if(course.isPresent()) {
            exam.setCourse(course.get());
            try {
                examService.createExam(exam);
                logger.info("Exam created with success!");
            } catch (DataIntegrityViolationException ex) {
                if (ex.getMessage().contains("exam_name_key")) {
                    errors.addError(new ObjectError("course", "Name is already in use!"));
                    logger.info("Name is already in use!");
                }
            }
        }else{
            errors.addError(new ObjectError("course", "You must select a course!"));
            logger.info("You must select a course!");
        }
        if (errors.hasErrors()) {
            model.addAttribute("errors", errors);
            model.addAttribute("exam", exam);
            model.addAttribute("courses", courseService.findAllCourses());
            return "professor/exam/create";
        }
        return "redirect:/api/professor/exam/view-all";
    }

    /**
     * Displays the form for deleting exams.
     */
    @GetMapping("/exam/delete")
    public String deleteExam(Model model) {
        List<ExamDTO> exams = examService.findAllExams();
        model.addAttribute("exams", exams);
        return "professor/exam/delete";
    }

    /**
     * Processes the deletion of selected exams.
     */
    @PostMapping("/exam/delete")
    public String deleteExam(@RequestParam(required = false) String[] examNames) {
        if (examNames != null) {
            for (String examName : examNames) {
                Optional<Exam> exam = examService.findExamByName(examName);
                exam.ifPresent(examService::deleteExam);
            }
        }
        return "redirect:/api/professor/exam/view-all";
    }

    /**
     * Displays the form for updating a specific exam.
     */
    @RequestMapping(value = "/exam/update/{examName}", method = {RequestMethod.GET, RequestMethod.POST})
    public String updateExam(@PathVariable("examName") String examName, Model model) {
        model.addAttribute("exam", examService.findExamByName(examName));
        model.addAttribute("courses", courseService.findAllCourses());
        return "professor/exam/update";
    }

    /**
     * Processes the update of an exam.
     */
    @PostMapping("/exam/update")
    public String updateExam(@ModelAttribute @Valid ExamDTO exam, BindingResult errors, Model model, @RequestParam String examCourse) {
        Optional<Exam> optionalExamForId = examService.findExamByName(exam.getName());

        if(optionalExamForId.isPresent()) {
            Exam examForId = optionalExamForId.get();
            Exam examToUpdate = examMapper.toEntity(exam);
            Optional<Course> optionalCourse = courseService.findCourseByName(examCourse);
            examToUpdate.setID(examForId.getID());
            if(optionalCourse.isPresent()) {
                examToUpdate.setCourse(optionalCourse.get());
                try {
                    examService.updateExam(examToUpdate);
                    logger.info("Exam update with success!");
                } catch (DataIntegrityViolationException ex) {
                    if (ex.getMessage().contains("exam_student_list_student_list_id_key")) {
                        errors.rejectValue("students", "error.exam.update", "Student already exists!");
                        logger.info("Student already exists!");
                    }
                }
            }
            else {
                errors.addError(new ObjectError("course", "You must select a course!"));
                logger.info("You must select a course!");
            }
        }
        if (errors.hasErrors()) {
            model.addAttribute("errors", errors);
            model.addAttribute("exam", exam);
            model.addAttribute("courses", courseService.findAllCourses());
            return "professor/exam/update";
        }
        return "redirect:/api/professor/exam/view-all";
    }

    /**
     * Displays the form for adding a grade to a student.
     */
    @GetMapping("/exam/add-grade")
    public String addGradeForStudent(Model model) {
        model.addAttribute("students", studentService.findAllStudents());
        model.addAttribute("exams", examService.findAllExams());
        return "professor/exam/add-grade";
    }

    /**
     * Processes the addition of a grade for a student.
     */
    @PostMapping("/exam/add-grade")
    public String addGradeForStudent(@RequestParam String examName, @RequestParam String studentName, @RequestParam Double grade, Model model) {
        Optional<Exam> optionalExam = examService.findExamByName(examName);
        Optional<Student> optionalStudent = studentService.findStudentByUsername(studentName);
        if(optionalExam.isPresent()) {
            Exam exam = optionalExam.get();
            if(optionalStudent.isPresent()) {
                Student student = optionalStudent.get();
                boolean isEnrolled = student.getCourses().stream()
                        .anyMatch(course -> course.getExams().contains(exam));

                if (isEnrolled) {
                    try {
                        gradeRepository.save(Grade.builder()
                                .student(student)
                                .exam(exam)
                                .grade(grade)
                                .build());
                        logger.info("Grade has been added with success!");
                    } catch (IllegalArgumentException ex) {
                        logger.info(ex.getMessage());
                    }
                }
                else {
                    logger.warning("Student " + studentName + " is not enrolled in the course associated with exam " + examName);
                }
            }else {
                logger.warning("Student " + studentName + " doesn't exist");
            }
        }else{
            logger.warning("Exam " + examName + " doesn't exist");
        }
        return "redirect:/api/professor/exam";
    }

}