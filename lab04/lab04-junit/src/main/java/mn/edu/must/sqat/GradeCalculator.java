package mn.edu.must.sqat;

// Оюутны нийлбэр оноог үсгэн дүн болгож хувиргах, бүрэлдэхүүн оноог нэгтгэх логик.
// Үнэлгээний бүтэц: ирц 10, лаб+бие даалт 40, сорил1 10, сорил2 10, шалгалт 30 = нийт 100.
public class GradeCalculator {

    private static final double MAX_ATTENDANCE = 10.0;
    private static final double MAX_LAB        = 40.0;
    private static final double MAX_QUIZ       = 10.0;
    private static final double MAX_EXAM       = 30.0;

    // 90+ -> A, 80-89.99 -> B, 70-79.99 -> C, 60-69.99 -> D, <60 -> F
    // 0-100 хязгаараас гарвал IllegalArgumentException шиднэ.
    public String letterGrade(double score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("score must be in [0,100], got " + score);
        }
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    // Бүрэлдэхүүн оноонуудыг нэгтгэнэ. Аль нэг нь сөрөг эсвэл өөрийн дээд хязгаараас
    // хэтэрсэн бол IllegalArgumentException шиднэ.
    public double totalScore(double att, double lab, double quiz1, double quiz2, double exam) {
        checkRange("attendance", att,   MAX_ATTENDANCE);
        checkRange("lab",        lab,   MAX_LAB);
        checkRange("quiz1",      quiz1, MAX_QUIZ);
        checkRange("quiz2",      quiz2, MAX_QUIZ);
        checkRange("exam",       exam,  MAX_EXAM);
        return att + lab + quiz1 + quiz2 + exam;
    }

    private static void checkRange(String name, double value, double max) {
        if (value < 0 || value > max) {
            throw new IllegalArgumentException(name + " must be in [0," + max + "], got " + value);
        }
    }
}
