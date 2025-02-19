package SINU.sinu.service;

import SINU.sinu.dto.ExamDTO;
import SINU.sinu.entities.Grade;
import SINU.sinu.entities.Professor.Exam;
import SINU.sinu.entities.Student.Student;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public interface ExamService {
    ExamDTO createExam(Exam exam);
    ExamDTO findExamDTOByID(Integer ID);
    ExamDTO findExamDTOByName(String name);
    Optional<Exam> findExamByID(Integer ID);
    Optional<Exam> findExamByName(String name);
    List<ExamDTO> findAllExams();
    ExamDTO updateExam(Exam exam);
    void deleteExam(Exam exam);
}
