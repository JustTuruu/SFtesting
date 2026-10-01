# Лаборатори №4 — Нэгжийн тест JUnit 5

**Хичээл:** F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест (2026)
**Оюутан:** Turbold 
**Оюутаны код:** B232270090
**Хэрэгсэл:** JUnit 5 (Jupiter, EPL 2.0), Apache Maven (Apache 2.0), OpenJDK 17 (GPLv2+CE)
**Тестлэгдэх код:** `lab04-junit/src/main/java/mn/edu/must/sqat/GradeCalculator.java`

---

## Туршилтын орчин

| Хэрэгсэл       | Хувилбар                                            | Гаралт файл                                        |
| -------------- | --------------------------------------------------- | -------------------------------------------------- |
| OpenJDK        | `17.0.18 2026-01-20 (Homebrew)`                     | [`results/java-version.txt`](results/java-version.txt) |
| Apache Maven   | `3.9.9`                                             | [`results/mvn-version.txt`](results/mvn-version.txt)   |
| JUnit Jupiter  | `5.10.2`                                            | `lab04-junit/pom.xml`-д тодорхойлсон                  |
| surefire       | `3.2.5` (JUnit 5-ыг таниулах хамгийн доод хувилбар) | `pom.xml → <pluginManagement>`                     |
| ҮС             | macOS (darwin/aarch64)                              | -                                                  |

Файлын бүтэц:

```
lab04/
├── .gitignore                       # target/, .idea/, .DS_Store
├── README.md
├── lab04-junit/
│   ├── pom.xml                      # junit-jupiter 5.10.2, surefire 3.2.5, release=17
│   └── src/
│       ├── main/java/mn/edu/must/sqat/GradeCalculator.java
│       └── test/java/mn/edu/must/sqat/GradeCalculatorTest.java
└── results/
    ├── java-version.txt
    ├── mvn-version.txt
    ├── mvn-test.txt                 # BUILD SUCCESS — 28 тест ногоон
    └── mvn-test-mutant.txt          # BUILD FAILURE — 2 тест унасан
```

---

## Алхам 1 — Орчин бэлдэх

Ажиллаж буй машин дээр Java 17+ ба Maven 3.9+ аль хэдийн байсан. Homebrew-аар суурилуулсан:

```bash
brew install openjdk@17 maven
java -version    # openjdk 17.0.18
mvn -version     # Apache Maven 3.9.9
```

---

## Алхам 2 — Maven төсөл үүсгэх

```bash
mvn archetype:generate -DgroupId=mn.edu.must.sqat \
  -DartifactId=lab04-junit -DarchetypeArtifactId=maven-archetype-quickstart \
  -DarchetypeVersion=1.4 -DinteractiveMode=false
```

**pom.xml дээр хийсэн өөрчлөлт (зааврын дагуу):**

1. `<properties>` дотор `maven.compiler.source/target 1.7`-г устгаад `<maven.compiler.release>17</maven.compiler.release>` тавьсан — үгүй бол JDK 20+ дээр `assertThrows`-ын lambda илэрхийлэл `Source option 7 is no longer supported` алдаа өгөх.
2. `<dependencies>`-с хуучин `junit 4.11`-ыг устгаад `org.junit.jupiter:junit-jupiter:5.10.2` (test scope) нэмсэн.
3. `<pluginManagement>` доторх `maven-surefire-plugin`-ыг `2.22.1` → `3.2.5` болгож солисон — surefire нь JUnit Platform-ыг зөвхөн 3.x-ээс автоматаар танина.
4. Archetype-ийн үүсгэсэн `App.java` ба JUnit 4-т суурилсан `AppTest.java` файлыг устгасан.

---

## Алхам 3 — Тестлэгдэх класс: `GradeCalculator`

`src/main/java/mn/edu/must/sqat/GradeCalculator.java`:

| Метод                                                         | Логик                                                                                  |
| ------------------------------------------------------------- | -------------------------------------------------------------------------------------- |
| `letterGrade(double score)` → `String`                        | `≥90→A, ≥80→B, ≥70→C, ≥60→D, <60→F`. `score` нь `[0,100]`-с гарвал `IllegalArgumentException`. |
| `totalScore(att, lab, quiz1, quiz2, exam)` → `double`         | Дээд хязгаар: 10/40/10/10/30. Аль нэг сөрөг эсвэл хэтэрсэн бол `IllegalArgumentException`.       |

