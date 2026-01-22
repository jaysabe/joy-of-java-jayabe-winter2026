package edu.pdx.cs.joy.jayabe;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Unit tests for the Student class.  In addition to the JUnit annotations,
 * they also make use of the <a href="http://hamcrest.org/JavaHamcrest/">hamcrest</a>
 * matchers for more readable assertion statements.
 */
public class StudentTest
{

  @Test
  void studentNamedPatIsNamedPat() {
    String name = "Pat";
    var pat = new Student(name, new ArrayList<>(), 0.0, "Doesn't matter");
    assertThat(pat.getName(), equalTo(name));
  }

  private static Student createStudentNamed (String name) {
    return new Student(name, new ArrayList<>(), 0.0, "Doesn't matter");
  }

  void says() {
    var student = new createStudentNamed("Jay");
    assertThat(student.says(), equalTo("This class is too much work"));
  }

  @Test
  void jayStudent() {
    classes.add("Algorithms");
    classes.add("Operating Systems");
    classes.add("Algorithms");
    Student jay = new Student("Jay", classes, 3.65, "male");
  }

  @Test
  void toStringContainsStudentNamed() {
    String name = "Jay";
    Student student = createStudentNamed(name);
    assertThat(student.toString(), containsString(name));
  }

  @Test
  void toStringContainsGPA() {
    double gpa = 3.65;
    Student student = createStudentNamed("Jay");
    assertThat(student.toString(), containsString(String.valueOf(gpa)));
  }
}
