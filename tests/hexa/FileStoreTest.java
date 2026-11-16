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
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileStoreTest {

    private static final Path DATA = Path.of("data");
    private static final Path FILE = DATA.resolve("books.txt");

    @BeforeEach
    @AfterEach
    void clean() throws Exception {
        Files.deleteIfExists(FILE);
        Files.deleteIfExists(DATA);
    }

    private Object newAdapter() throws Exception {
        return Find.type("FileBookAdapter").getConstructor().newInstance();
    }

    private Object newBook(String id, String title) throws Exception {
        return Find.type("Book").getConstructor(String.class, String.class).newInstance(id, title);
    }

    private void borrow(Object book, String memberId) throws Throwable {
        try {
            try {
                book.getClass().getMethod("borrow", String.class, LocalDate.class)
                        .invoke(book, memberId, LocalDate.now().plusDays(14));
            } catch (NoSuchMethodException e) {
                book.getClass().getMethod("borrow", String.class).invoke(book, memberId);
            }
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private void save(Object adapter, Object book) throws Exception {
        adapter.getClass().getMethod("save", Find.type("Book")).invoke(adapter, book);
    }

    private Object findById(Object adapter, String id) throws Exception {
        return adapter.getClass().getMethod("findById", String.class).invoke(adapter, id);
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

    @Test
    @DisplayName("S1 책을 파일에 저장하는 드리븐 어댑터를 더한다. 한 줄에 책 하나이고 책 번호, 제목, 대출한 회원 번호를 쉼표로 나눠 적는다")
    void s1() throws Throwable {
        assertTrue(Find.type("BookPort").isAssignableFrom(Find.type("FileBookAdapter")));

        Object adapter = newAdapter();
        Object book = newBook("B1", "객체지향의 사실과 오해");
        borrow(book, "M1");
        save(adapter, book);

        List<String> lines = Files.readAllLines(FILE);
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("B1,객체지향의 사실과 오해,M1")), lines.toString());
        assertTrue(lines.stream().anyMatch(line -> line.equals("B2,클린 아키텍처,") || line.equals("B2,클린 아키텍처,,")),
                lines.toString());
    }

    @Test
    @DisplayName("S2 어댑터는 파일 한 줄을 책으로 되살린다. 대출 중이던 책은 대출 중인 책으로 되살아난다")
    void s2() throws Throwable {
        Object book = newBook("B1", "객체지향의 사실과 오해");
        borrow(book, "M1");
        save(newAdapter(), book);

        Object again = newAdapter();
        Object b1 = findById(again, "B1");
        assertEquals("객체지향의 사실과 오해", b1.getClass().getMethod("getTitle").invoke(b1));
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> borrow(b1, "M2"));
        assertEquals("이미 대출 중인 책입니다", e.getMessage());

        Object b2 = findById(again, "B2");
        borrow(b2, "M2");
    }

    @Test
    @DisplayName("S3 저장 파일이 없으면 처음 책 3권으로 시작한다")
    void s3() throws Exception {
        assertFalse(Files.exists(FILE));
        Object adapter = newAdapter();

        assertEquals(3, Files.readAllLines(FILE).size());
        for (String id : new String[] {"B1", "B2", "B3"}) {
            Object book = findById(adapter, id);
            assertEquals(id, book.getClass().getMethod("getId").invoke(book));
        }
    }

    @Test
    @DisplayName("S4 Main은 메모리 저장 대신 파일 저장을 끼운다. 서비스는 고치지 않는다. 프로그램을 껐다 켜도 대출 기록이 남는다")
    void s4() throws Exception {
        assertTrue(Files.readString(Path.of("hexa/src/hexa/Main.java")).contains("new FileBookAdapter()"));
        assertFalse(Files.readString(Path.of("hexa/src/hexa/application/LoanService.java")).contains("File"));

        String first = runMain("1\nM1\nB1\n0\n");
        assertTrue(first.contains("객체지향의 사실과 오해 대출했습니다"));

        String second = runMain("1\nM2\nB1\n0\n");
        assertTrue(second.contains("이미 대출 중인 책입니다"));
        assertFalse(second.contains("대출했습니다"));

        String third = runMain("2\nB1\n1\nM2\nB1\n0\n");
        assertTrue(third.contains("객체지향의 사실과 오해 반납했습니다"));
        assertTrue(third.contains("객체지향의 사실과 오해 대출했습니다"));
    }
}