Нэмэлт: `checkRange(name, value, max)` гэсэн жижиг helper — аль талбар зөрчигдсөн байгааг exception мессежээр илэрхийлдэг.

---

## Алхам 4 — Нэгжийн тестүүд (`GradeCalculatorTest`)

**Бүх тест метод — 15** (13 `@Test` + 2 `@ParameterizedTest`).
**Surefire-ийн тоолсон тестийн тохиолдол — 28** (parameterized мөр бүрийг тусад нь тоолдог).

| Бүлэг              | Метод                              | Юуг шалгах                                    |
| ------------------ | ---------------------------------- | --------------------------------------------- |
| letterGrade ердийн | `ninetyFiveIsA`, `thirtyIsF`       | Тохирлын жишээ (equivalence class)            |
| letterGrade хязгаар | `ninetyIsExactlyA`                 | 90 нь A-ийн доод хязгаар (`>=` vs `>`)        |
|                    | `justBelowNinetyIsB`               | 89.99 → B (нэг эпсилон доогуур)               |
|                    | `sixtyBoundary`                    | 60→D, 59.99→F — `assertAll`                   |
|                    | `extremeBoundaries`                | 0→F, 100→A                                    |
| letterGrade буруу  | `negativeScoreThrows`              | `-1` → `assertThrows(IllegalArgumentException)`|
|                    | `aboveHundredThrows`               | `101` → мөн адил                              |
| totalScore зөв     | `totalScoreMax`                    | 10/40/10/10/30 → 100                          |
|                    | `totalScoreTypical`                | 8/30/7/6/20 → 71                              |
| totalScore буруу   | `totalScoreNegativeAttendance`     | att = -5 → exception                          |
|                    | `totalScoreLabOverflow`            | lab = 41 → exception (дээд 40 хэтэрсэн)       |
|                    | `totalScoreQuizOverflow`           | quiz1 = 11 → exception                        |
| Parameterized      | `letterGradeBoundaries` (11 мөр)    | 100, 95, 90, 89.99, 80, 79.99, 70, 69.99, 60, 59.99, 0 |
|                    | `totalScoreParameterized` (4 мөр)   | Ердийн 4 нийлбэрийн хослол                    |

**Тестийн бүтэц:** тест бүр AAA (Arrange–Act–Assert)-тэй, `@DisplayName` нь Монгол хэлээр товч тайлбар өгнө.

---

## Алхам 6 — Тестүүд ажиллуулах (PASS хувилбар)

```bash
mkdir -p results && mvn -B --no-transfer-progress clean test 2>&1 | tee results/mvn-test.txt
```

**Гаралт:** [`results/mvn-test.txt`](results/mvn-test.txt) — exit code = **0**

```
[INFO] Running mn.edu.must.sqat.GradeCalculatorTest
[INFO] Tests run: 28, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.056 s
[INFO] Results:
[INFO] Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Tests run: 28** — README-д бичсэн энэ тоо log файл дах эцсийн мөртэй яг ижил байна.

---

## Мутаци — санаатай унагах туршилт

`GradeCalculator.letterGrade` доторх `score >= 90` нөхцөлийг зориуд `score > 90` болгож ажиллуулсан:

```bash
mvn -B --no-transfer-progress clean test 2>&1 | tee results/mvn-test-mutant.txt
```

**Гаралт:** [`results/mvn-test-mutant.txt`](results/mvn-test-mutant.txt) — exit code = **1** · **BUILD FAILURE**

Унасан тест — **2** (14 методын дундаас 90 оноог шалгаж байсан хоёр):

```
[ERROR] GradeCalculatorTest.ninetyIsExactlyA:42 expected: <A> but was: <B>
[ERROR] GradeCalculatorTest.letterGradeBoundaries:148 expected: <A> but was: <B>
[ERROR] Tests run: 28, Failures: 2, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

- `ninetyIsExactlyA` — 90-ийн хязгаарыг зорьж тавьсан ЕРДИЙН `@Test`
- `letterGradeBoundaries[3]` — parameterized CSV-ийн 3-р мөр (`"90,A"`)

