package SINU.sinu.service;

import SINU.sinu.dto.ExamDTO;
import SINU.sinu.entities.Grade;
import SINU.sinu.entities.Professor.Professor;
import SINU.sinu.entities.Student.Student;
import SINU.sinu.mapper.ExamMapper;
import SINU.sinu.repository.ExamenRepository;
import SINU.sinu.entities.Professor.Exam;
import SINU.sinu.repository.GradeRepository;
import SINU.sinu.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamenRepository examenRepository;
    private final ExamMapper examMapper;
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;

    @Override
    public ExamDTO createExam(Exam exam) {
        return examMapper.toDTO(examenRepository.save(exam));
    }

    @Override
    public ExamDTO findExamDTOByID(Integer ID) {
        Optional<Exam> optionalExam=examenRepository.findById(ID);
        return optionalExam.map(examMapper::toDTO).orElse(null);
    }

    @Override
    public ExamDTO findExamDTOByName(String name) {
        Optional<Exam> optionalExam = examenRepository.findByName(name);
        return optionalExam.map(examMapper::toDTO).orElse(null);
    }

    @Override
    public Optional<Exam> findExamByID(Integer ID) {
        return examenRepository.findById(ID);
    }

    @Override
    public Optional<Exam> findExamByName(String name) {
        return examenRepository.findByName(name);
    }

    @Override
    public List<ExamDTO> findAllExams() {
        return examenRepository.findAll().stream().map(examMapper::toDTO).toList();
    }

    @Override
    public ExamDTO updateExam(Exam exam) {
        return examMapper.toDTO(examenRepository.save(exam));
    }

    @Override
    public void deleteExam(Exam exam) {
        examenRepository.delete(exam);
    }

}
