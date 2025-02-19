package SINU.sinu.entities.Professor;


import SINU.sinu.entities.Grade;
import SINU.sinu.entities.Student.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
public class Exam {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer ID;
    @NotBlank
    @Column(unique=true)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Course course;
    @OneToMany(mappedBy = "exam", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Grade> grades;
}