Мутацийг буцааж `>= 90` болгосны дараа `mvn test` дахин **BUILD SUCCESS** гэсэн үр дүнг өгсөн ([`results/mvn-test.txt`](results/mvn-test.txt) ногоон хэвээр).

**Утга нь:** хэрэв би зөвхөн `95→A`, `89.99→B` гэсэн тестийг бичсэн бол `>=` vs `>` мутаци **илрэхгүй** өнгөрөх байсан. 90 гэсэн ЯГ хязгаарын утгыг шалгасан учраас л мутаци баригдсан. Энэ бол лекц 2-т үзсэн "boundary value testing"-ийн ач холбогдлын шууд нотолгоо: pass болсон тест бүр сайн тест биш, харин ямар мутацийг барих чадвартай нь чанарыг шийднэ.

---

## Дүгнэлт (5-8 өгүүлбэр)

Энэ лабораториор JUnit 5-ын `@Test`, `@DisplayName`, `assertEquals`, `assertThrows`, `assertAll`, `@ParameterizedTest` + `@CsvSource` гэсэн үндсэн боломжуудыг бүгдийг оролдож үзсэн бөгөөд 15 тест метод (нийт 28 тест тохиолдол) бичив. Хамгийн сонирхолтой олдвор бол мутацийн туршилт: `>= 90`-ыг `> 90` болгож солиход зөвхөн 90-ийн ЯГ хязгаарыг шалгаж байсан 2 тест унасан — үлдсэн 26 тест PASS хэвээр үлдсэн нь "green build" гэдэг нь код зөв гэсэн үг биш болохыг харууллаа. Хамгийн үнэтэй тест бол `sixtyBoundary` (60 → D, 59.99 → F) шиг эпсилон-ойрын утгыг тестлэсэн тестүүд байсан — эдгээр нь ирээдүйд шинжлэх ухааны notation (float comparison) алдаа гарвал шууд барих чадвартай. Parameterized тест нь мөр бүрд тусдаа `@Test` бичихэд шаардагдах ~10 мөр код 2 мөр болгож хумисан ч, унасан үедээ (`letterGradeBoundaries:148`) яг аль мөр (index=3, arguments=`[90, A]`) унасныг тодорхой хэлж өгдөг тул уншигдах чанараа алддаггүй. Хамгийн хэцүү нь pom.xml дээрх surefire-ийн 2.22.1 → 3.2.5 болон `maven.compiler.release=17` өөрчлөлтийг мэдэлгүй үлдвэл `NoTestsRemainingException` эсвэл `Source option 7 is no longer supported` гэсэн эх нь тодорхойгүй алдаа гаргадаг явдал байлаа. Дараагийн лабораторид JaCoCo coverage-оор эдгээр 28 тест кодын хэдэн хувийг хамарч байгааг тоон утгаар харах бол логик алхам болно.

---

## Тэнцэх шаардлагын checklist

- [x] `target/` `.gitignore`-д (`.idea/`, `.DS_Store`, `*.iml` ч бас)
- [x] `results/mvn-test.txt` ба `results/mvn-test-mutant.txt` репод байгаа
- [x] README-д нэр, код, `java -version`, `mvn -version` бүгд бий
- [x] `GradeCalculator`: `letterGrade` хязгаар зөв, `totalScore` сөрөг/хэтэрсэн утгад exception; `mvn-test.txt`-д **BUILD SUCCESS**
- [x] 8+ тест метод (энд 13 non-param + 2 param = 15) — AAA, `@DisplayName`, boundary values (90, 89.99, 60, 59.99, 0, 100), `assertThrows` `letterGrade` ба `totalScore` хоёуланд
- [x] 2+ `@ParameterizedTest` (`letterGradeBoundaries`, `totalScoreParameterized`)
- [x] README-д бичсэн тестийн тоо `28` = `results/mvn-test.txt`-ийн эцсийн `Tests run: 28`
- [x] Мутацийн нотолгоо: `results/mvn-test-mutant.txt`-д `Failures: 2`, **BUILD FAILURE** · буцааж засаад `mvn-test.txt` ногоон хэвээр
- [x] Дүгнэлт 5-8 өгүүлбэр (сонирхолтой тест/мутацийн үр дүнгийн талаар)
