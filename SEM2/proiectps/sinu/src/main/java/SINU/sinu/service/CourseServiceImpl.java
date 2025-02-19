package SINU.sinu.service;

import SINU.sinu.entities.Professor.Course;
import SINU.sinu.entities.Professor.Exam;
import SINU.sinu.entities.Professor.Professor;
import SINU.sinu.entities.Student.Student;
import SINU.sinu.repository.CourseRepository;
import SINU.sinu.repository.ExamenRepository;
import SINU.sinu.repository.ProfessorRepository;
import SINU.sinu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final ExamenRepository examenRepository;
    private final StudentRepository studentRepository;

    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Optional<Course> findCourseByID(Integer courseId) {
        return courseRepository.findById(courseId);
    }

    @Override
    public Optional<Course> findCourseByName(String courseName) {
        return courseRepository.findCourseByName(courseName);
    }

    @Override
    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Course course) {
        Professor professor = course.getProfessor();
        professor.getCourseList().remove(course);
        professorRepository.save(professor);
        for(Exam exam : course.getExams()) {
            exam.setCourse(null);
            examenRepository.save(exam);
        }
        for(Student student : course.getStudents()) {
            student.getCourses().remove(course);
            studentRepository.save(student);
        }
        courseRepository.delete(course);
    }
}
