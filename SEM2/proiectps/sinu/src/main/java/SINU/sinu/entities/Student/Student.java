package SINU.sinu.entities.Student;

import SINU.sinu.entities.Grade;
import SINU.sinu.entities.MyUser;
import SINU.sinu.entities.Professor.Course;
import SINU.sinu.entities.Professor.Exam;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@SuperBuilder
public class Student extends MyUser {
    @Enumerated(EnumType.STRING)
    private StudentGroup studentGroup;
    @Enumerated(EnumType.STRING)
    private StudyYear studyYear;
        @ManyToMany(mappedBy = "students", cascade = CascadeType.REFRESH)
    private List<Course> courses;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Grade> grades;
    public double calculateAverageGrade() {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        double totalGrade = 0.0;
        for (Grade grade : grades) {
            totalGrade += grade.getGrade();
        }

        return totalGrade / grades.size();
    }
    public boolean hasPassed() {
        double passingGrade = 5.0;
        return calculateAverageGrade() >= passingGrade;
    }
}
