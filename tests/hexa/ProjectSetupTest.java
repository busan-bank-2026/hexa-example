package hexa;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProjectSetupTest {

    @Test
    @DisplayName("S1 프로그램을 실행하면 콘솔에 시작 글을 보여준다")
    void s1() {
        InputStream originalIn = System.in;
        PrintStream original = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream("0\n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            Main.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(original);
        }
        assertTrue(out.toString(StandardCharsets.UTF_8).contains("도서 대출 프로그램을 시작합니다"));
    }

    @Test
    @DisplayName("S2 이클립스에서 hexa 폴더를 프로젝트로 열고 Main을 실행할 수 있다")
    void s2() throws Exception {
        String project = Files.readString(Path.of("hexa/.project"));
        assertTrue(project.contains("<name>hexa</name>"));
        assertTrue(project.contains("org.eclipse.jdt.core.javanature"));

        String classpath = Files.readString(Path.of("hexa/.classpath"));
        assertTrue(classpath.contains("kind=\"src\" path=\"src\""));
        assertTrue(classpath.contains("JavaSE-21"));
        assertTrue(classpath.contains("kind=\"output\" path=\"bin\""));

        String jdt = Files.readString(Path.of("hexa/.settings/org.eclipse.jdt.core.prefs"));
        assertTrue(jdt.contains("org.eclipse.jdt.core.compiler.source=21"));

        String resources = Files.readString(Path.of("hexa/.settings/org.eclipse.core.resources.prefs"));
        assertTrue(resources.contains("encoding/<project>=UTF-8"));

        assertTrue(Files.exists(Path.of("hexa/src/hexa/Main.java")));
    }
}
