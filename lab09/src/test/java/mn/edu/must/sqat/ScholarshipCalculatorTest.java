package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ScholarshipCalculatorTest {
    private final ScholarshipCalculator calculator = new ScholarshipCalculator();

    @Test
    void letterGradeReturnsAForExcellentScore() {
        assertEquals("A", calculator.letterGrade(95));
    }

    @Test
    void letterGradeReturnsBForGoodScore() {
        assertEquals("B", calculator.letterGrade(85));
    }

    @Test
    void gpaCalculatesAverageFromTwoScores() {
        assertEquals(3.5, calculator.gpa(new double[] {95, 85}));
    }

    @Test
    void scholarshipReturnsFullAwardForHighGpaAndCredits() {
        assertEquals(100, calculator.scholarship(3.9, 16, false));
    }

    @Test
    void scholarshipReturnsPartialAwardForQualifyingGpa() {
        assertEquals(50, calculator.scholarship(3.0, 10, false));
    }
}
