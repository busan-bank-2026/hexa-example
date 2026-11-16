package hexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LayeredLoanTest {

    private static final String MENU = "1 대출  2 반납  ";

    @BeforeEach
    @AfterEach
    void clean() throws Exception {
        Files.deleteIfExists(Path.of("data/books.txt"));
        Files.deleteIfExists(Path.of("data"));
    }

    private String run(String input) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            Main.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    private int count(String text, String word) {
        return text.split(word, -1).length - 1;
    }

    @Test
    @DisplayName("S1 시작하면 메뉴를 보여준다. 메뉴에 없는 번호를 고르면 메뉴를 다시 보여준다")
    void s1() {
        String out = run("9\n0\n");
        assertEquals(2, count(out, MENU));
    }

    @Test
    @DisplayName("S2 대출을 고르고 회원 번호와 책 번호를 넣으면 책을 대출한다")
    void s2() {
        String out = run("1\nM1\nB1\n0\n");
        assertTrue(out.contains("회원 번호:"));
        assertTrue(out.contains("책 번호:"));
        assertTrue(out.contains("객체지향의 사실과 오해 대출했습니다"));
    }

    @Test
    @DisplayName("S3 대출 중인 책을 다시 대출하면 거절한다")
    void s3() {
        String out = run("1\nM1\nB1\n1\nM2\nB1\n0\n");
        assertTrue(out.contains("이미 대출 중인 책입니다"));
        assertEquals(1, count(out, "객체지향의 사실과 오해 대출했습니다"));
        assertTrue(out.contains("프로그램을 끝냅니다"));
    }

    @Test
    @DisplayName("S4 반납을 고르고 책 번호를 넣으면 책을 반납한다. 반납한 책은 다시 대출할 수 있다")
    void s4() {
        String out = run("1\nM1\nB1\n2\nB1\n1\nM2\nB1\n0\n");
        assertTrue(out.contains("객체지향의 사실과 오해 반납했습니다"));
        assertEquals(2, count(out, "객체지향의 사실과 오해 대출했습니다"));
    }

    @Test
    @DisplayName("S5 종료를 고르면 프로그램을 끝낸다")
    void s5() {
        String out = run("0\n");
        assertTrue(out.contains("프로그램을 끝냅니다"));
    }
}
