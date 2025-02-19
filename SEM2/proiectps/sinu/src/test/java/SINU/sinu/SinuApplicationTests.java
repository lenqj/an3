package SINU.sinu;

import SINU.sinu.entities.Grade;
import SINU.sinu.entities.Student.Student;
import SINU.sinu.entities.Student.StudentGroup;
import SINU.sinu.entities.Student.StudyYear;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;

class SinuApplicationTests {

	@Test

	public void testGradeEntity() {
		Grade grade = Grade.builder()
				.grade(8.5)
				.build();

		assertEquals("Grade test", grade.getGrade(), 8.5);
	}

	@Test
	public void testStudentEntity() {
		Student student = Student.builder()
				.studentGroup(StudentGroup.GROUP1)
				.studyYear(StudyYear.YEAR1)
				.build();
		assertEquals("Student group test", StudentGroup.GROUP1, student.getStudentGroup());
		assertEquals("Student year test", StudyYear.YEAR1, student.getStudyYear());
	}

	@Test
	public void testStudentAverageGrade() {
		List<Grade> grades = new ArrayList<>();
		grades.add(Grade.builder().grade(8.).build());
		grades.add(Grade.builder().grade(7.0).build());
		grades.add(Grade.builder().grade(9.0).build());

		Student student = Student.builder()
				.studentGroup(StudentGroup.GROUP1)
				.studyYear(StudyYear.YEAR1)
				.grades(grades)
				.build();

		assertEquals("Student average grade test", 8.0, student.calculateAverageGrade());
	}

	@Test
	public void testStudentPassStatus() {
		List<Grade> grades = new ArrayList<>();
		grades.add(Grade.builder().grade(8.5).build());
		grades.add(Grade.builder().grade(7.5).build());
		grades.add(Grade.builder().grade(9.0).build());

		Student student = Student.builder()
				.studentGroup(StudentGroup.GROUP1)
				.studyYear(StudyYear.YEAR1)
				.grades(grades)
				.build();

		assertEquals("Student pass status test", true, student.hasPassed());
	}

}

