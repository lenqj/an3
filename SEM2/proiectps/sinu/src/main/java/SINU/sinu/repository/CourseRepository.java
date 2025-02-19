package SINU.sinu.repository;

import SINU.sinu.entities.Professor.Course;
import SINU.sinu.entities.Professor.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findCourseByName(@Param("name") String name);
}
