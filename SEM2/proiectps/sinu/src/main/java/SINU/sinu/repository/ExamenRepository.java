package SINU.sinu.repository;

import SINU.sinu.entities.Professor.Exam;
import SINU.sinu.entities.Student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamenRepository extends JpaRepository<Exam,Integer> {
    Optional<Exam> findByName(String name);
}
