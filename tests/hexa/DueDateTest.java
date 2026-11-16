package hexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DueDateTest {

    private static final Path SRC = Path.of("hexa/src/hexa");
    private static final Path DATA = Path.of("data");
    private static final Path FILE = DATA.resolve("books.txt");

    @BeforeEach
    @AfterEach
    void clean() throws Exception {
        Files.deleteIfExists(FILE);
        Files.deleteIfExists(DATA);
    }

    private String due() {
        return LocalDate.now().plusDays(14).toString();
    }

    private String run(Method main, String input) throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            main.invoke(null, (Object) new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    private String runMain(String input) throws Exception {
        return run(Main.class.getMethod("main", String[].class), input);
    }

    private Object get(Object view, String name) throws Exception {
        return view.getClass().getMethod(name).invoke(view);
    }

    private Object clock(LocalDate day) throws Exception {
        Class<?> port = Find.type("ClockPort");
        return Proxy.newProxyInstance(port.getClassLoader(), new Class<?>[] {port},
                (proxy, method, args) -> method.getName().equals("today") ? day : null);
    }

    private Object service(Object store, Object clock) throws Exception {
        Class<?> service = Find.type("LoanService");
        if (Find.exists("NotifyPort")) {
            return service.getConstructor(Find.type("BookPort"), Find.type("ClockPort"), Find.type("NotifyPort"))
                    .newInstance(store, clock, Find.silentNotices());
        }
        return service.getConstructor(Find.type("BookPort"), Find.type("ClockPort")).newInstance(store, clock);
    }

    private Object firstBook(Object service) throws Exception {
        List<?> books = (List<?>) Find.type("LoanUseCase").getMethod("listBooks").invoke(service);
        return books.get(0);
    }

    @Test
    @DisplayName("S1 대출하면 반납 기한을 오늘부터 14일 뒤로 정한다. 콘솔 메뉴는 대출 결과 아래에 반납 기한을 보여 준다")
    void s1() throws Exception {
        Object borrowed = Find.borrow(Find.newLoanService(), "M1", "B1");
        assertEquals(LocalDate.now().plusDays(14), get(borrowed, "dueDate"));

        String out = runMain("1\nM1\nB1\n0\n");
        assertTrue(out.contains("객체지향의 사실과 오해 대출했습니다"), out);
        assertTrue(out.contains("반납 기한: " + due()), out);
        assertTrue(out.indexOf("대출했습니다") < out.indexOf("반납 기한: "), out);
    }

    @Test
    @DisplayName("S2 저장 파일 한 줄 끝에 반납 기한을 더한다. 껐다 켜도 기한이 남고 반납하면 기한이 비워진다")
    void s2() throws Exception {
        runMain("1\nM1\nB1\n0\n");
        List<String> lines = Files.readAllLines(FILE);
        assertTrue(lines.contains("B1,객체지향의 사실과 오해,M1," + due()), lines.toString());
        assertTrue(lines.contains("B2,클린 아키텍처,,"), lines.toString());

        String again = runMain("3\n0\n");
        assertTrue(again.contains("B1 객체지향의 사실과 오해 대출 중 반납 기한 " + due()), again);

        runMain("2\nB1\n0\n");
        assertTrue(Files.readAllLines(FILE).contains("B1,객체지향의 사실과 오해,,"));
    }

    @Test
    @DisplayName("S3 목록은 대출 중인 책의 반납 기한을 보여 주고 오늘이 기한을 지난 책에 연체를 붙인다")
    void s3() throws Exception {
        String out = runMain("1\nM1\nB1\n3\n0\n");
        assertTrue(out.contains("B1 객체지향의 사실과 오해 대출 중 반납 기한 " + due()), out);
        assertTrue(out.contains("B2 클린 아키텍처 대출 가능"), out);
        assertFalse(out.contains("연체"), out);

        Files.write(FILE, List.of("B1,객체지향의 사실과 오해,M1,2020-01-01", "B2,클린 아키텍처,,", "B3,도메인 주도 설계 첫걸음,,"));
        String late = runMain("3\n0\n");
        assertTrue(late.contains("B1 객체지향의 사실과 오해 대출 중 반납 기한 2020-01-01 연체"), late);
        assertTrue(late.contains("B2 클린 아키텍처 대출 가능"), late);
        assertEquals(1, late.split("연체", -1).length - 1, late);
    }

    @Test
    @DisplayName("S4 서비스는 오늘 날짜를 나가는 포트인 시계 포트에 묻는다. 서비스와 도메인은 시스템 날짜를 직접 읽지 않는다")
    void s4() throws Exception {
        Class<?> port = Find.type("ClockPort");
        assertTrue(port.isInterface());
        assertEquals("hexa.application.port.out", port.getPackageName());
        assertEquals(LocalDate.class, port.getMethod("today").getReturnType());

        List<Path> inside = new ArrayList<>();
        for (String folder : List.of("domain", "application")) {
            try (Stream<Path> files = Files.walk(SRC.resolve(folder))) {
                inside.addAll(files.filter(p -> p.toString().endsWith(".java")).toList());
            }
        }
        for (Path file : inside) {
            assertFalse(Files.readString(file).contains("LocalDate.now"), file.toString());
        }

        Object store = Find.newBookStore();
        Object borrowed = Find.borrow(service(store, clock(LocalDate.of(2026, 3, 2))), "M1", "B1");
        assertEquals(LocalDate.of(2026, 3, 16), get(borrowed, "dueDate"));
        assertEquals(false, get(firstBook(service(store, clock(LocalDate.of(2026, 3, 16)))), "overdue"));
        assertEquals(true, get(firstBook(service(store, clock(LocalDate.of(2026, 3, 17)))), "overdue"));
    }

    @Test
    @DisplayName("S5 시계 어댑터를 갈아 끼운다. Main은 시스템 날짜 시계를 끼우고 두 번째 입구는 날짜를 정해 둔 시계를 끼워 15일 뒤 연체를 바로 보여 준다")
    void s5() throws Exception {
        Class<?> port = Find.type("ClockPort");
        Class<?> system = Find.type("SystemClockAdapter");
        Class<?> fixed = Find.type("FixedClockAdapter");
        for (Class<?> adapter : List.of(system, fixed)) {
            assertEquals("hexa.adapter.out", adapter.getPackageName());
            assertTrue(port.isAssignableFrom(adapter));
        }
        Method today = port.getMethod("today");
        assertEquals(LocalDate.now(), today.invoke(system.getConstructor().newInstance()));
        LocalDate day = LocalDate.of(2026, 3, 2);
        assertEquals(day, today.invoke(fixed.getConstructor(LocalDate.class).newInstance(day)));

        assertTrue(Files.readString(SRC.resolve("Main.java")).contains("new SystemClockAdapter()"));
        assertTrue(Files.readString(SRC.resolve("LoanCheck.java")).contains("new FixedClockAdapter("));

        String out = run(Find.type("LoanCheck").getMethod("main", String[].class), "");
        assertTrue(out.contains("반납 기한: 2026-03-16"), out);
        assertTrue(out.contains("15일 뒤 B1 연체: true"), out);
    }
}
