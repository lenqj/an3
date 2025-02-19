package SINU.sinu.repository;

import SINU.sinu.entities.Professor.Professor;
import SINU.sinu.entities.Student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    Optional<Professor> findProfessorByUsername(String username);
}
