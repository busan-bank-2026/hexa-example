package hexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoanErrorsTest {

    private static final Path SRC = Path.of("hexa/src/hexa");
    private static final Path FILE = Path.of("data/books.txt");
    private static final String END = "프로그램을 끝냅니다";

    @BeforeEach
    @AfterEach
    void clean() throws Exception {
        Files.deleteIfExists(FILE);
        Files.deleteIfExists(Path.of("data"));
    }

    private String runMain(String input) {
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

    private Throwable causeOf(ThrowingRunnable action) {
        InvocationTargetException e = assertThrows(InvocationTargetException.class, action::run);
        return e.getCause();
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private List<Path> javaFiles(Path folder) throws Exception {
        try (Stream<Path> files = Files.walk(folder)) {
            return new ArrayList<>(files.filter(p -> p.toString().endsWith(".java")).toList());
        }
    }

    @Test
    @DisplayName("S1 대출 중인 책을 대출하면 콘솔 메뉴가 이미 대출 중인 책입니다를 보여 주고 메뉴로 돌아간다. 빌린 사람은 바뀌지 않는다")
    void s1() throws Exception {
        String out = runMain("1\nM1\nB1\n1\nM2\nB1\n0\n");
        assertTrue(out.contains("이미 대출 중인 책입니다"), out);
        assertEquals(1, count(out, "대출했습니다"), out);
        assertTrue(out.contains(END), out);
        assertTrue(Files.readAllLines(FILE).stream().anyMatch(line -> line.startsWith("B1,객체지향의 사실과 오해,M1")));
    }

    @Test
    @DisplayName("S2 없는 책 번호로 대출하거나 반납하면 서비스가 책을 찾을 수 없습니다 오류를 낸다. 콘솔 메뉴는 그 메시지를 보여 주고 메뉴로 돌아간다")
    void s2() throws Exception {
        Object service = Find.newLoanService();
        Throwable borrow = causeOf(() -> Find.borrow(service, "M1", "B9"));
        assertEquals(IllegalArgumentException.class, borrow.getClass());
        assertEquals("책을 찾을 수 없습니다", borrow.getMessage());

        Method returnBook = Find.type("LoanUseCase").getMethod("returnBook", String.class);
        Throwable back = causeOf(() -> returnBook.invoke(service, "B9"));
        assertEquals(IllegalArgumentException.class, back.getClass());
        assertEquals("책을 찾을 수 없습니다", back.getMessage());

        String out = runMain("1\nM1\nB9\n2\nB9\n0\n");
        assertEquals(2, count(out, "책을 찾을 수 없습니다"), out);
        assertTrue(out.contains(END), out);
    }

    @Test
    @DisplayName("S3 회원 번호나 책 번호를 비우고 대출하면 콘솔 메뉴가 회원 번호가 비었습니다나 책 번호가 비었습니다를 보여 주고 메뉴로 돌아간다. 저장 파일에 대출이 남지 않는다")
    void s3() throws Exception {
        String out = runMain("1\n\nB1\n1\n \nB2\n1\nM1\n \n0\n");
        assertEquals(2, count(out, "회원 번호가 비었습니다"), out);
        assertEquals(1, count(out, "책 번호가 비었습니다"), out);
        assertFalse(out.contains("대출했습니다"), out);
        assertTrue(out.contains(END), out);
        assertEquals(List.of("B1,객체지향의 사실과 오해,", "B2,클린 아키텍처,", "B3,도메인 주도 설계 첫걸음,"),
                Files.readAllLines(FILE).stream().map(line -> line.endsWith(",,") ? line.substring(0, line.length() - 1) : line).toList());
    }

    @Test
    @DisplayName("S4 대출 중이 아닌 책을 반납하면 도메인 책이 거절한다. 콘솔 메뉴는 대출 중이 아닌 책입니다를 보여 주고 메뉴로 돌아간다")
    void s4() throws Exception {
        Object book = Find.type("Book").getConstructor(String.class, String.class).newInstance("B1", "책");
        Method returnBook = book.getClass().getMethod("returnBook");
        Throwable cause = causeOf(() -> returnBook.invoke(book));
        assertEquals(IllegalStateException.class, cause.getClass());
        assertEquals("대출 중이 아닌 책입니다", cause.getMessage());

        String out = runMain("2\nB2\n0\n");
        assertTrue(out.contains("대출 중이 아닌 책입니다"), out);
        assertFalse(out.contains("반납했습니다"), out);
        assertTrue(out.contains(END), out);
    }

    @Test
    @DisplayName("S5 서비스와 도메인은 실패를 예외로만 알리고 콘솔 글을 모른다. 예외를 잡아 메시지로 바꾸는 곳은 콘솔 메뉴 하나다")
    void s5() throws Exception {
        List<Path> inside = javaFiles(SRC.resolve("domain"));
        inside.addAll(javaFiles(SRC.resolve("application")));
        for (Path file : inside) {
            String source = Files.readString(file);
            assertFalse(source.contains("System.out"), file.toString());
            assertFalse(source.contains("catch"), file.toString());
        }

        List<String> users = new ArrayList<>();
        for (Path file : javaFiles(SRC)) {
            if (Files.readString(file).contains("getMessage()")) {
                users.add(file.getFileName().toString());
            }
        }
        assertEquals(List.of("ConsoleMenu.java"), users);
    }
}
