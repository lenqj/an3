package SINU.sinu.service;

import SINU.sinu.entities.Professor.Course;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {
    Course createCourse(Course course);
    Optional<Course> findCourseByID(Integer courseId);
    Optional<Course> findCourseByName(String courseName);
    List<Course> findAllCourses();
    Course updateCourse(Course course);
    void deleteCourse(Course course);
}
