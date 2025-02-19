package SINU.sinu.entities.Professor;

import SINU.sinu.entities.MyUser;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Professor extends MyUser {
    @OneToMany(mappedBy ="professor", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private List<Course> courseList;
    public void addCourse(Course c){
        courseList.add(c);
    }
}
