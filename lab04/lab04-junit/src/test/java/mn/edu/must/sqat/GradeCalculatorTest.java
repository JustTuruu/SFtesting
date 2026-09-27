package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GradeCalculatorTest {

    // Тест бүр AAA бүтэц (Arrange–Act–Assert)-тэй, @DisplayName нь монгол хэлээр
    // ойлгомжтой тайлбар өгнө.

    // ---------- letterGrade — ердийн утга ----------

    @Test
    @DisplayName("95 оноо → A дүн (ердийн утга)")
    void ninetyFiveIsA() {
        GradeCalculator calc = new GradeCalculator();      // Arrange
        String grade = calc.letterGrade(95.0);             // Act
        assertEquals("A", grade);                          // Assert
    }

    @Test
    @DisplayName("30 оноо → F дүн (тэнцээгүй бүсэд)")
    void thirtyIsF() {
        GradeCalculator calc = new GradeCalculator();
        String grade = calc.letterGrade(30.0);
        assertEquals("F", grade);
    }

    // ---------- letterGrade — хязгаарын утгууд (boundary values) ----------

    @Test
    @DisplayName("90 оноо яг A дүн байх ёстой (хязгаарын тохиолдол)")
    void ninetyIsExactlyA() {
        GradeCalculator calc = new GradeCalculator();
        String grade = calc.letterGrade(90.0);
        assertEquals("A", grade);
    }

    @Test
    @DisplayName("89.99 оноо B дүн (A-ийн доод хязгаараас нэг эпсилон доогуур)")
    void justBelowNinetyIsB() {
        GradeCalculator calc = new GradeCalculator();
        assertEquals("B", calc.letterGrade(89.99));
    }

    @Test
    @DisplayName("60 ба 59.99 — D/F хязгаарыг хоёуланг нь шалгах")
    void sixtyBoundary() {
        GradeCalculator calc = new GradeCalculator();
        assertAll(
            () -> assertEquals("D", calc.letterGrade(60.0)),
            () -> assertEquals("F", calc.letterGrade(59.99))
        );
    }

    @Test
    @DisplayName("0 ба 100 — доод/дээд хамгийн зах хязгаар зөв ажиллах")
    void extremeBoundaries() {
        GradeCalculator calc = new GradeCalculator();
        assertAll(
            () -> assertEquals("F", calc.letterGrade(0.0)),
            () -> assertEquals("A", calc.letterGrade(100.0))
        );
    }

    // ---------- letterGrade — буруу оролт ----------

    @Test
    @DisplayName("-1 оноо IllegalArgumentException шидэх ёстой")
    void negativeScoreThrows() {
        GradeCalculator calc = new GradeCalculator();
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
    }

    @Test
    @DisplayName("101 оноо IllegalArgumentException шидэх ёстой")
    void aboveHundredThrows() {
        GradeCalculator calc = new GradeCalculator();
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    // ---------- totalScore ----------

    @Test
    @DisplayName("Дээд утгууд 10/40/10/10/30 → нийт 100")
    void totalScoreMax() {
        GradeCalculator calc = new GradeCalculator();
        double total = calc.totalScore(10, 40, 10, 10, 30);
        assertEquals(100.0, total, 1e-9);
    }

    @Test
    @DisplayName("Ердийн утгууд 8/30/7/6/20 → нийт 71")
    void totalScoreTypical() {
        GradeCalculator calc = new GradeCalculator();
        double total = calc.totalScore(8, 30, 7, 6, 20);
        assertEquals(71.0, total, 1e-9);
    }

    @Test
    @DisplayName("totalScore — сөрөг ирц (-5) IllegalArgumentException шидэх ёстой")
    void totalScoreNegativeAttendance() {
        GradeCalculator calc = new GradeCalculator();
        assertThrows(IllegalArgumentException.class,
                     () -> calc.totalScore(-5, 30, 7, 6, 20));
    }

    @Test
    @DisplayName("totalScore — лаб 41 (дээд 40 хэтэрсэн) IllegalArgumentException")
    void totalScoreLabOverflow() {
        GradeCalculator calc = new GradeCalculator();
        assertThrows(IllegalArgumentException.class,
                     () -> calc.totalScore(10, 41, 10, 10, 30));
    }

    @Test
    @DisplayName("totalScore — сорил 11 (дээд 10 хэтэрсэн) IllegalArgumentException")
    void totalScoreQuizOverflow() {
        GradeCalculator calc = new GradeCalculator();
        assertThrows(IllegalArgumentException.class,
                     () -> calc.totalScore(10, 40, 11, 10, 30));
    }

    // ---------- Parameterized (Алхам 5) ----------

    @ParameterizedTest(name = "[{index}] {0} → {1}")
    @DisplayName("letterGrade — хязгаарын утгуудыг нэг тестээр")
    @CsvSource({
        "100,A",
        "95,A",
        "90,A",
        "89.99,B",
        "80,B",
        "79.99,C",
        "70,C",
        "69.99,D",
        "60,D",
        "59.99,F",
        "0,F"
    })
    void letterGradeBoundaries(double score, String expected) {
        assertEquals(expected, new GradeCalculator().letterGrade(score));
    }

    @ParameterizedTest(name = "[{index}] att={0}, lab={1}, q1={2}, q2={3}, exam={4} → {5}")
    @DisplayName("totalScore — parameterized нийлбэр")
    @CsvSource({
        "10, 40, 10, 10, 30, 100",
        "0,  0,  0,  0,  0,  0",
        "5,  20, 5,  5,  15, 50",
        "10, 35, 8,  7,  25, 85"
    })
    void totalScoreParameterized(double att, double lab, double q1, double q2,
                                 double exam, double expected) {
        assertEquals(expected,
                     new GradeCalculator().totalScore(att, lab, q1, q2, exam),
                     1e-9);
    }
}
