package SINU.sinu.service;

import SINU.sinu.dto.ProfessorDTO;
import SINU.sinu.dto.StudentDTO;
import SINU.sinu.entities.Professor.Professor;
import SINU.sinu.entities.Student.Student;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {
    ProfessorDTO createProfessor(Professor professor);
    Optional<Professor> findProfessorByID(Integer professorId);
    Optional<Professor> findProfessorByUsername(String username);
    ProfessorDTO findProfessorDTOByID(Integer professorId);
    ProfessorDTO findProfessorDTOByUsername(String username);
    List<ProfessorDTO> findAllProfessors();
    ProfessorDTO updateProfessor(Professor professor);
    void deleteProfessor(Professor professor);
}

