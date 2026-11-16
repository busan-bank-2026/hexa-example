package hexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import javax.tools.ToolProvider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DependencyCheckTest {

    private static final Path HEXA = Path.of("hexa").toAbsolutePath();
    private static final Path JAR = HEXA.resolve("lib/junit-platform-console-standalone.jar");
    private static final Path STUDENT_TEST = HEXA.resolve("test/hexa/DependencyRuleTest.java");

    private Path work;

    record Result(int exit, String output) {
    }

    @BeforeEach
    void makeWork() throws Exception {
        work = Files.createTempDirectory("dependency-check");
    }

    @AfterEach
    void removeWork() throws Exception {
        try (Stream<Path> paths = Files.walk(work)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private Path compileStudentTest() throws Exception {
        assertTrue(Files.exists(JAR), "hexa/lib에 JUnit jar가 없다");
        assertTrue(Files.exists(STUDENT_TEST), "hexa/test/hexa/DependencyRuleTest.java가 없다");
        Path bin = work.resolve("bin");
        Files.createDirectories(bin);
        int code = ToolProvider.getSystemJavaCompiler().run(null, null, null,
                "-encoding", "UTF-8", "-d", bin.toString(), "-cp", JAR.toString(), STUDENT_TEST.toString());
        assertEquals(0, code, "학생 테스트가 hexa/lib의 jar만으로 컴파일되지 않는다");
        return bin;
    }

    private Result runStudentTest(Path cwd) throws Exception {
        Path bin = compileStudentTest();
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process process = new ProcessBuilder(java, "-jar", JAR.toString(), "execute",
                "-cp", bin.toString(), "--select-class", "hexa.DependencyRuleTest",
                "--disable-banner", "--disable-ansi-colors", "--details=flat")
                .directory(cwd.toFile()).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return new Result(process.waitFor(), output);
    }

    private Path copyOfSrc() throws Exception {
        Path root = Files.createTempDirectory(work, "copy");
        Path from = HEXA.resolve("src");
        try (Stream<Path> paths = Files.walk(from)) {
            for (Path path : paths.toList()) {
                Path to = root.resolve("src").resolve(from.relativize(path).toString());
                if (Files.isDirectory(path)) {
                    Files.createDirectories(to);
                } else {
                    Files.copy(path, to);
                }
            }
        }
        return root;
    }

    private Result runWithImport(String file, String importLine) throws Exception {
        Path root = copyOfSrc();
        Path source = root.resolve("src/hexa").resolve(file);
        String text = Files.readString(source);
        String packageLine = text.lines().filter(line -> line.startsWith("package ")).findFirst().orElseThrow();
        Files.writeString(source, text.replace(packageLine, packageLine + "\n\n" + importLine));
        return runStudentTest(root);
    }

    private void assertFails(String file, String importLine) throws Exception {
        Result result = runWithImport(file, importLine);
        assertNotEquals(0, result.exit(), file + "에 " + importLine + "를 넣어도 학생 테스트가 통과한다\n" + result.output());
        assertTrue(result.output().contains(importLine), "빨간불에 어긴 import 줄이 보이지 않는다\n" + result.output());
    }

    private void assertPassesNow() throws Exception {
        Result result = runStudentTest(HEXA);
        assertEquals(0, result.exit(), result.output());
        assertTrue(result.output().contains("tests successful"), result.output());
    }

    @Test
    @DisplayName("S1 hexa 프로젝트에 JUnit을 붙인다. lib 폴더의 JUnit jar와 test 소스 폴더를 더해 이클립스에서 Run As, JUnit Test로 학생 테스트를 돌린다")
    void s1() throws Exception {
        assertTrue(Files.exists(JAR), "hexa/lib에 JUnit jar가 없다");
        assertEquals(Files.size(Path.of("tests/lib/junit-platform-console-standalone.jar")), Files.size(JAR));

        String classpath = Files.readString(HEXA.resolve(".classpath"));
        assertTrue(classpath.contains("kind=\"src\" path=\"src\""));
        assertTrue(classpath.contains("kind=\"src\" path=\"test\""));
        assertTrue(classpath.contains("kind=\"lib\" path=\"lib/junit-platform-console-standalone.jar\""));

        String test = Files.readString(STUDENT_TEST);
        assertTrue(test.startsWith("package hexa;"));
        assertTrue(test.contains("@Test"));
        assertTrue(test.contains("assertFalse"));

        assertPassesNow();
        try (Stream<Path> paths = Files.walk(HEXA.resolve("src"))) {
            assertTrue(paths.noneMatch(p -> p.toString().endsWith("Test.java")), "학생 테스트가 src에 섞였다");
        }
    }

    @Test
    @DisplayName("S2 의존 규칙 테스트가 domain 소스를 읽어 application이나 adapter를 import하면 실패한다")
    void s2() throws Exception {
        assertPassesNow();
        assertFails("domain/Book.java", "import hexa.adapter.out.MemoryBookAdapter;");
        assertFails("domain/Book.java", "import hexa.application.port.out.BookPort;");
    }

    @Test
    @DisplayName("S3 의존 규칙 테스트가 application 소스를 읽어 adapter를 import하면 실패한다")
    void s3() throws Exception {
        assertPassesNow();
        assertFails("application/LoanService.java", "import hexa.adapter.out.MemoryBookAdapter;");
        assertFails("application/port/in/LoanUseCase.java", "import hexa.adapter.in.ConsoleMenu;");
    }

    @Test
    @DisplayName("S4 의존 규칙 테스트가 어댑터끼리 import하면 실패한다. adapter.in은 adapter.out을, adapter.out은 adapter.in을 모른다")
    void s4() throws Exception {
        assertPassesNow();
        assertFails("adapter/in/ConsoleMenu.java", "import hexa.adapter.out.MemoryBookAdapter;");
        assertFails("adapter/out/FileBookAdapter.java", "import hexa.adapter.in.ConsoleMenu;");
    }
}
