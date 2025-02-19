package SINU.sinu.service;

import SINU.sinu.dto.ProfessorDTO;
import SINU.sinu.entities.Professor.Course;
import SINU.sinu.entities.Professor.Professor;
import SINU.sinu.mapper.ProfessorMapper;
import SINU.sinu.repository.CourseRepository;
import SINU.sinu.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfessorServiceImpl implements ProfessorService {
    private final ProfessorRepository professorRepository;
    private final ProfessorMapper professorMapper;
    private final CourseRepository courseRepository;

    @Override
    public ProfessorDTO createProfessor(Professor professor) {
        return professorMapper.toDTO(professorRepository.save(professor));
    }

    @Override
    public Optional<Professor> findProfessorByID(Integer professorId) {
        return professorRepository.findById(professorId);
    }

    @Override
    public Optional<Professor> findProfessorByUsername(String username) {
        return professorRepository.findProfessorByUsername(username);
    }

    @Override
    public ProfessorDTO findProfessorDTOByID(Integer professorId) {
        Optional<Professor> professor = professorRepository.findById(professorId);
        return professor.map(professorMapper::toDTO).orElse(null);
    }

    @Override
    public ProfessorDTO findProfessorDTOByUsername(String username) {
        Optional<Professor> professor = professorRepository.findProfessorByUsername(username);
        return professor.map(professorMapper::toDTO).orElse(null);
    }

    @Override
    public List<ProfessorDTO> findAllProfessors() {
        return professorRepository.findAll().stream()
                .map(professorMapper::toDTO)
                .toList();
    }

    @Override
    public ProfessorDTO updateProfessor(Professor professor) {
        return professorMapper.toDTO(professorRepository.save(professor));
    }

    @Override
    public void deleteProfessor(Professor professor) {
        for(Course course : professor.getCourseList()){
            course.setProfessor(null);
            courseRepository.save(course);
        }
        professorRepository.delete(professor);
    }
}
