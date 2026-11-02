# Лаборатори №8 — Mock объектууд: Mockito-гоор хамаарлыг орлуулж тестлэх

**Хичээл:** F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест (2026)
**Оюутан:** О.Төрболд
**Оюутны код:** B232270090

```
$ java -version
openjdk version "17.0.18" 2026-01-20
OpenJDK Runtime Environment Homebrew (build 17.0.18+0)
OpenJDK 64-Bit Server VM Homebrew (build 17.0.18+0, mixed mode, sharing)

$ mvn -version
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: /opt/homebrew/Cellar/maven/3.9.9/libexec
Java version: 17.0.18, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk@17/17.0.18/libexec/openjdk.jdk/Contents/Home
Default locale: en_MN, platform encoding: UTF-8
OS name: "mac os x", version: "26.6.2", arch: "aarch64", family: "mac"
```

## Бүтэц

| Файл                                                                                                                   | Агуулга                                                                              |
| ---------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| [`pom.xml`](pom.xml)                                                                                                   | JUnit 5.10.2 + Mockito 5.11.0, surefire 3.2.5 (starter)                              |
| [`src/main/.../CourseRegistrationService.java`](src/main/java/mn/edu/must/sqat/CourseRegistrationService.java)         | Тестлэгдэх класс — эх хувилбараараа                                                  |
| [`src/test/.../CourseRegistrationServiceTest.java`](src/test/java/mn/edu/must/sqat/CourseRegistrationServiceTest.java) | A1–A8 тестүүд                                                                        |
| [`results/mvn-test.txt`](results/mvn-test.txt)                                                                         | Ногоон гаралт — **Tests run: 10, Failures: 0, Errors: 0, Skipped: 0**, BUILD SUCCESS |
| [`results/mvn-test-mutant-1..3.txt`](results/)                                                                         | Хэсэг B-ийн гурван мутантын гаралт — бүгд BUILD FAILURE                              |

Ажиллуулах: `mvn test`

## Хэсэг A — Найман тест (нийт 10 тест)

| №   | Тестийн нэр                                                                         | Гол хэрэгсэл                                      |
| --- | ----------------------------------------------------------------------------------- | ------------------------------------------------- |
| A1  | `register_activeStudentWithPrerequisites_returnsOkSavesAndSendsEmail`               | `when` + `verify`                                 |
| A2  | `register_unknownStudent_returnsErrorNoStudentAndDoesNotSaveOrEmail`                | `verify(..., never())`                            |
| A3  | `register_inactiveStudent_returnsErrorInactiveAndNeverTouchesEmail`                 | `verifyNoInteractions`                            |
| A4  | `register_missingPrerequisite_returnsErrorPrerequisitesAndDoesNotSave`              | `when` + `never`                                  |
| A5  | `register_emailSenderThrows_stillReturnsOkAndRegistrationIsSaved`                   | `doThrow(...).when(...)`                          |
| A6  | `register_nullStudentIdOrCourseId_throwsIllegalArgumentAndNeverTouchesDependencies` | `assertThrows` + `verifyNoInteractions`           |
| A7  | `register_success_emailBodyContainsCourseId`                                        | `ArgumentCaptor`                                  |
| A8  | `register_errorClass_returnsMatchingErrorAndDoesNotSaveOrEmail` (3 мөр)             | `@ParameterizedTest` + `@CsvSource` + `lenient()` |

7 энгийн тест + A8-ийн 3 мөр = **10 тест** ([`results/mvn-test.txt`](results/mvn-test.txt)-ийн сүүлийн `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0`).

A8 нь `ERROR_NO_STUDENT`, `ERROR_INACTIVE_STUDENT`, `ERROR_NO_COURSE` гэсэн гурван алдааны ангийг хамарна. `ERROR_NO_COURSE` өөр ямар ч тестэд ороогүй тул A8-д сонгосон. Matcher-ийн дүрмийг баримталсан: нэг дуудалтад matcher хэрэглэсэн бол бүх аргументад хэрэглэсэн (`verify(emailSender).send(eq(EMAIL), anyString(), anyString())`).

### A8-д `lenient()` яагаад хэрэгтэй вэ

A8-ийн мөр бүр `register()`-ийн өөр салбарт эрт буцдаг тул нэг мөрд хэрэгтэй stub нөгөө мөрд ашиглагдахгүй үлддэг: жишээ нь оюутан байхгүй мөрд `courseExists`, `prerequisites` хоёр огт дуудагдахгүй. `MockitoExtension`-ий strict stubs горим ийм ашиглагдаагүй stub-ыг `UnnecessaryStubbingException` гэж алдаа болгодог — `lenient()`-гүйгээр ажиллуулахад A8-ийн 3 мөр бүгд `Errors: 3` болж унасныг шалгаж үзсэн.

## Хэсэг B — Мини мутаци

