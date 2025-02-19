package SINU.sinu.repository;

import SINU.sinu.entities.MyUser;
import SINU.sinu.entities.Student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findStudentByUsername(String username);
    Optional<Student> findStudentByEmailAddress(String emailAddress);
}
