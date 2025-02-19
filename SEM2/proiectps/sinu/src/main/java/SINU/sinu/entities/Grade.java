package SINU.sinu.entities;

import SINU.sinu.entities.Professor.Exam;
import SINU.sinu.entities.Student.Student;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
public class Grade {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer ID;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Exam exam;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Student student;
    private Double grade;
}
