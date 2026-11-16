package hexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
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

class BookListTest {

    private static final Path SRC = Path.of("hexa/src/hexa");

    @BeforeEach
    @AfterEach
    void clean() throws Exception {
        Files.deleteIfExists(Path.of("data/books.txt"));
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

    private List<String> ids(Object books) throws Exception {
        List<String> result = new ArrayList<>();
        for (Object book : (List<?>) books) {
            result.add((String) book.getClass().getMethod("getId").invoke(book));
        }
        return result;
    }

    private Object get(Object view, String name) throws Exception {
        return view.getClass().getMethod(name).invoke(view);
    }

    @Test
    @DisplayName("S1 메뉴에 3 목록을 더한다. 고르면 책마다 책 번호, 제목, 대출 상태를 한 줄씩 찍는다. 대출 상태는 대출 가능 또는 대출 중이다")
    void s1() {
        String first = runMain("3\n0\n");
        assertTrue(first.contains("1 대출  2 반납  3 목록  0 종료"), first);
        assertTrue(first.contains("B1 객체지향의 사실과 오해 대출 가능"), first);
        assertTrue(first.contains("B2 클린 아키텍처 대출 가능"), first);
        assertTrue(first.contains("B3 도메인 주도 설계 첫걸음 대출 가능"), first);
        assertTrue(first.indexOf("B1 객체") < first.indexOf("B2 클린"));
        assertTrue(first.indexOf("B2 클린") < first.indexOf("B3 도메인"));

        String second = runMain("1\nM1\nB1\n3\n0\n");
        assertTrue(second.contains("B1 객체지향의 사실과 오해 대출 중"), second);
        assertTrue(second.contains("B2 클린 아키텍처 대출 가능"), second);
    }

    @Test
    @DisplayName("S2 목록은 상태를 바꾸지 않는 조회 유스케이스다. 들어오는 포트로 열고 나가는 포트의 전체 찾기로 읽는다. 메모리 저장과 파일 저장 모두 전체 찾기를 한다")
    void s2() throws Exception {
        Method findAll = Find.type("BookPort").getMethod("findAll");
        assertEquals(List.class, findAll.getReturnType());
        List<String> expected = List.of("B1", "B2", "B3");
        assertEquals(expected, ids(findAll.invoke(Find.type("MemoryBookAdapter").getConstructor().newInstance())));
        assertEquals(expected, ids(findAll.invoke(Find.type("FileBookAdapter").getConstructor().newInstance())));

        Class<?> port = Find.type("LoanUseCase");
        Method listBooks = port.getMethod("listBooks");
        assertEquals(List.class, listBooks.getReturnType());

        Object service = Find.newLoanService();
        Object before = listBooks.invoke(service);
        assertEquals(before, listBooks.invoke(service));
        assertEquals(3, ((List<?>) before).size());

        Find.borrow(service, "M1", "B2");
        List<?> after = (List<?>) listBooks.invoke(service);
        assertEquals("B1", get(after.get(0), "id"));
        assertEquals(false, get(after.get(0), "borrowed"));
        assertEquals("B2", get(after.get(1), "id"));
        assertEquals("클린 아키텍처", get(after.get(1), "title"));
        assertEquals(true, get(after.get(1), "borrowed"));
        assertEquals(false, get(after.get(2), "borrowed"));
    }

    @Test
    @DisplayName("S3 들어오는 포트는 도메인 책 대신 응답 모델을 돌려준다. 응답 모델은 책 번호, 제목, 대출 여부만 담고 상태를 바꾸는 메서드가 없다")
    void s3() throws Exception {
        Class<?> view = Find.type("BookView");
        assertEquals("hexa.application.port.in", view.getPackageName());
        assertTrue(view.isRecord());
        List<String> names = new ArrayList<>();
        for (RecordComponent component : view.getRecordComponents()) {
            names.add(component.getName());
        }
        assertEquals(List.of("id", "title", "borrowed"), names.subList(0, 3));
        for (Method method : view.getMethods()) {
            assertFalse(method.getName().equals("borrow") || method.getName().equals("returnBook"), method.getName());
        }

        Class<?> port = Find.type("LoanUseCase");
        Method borrow = Find.borrowMethod();
        Method returnBook = port.getMethod("returnBook", String.class);
        assertEquals(view, borrow.getReturnType());
        assertEquals(view, returnBook.getReturnType());

        Object service = Find.newLoanService();
        Object borrowed = Find.borrow(service, "M1", "B1");
        assertEquals("B1", get(borrowed, "id"));
        assertEquals("객체지향의 사실과 오해", get(borrowed, "title"));
        assertEquals(true, get(borrowed, "borrowed"));
        assertEquals(false, get(returnBook.invoke(service, "B1"), "borrowed"));
    }

    @Test
    @DisplayName("S4 콘솔 메뉴와 두 번째 입구는 도메인 책을 모른다")
    void s4() throws Exception {
        for (Path file : List.of(SRC.resolve("adapter/in/ConsoleMenu.java"), SRC.resolve("LoanCheck.java"))) {
            String source = Files.readString(file);
            assertFalse(source.contains("import hexa.domain."), file.toString());
            assertTrue(source.contains("import hexa.application.port.in.BookView;"), file.toString());
        }
    }
}
