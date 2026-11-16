package hexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BorrowCommandTest {

    private static final Path SRC = Path.of("hexa/src/hexa");
    private static final Path FILE = Path.of("data/books.txt");

    @BeforeEach
    @AfterEach
    void clean() throws Exception {
        Files.deleteIfExists(FILE);
        Files.deleteIfExists(Path.of("data"));
    }

    private String runMain(String input, ByteArrayOutputStream out) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
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

    private Constructor<?> commandConstructor() throws Exception {
        return Find.type("BorrowCommand").getConstructor(String.class, String.class);
    }

    private void assertRejected(String memberId, String bookId) throws Exception {
        Constructor<?> constructor = commandConstructor();
        InvocationTargetException e = assertThrows(InvocationTargetException.class,
                () -> constructor.newInstance(memberId, bookId), "[" + memberId + "] [" + bookId + "]");
        assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }

    @Test
    @DisplayName("S1 들어오는 포트의 대출은 대출 요청 하나를 받는다. 대출 요청은 회원 번호와 책 번호를 담은 입력 모델이다. 콘솔 메뉴와 두 번째 입구는 대출 요청을 만들어 포트를 부른다")
    void s1() throws Exception {
        Class<?> command = Find.type("BorrowCommand");
        assertEquals("hexa.application.port.in", command.getPackageName());
        assertTrue(command.isRecord());
        List<String> names = new ArrayList<>();
        for (RecordComponent component : command.getRecordComponents()) {
            names.add(component.getName());
        }
        assertEquals(List.of("memberId", "bookId"), names);

        Class<?> port = Find.type("LoanUseCase");
        Method borrow = port.getMethod("borrow", command);
        assertEquals(Find.type("BookView"), borrow.getReturnType());
        assertThrows(NoSuchMethodException.class, () -> port.getMethod("borrow", String.class, String.class));

        Object service = Find.newLoanService();
        Object view = borrow.invoke(service, commandConstructor().newInstance("M1", "B3"));
        assertEquals("도메인 주도 설계 첫걸음", view.getClass().getMethod("title").invoke(view));

        for (Path file : List.of(SRC.resolve("adapter/in/ConsoleMenu.java"), SRC.resolve("LoanCheck.java"))) {
            assertTrue(Files.readString(file).contains("new BorrowCommand("), file.toString());
        }
    }

    @Test
    @DisplayName("S2 대출 요청은 만들 때 형식을 검사한다. 회원 번호나 책 번호가 비었거나 빈칸뿐이면 잘못된 값 오류로 만들어지지 않는다")
    void s2() throws Exception {
        for (String blank : List.of("", " ", "   ", "\t ")) {
            assertRejected(blank, "B1");
            assertRejected("M1", blank);
        }
        Object command = commandConstructor().newInstance("M1", "B1");
        assertEquals("M1", command.getClass().getMethod("memberId").invoke(command));
        assertEquals("B1", command.getClass().getMethod("bookId").invoke(command));
    }

    @Test
    @DisplayName("S3 콘솔 메뉴에서 회원 번호를 비우거나 빈칸만 넣고 대출하면 거절한다. 대출했습니다가 나오지 않고 저장 파일에 대출이 남지 않는다")
    void s3() throws Exception {
        String empty = runMain("1\n\nB1\n0\n", new ByteArrayOutputStream());
        assertTrue(empty.contains("회원 번호가 비었습니다"), empty);
        assertFalse(empty.contains("대출했습니다"), empty);

        String space = runMain("1\n \nB2\n0\n", new ByteArrayOutputStream());
        assertTrue(space.contains("회원 번호가 비었습니다"), space);
        assertFalse(space.contains("대출했습니다"), space);

        assertTrue(Files.readAllLines(FILE).contains("B2,클린 아키텍처,"));
        String after = runMain("3\n0\n", new ByteArrayOutputStream());
        assertTrue(after.contains("B1 객체지향의 사실과 오해 대출 가능"), after);
        assertTrue(after.contains("B2 클린 아키텍처 대출 가능"), after);
    }
}