| Мутант | Эвдрэл                                                    | Унасан тест                                                   | Төрөл / шалтгаан                                                                                                | Гаралт                                                                                             |
| ------ | --------------------------------------------------------- | ------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| 1      | `if (!student.active()) ...` мөрийг комментолсон          | **A3**, **A8 [2]**                                            | 2 Failures — `expected: <ERROR_INACTIVE_STUDENT> but was: <ERROR_NO_COURSE>` (A3), `... but was: <OK>` (A8 [2]) | [`mvn-test-mutant-1.txt`](results/mvn-test-mutant-1.txt) — `Failures: 2, Errors: 0`, BUILD FAILURE |
| 2      | `repository.saveRegistration(...)` дуудалтыг комментолсон | **A1**, **A5**                                                | 2 Failures — `Wanted but not invoked: repository.saveRegistration("S001", "F.CSA313")`                          | [`mvn-test-mutant-2.txt`](results/mvn-test-mutant-2.txt) — `Failures: 2, Errors: 0`, BUILD FAILURE |
| 3      | эхний `if`-ээс `\|\| courseId == null`-ийг устгасан       | Засахаас өмнө: **нэг ч тест унаагүй**. Засварын дараа: **A6** | 1 Failure — `Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.`                 | [`mvn-test-mutant-3.txt`](results/mvn-test-mutant-3.txt) — `Failures: 1, Errors: 0`, BUILD FAILURE |

**Мутант 3 ба A6-ийн засвар.** Анхны A6 зөвхөн `register(null, COURSE_ID)`-г шалгадаг байсан тул мутант 3 ажиллахад 10 тест бүгд ногоон гарч (BUILD SUCCESS), мутант амьд үлдсэн — тестийн цоорхой. A6-д `assertThrows(IllegalArgumentException.class, () -> service.register(STUDENT_ID, null))`-г нэмж, хоёр аргумент тус бүрийн null-ийг шалгадаг болгосон. Мутант дээр `courseId == null` үед код mock-руу `findStudent("S001")` дуудаж, тохируулаагүй mock `null` буцааж `"ERROR_NO_STUDENT"` өгдөг тул exception шидэгдэхгүй — засварласан A6 үүнийг барьсан. `results/mvn-test-mutant-3.txt` нь засварын дараах гаралт.

Мутаци бүрийн дараа `src/main`-ийг `git checkout`-оор сэргээж, эцэст нь `mvn test`-ийг дахин ажиллуулж [`results/mvn-test.txt`](results/mvn-test.txt) ногоон гэдгийг баталсан. Мутант 1, 2-т `UnnecessaryStubbingException` гараагүй: A3/A8 [2]-т stub-ууд (lenient эсвэл бодитоор дуудагдсан) хэвээр ашиглагдсан, A1/A5-д бүх stub дуудагдсан.

**Дүгнэлт.** Гурван мутантыг бүгд барьсан ч гурав дахь нь анх амьд үлдсэн — "assertThrows ажиллаж байна" гэдэг нь "бүх null-ийг шалгаж байна" гэсэн үг биш. Мутант бүрийг өөр тест барьсан нь (A3/A8, A1/A5, A6) тест бүр өөрийн хариуцлагатай, давхцал багатайг харуулна. Харин A2, A4, A7 аль ч мутантыг бариагүй — энэ гурван мутант тэдний шалгадаг салбарт хүрээгүйтэй холбоотой бөгөөд Лекц 11-ийн автомат mutation testing-ээр илүү бүрэн шалгах боломжтой.

## Дүгнэлт

Mock нь Mockito-гоор ажиллах үед үүсгэгдэж, тест бүрд "энэ дуудалтад ийм утга буцаа" (stub) гэж тохируулагдаад, дараа нь "энэ дуудалт ийм аргументаар болсон уу" (verify) гэж харилцан үйлдлийг шалгадаг; гараар бичсэн fake (жишээ нь `HashMap` дээр суурилсан `InMemoryStudentRepository`) харин бодит, хялбаршуулсан хэрэгжүүлэлт бөгөөд төлөвийг шалгахад тохиромжтой ч хэн юуг хэдэн удаа дуудсаныг өөрөө бүртгэхгүй. Mock нь энэ лабын `EmailSender` шиг гаднах, удаан, эсвэл тохируулахад хэцүү хамаарлыг (SMTP, өгөгдлийн сан) орлуулж, `doThrow`-оор бодитоор үүсгэхэд хэцүү алдааг ч дуурайлгаж чадна. Харин `Student` record, `List` зэрэг утгын объект, өөрийн тестлэж буй класс, мөн өөрийн эзэмшдэггүй гуравдагч талын төрлийг шууд mock хийх ёсгүй — тэдгээрийг бодитоор нь ашиглах эсвэл өөрийн interface-ээр боож mock хийнэ. `verify`-г хэтрүүлбэл тест хэрэгжүүлэлтийн нарийн ширийнд (дуудалтын дараалал, тоо, аргумент бүр) хэт уягдаж, үр дүн нь өөрчлөгдөөгүй рефакторинг ч тестийг улаан болгодог "эмзэг тест" үүснэ. Иймээс энэ лабад `verify`-г зөвхөн спецификацид бичигдсэн гаднах үр дагаварт — бүртгэл хадгалагдсан эсэх, и-мэйл явсан эсэх — хэрэглэж, `courseExists`, `prerequisites` зэрэг уншилтын дуудалтыг verify хийгээгүй. Хэсэг B харуулсанчлан ногоон тест дангаараа хангалтгүй: кодыг зориуд эвдэж, тест үнэхээр барьж байгааг шалгах хэрэгтэй.
