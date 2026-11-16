package hexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
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

class OverdueNoticeTest {

    private static final Path SRC = Path.of("hexa/src/hexa");
    private static final Path DATA = Path.of("data");
    private static final Path BOOKS = DATA.resolve("books.txt");
    private static final Path NOTICES = DATA.resolve("notices.txt");
    private static final String MENU = "1 대출  2 반납  3 목록  4 연체 알림  0 종료";
    private static final String LATE_LINE = "M1,객체지향의 사실과 오해 반납 기한 2020-01-01이 지났습니다";

    @BeforeEach
    @AfterEach
    void clean() throws Exception {
        Files.deleteIfExists(BOOKS);
        Files.deleteIfExists(NOTICES);
        Files.deleteIfExists(DATA);
    }

    private void writeLateBooks() throws Exception {
        Files.createDirectories(DATA);
        Files.write(BOOKS, List.of("B1,객체지향의 사실과 오해,M1,2020-01-01", "B2,클린 아키텍처,,", "B3,도메인 주도 설계 첫걸음,,"));
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

    private Object clock(LocalDate day) throws Exception {
        Class<?> port = Find.type("ClockPort");
        return Proxy.newProxyInstance(port.getClassLoader(), new Class<?>[] {port},
                (proxy, method, args) -> method.getName().equals("today") ? day : null);
    }

    private Object notices(List<String> sent) throws Exception {
        Class<?> port = Find.type("NotifyPort");
        return Proxy.newProxyInstance(port.getClassLoader(), new Class<?>[] {port}, (proxy, method, args) -> {
            if (method.getName().equals("send")) {
                sent.add(args[0] + "|" + args[1]);
            }
            return null;
        });
    }

    private Object service(Object store, LocalDate day, List<String> sent) throws Exception {
        return Find.type("LoanService")
                .getConstructor(Find.type("BookPort"), Find.type("ClockPort"), Find.type("NotifyPort"))
                .newInstance(store, clock(day), notices(sent));
    }

    private int notifyOverdue(Object service) throws Exception {
        return (int) Find.type("LoanUseCase").getMethod("notifyOverdue").invoke(service);
    }

    @Test
    @DisplayName("S1 메뉴에 4 연체 알림을 더한다. 고르면 연체된 책마다 빌린 회원에게 알림을 보내고 연체 알림 N건을 보냈습니다를 보여 준다")
    void s1() throws Exception {
        String none = runMain("4\n0\n");
        assertTrue(none.contains(MENU), none);
        assertTrue(none.contains("연체 알림 0건을 보냈습니다"), none);

        clean();
        writeLateBooks();
        String late = runMain("4\n0\n");
        assertTrue(late.contains("연체 알림 1건을 보냈습니다"), late);
        assertTrue(late.contains("프로그램을 끝냅니다"), late);
    }

    @Test
    @DisplayName("S2 알림 글은 책 제목, 반납 기한, 지났다는 말을 담는다. 연체가 아닌 책과 빌리지 않은 책에는 보내지 않는다")
    void s2() throws Exception {
        Object store = Find.newBookStore();
        List<String> sent = new ArrayList<>();
        Find.borrow(service(store, LocalDate.of(2026, 3, 2), sent), "M1", "B1");
        Find.borrow(service(store, LocalDate.of(2026, 3, 10), sent), "M2", "B2");

        assertEquals(0, notifyOverdue(service(store, LocalDate.of(2026, 3, 16), sent)));
        assertEquals(List.of(), sent);

        assertEquals(1, notifyOverdue(service(store, LocalDate.of(2026, 3, 17), sent)));
        assertEquals(1, sent.size(), sent.toString());
        String[] notice = sent.get(0).split("\\|");
        assertEquals("M1", notice[0]);
        assertTrue(notice[1].contains("객체지향의 사실과 오해"), notice[1]);
        assertTrue(notice[1].contains("2026-03-16"), notice[1]);
        assertTrue(notice[1].contains("지났습니다"), notice[1]);

        sent.clear();
        assertEquals(2, notifyOverdue(service(store, LocalDate.of(2026, 4, 1), sent)));
        assertEquals(2, sent.size(), sent.toString());
    }

    @Test
    @DisplayName("S3 서비스는 알림을 나가는 포트인 알림 포트로 보낸다. 서비스는 알림이 어디로 가는지 모른다")
    void s3() throws Exception {
        Class<?> port = Find.type("NotifyPort");
        assertTrue(port.isInterface());
        assertEquals("hexa.application.port.out", port.getPackageName());
        port.getMethod("send", String.class, String.class);

        boolean usesPort = false;
        for (Field field : Find.type("LoanService").getDeclaredFields()) {
            if (field.getType().equals(port)) {
                usesPort = true;
            }
        }
        assertTrue(usesPort);

        try (Stream<Path> files = Files.walk(SRC.resolve("application"))) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String source = Files.readString(file);
                assertFalse(source.contains("System.out"), file.toString());
                assertFalse(source.contains("notices"), file.toString());
            }
        }
    }

    @Test
    @DisplayName("S4 콘솔 알림 어댑터는 받는 회원과 알림 글을 콘솔에 찍는다. 두 번째 입구는 콘솔 알림을 끼워 15일 뒤 연체 알림을 바로 보여 준다")
    void s4() throws Exception {
        Class<?> adapter = Find.type("ConsoleNotifyAdapter");
        assertEquals("hexa.adapter.out", adapter.getPackageName());
        assertTrue(Find.type("NotifyPort").isAssignableFrom(adapter));

        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            adapter.getMethod("send", String.class, String.class)
                    .invoke(adapter.getConstructor().newInstance(), "M1", "알림 글");
        } finally {
            System.setOut(originalOut);
        }
        assertTrue(out.toString(StandardCharsets.UTF_8).contains("M1에게 알림: 알림 글"));

        assertTrue(Files.readString(SRC.resolve("LoanCheck.java")).contains("new ConsoleNotifyAdapter()"));
        String check = run(Find.type("LoanCheck").getMethod("main", String[].class), "");
        assertTrue(check.contains("M1에게 알림: 객체지향의 사실과 오해 반납 기한 2026-03-16이 지났습니다"), check);
        assertTrue(check.contains("연체 알림: 1건"), check);
        assertTrue(check.indexOf("연체 알림: 1건") < check.indexOf("B1 반납: "), check);
    }

    @Test
    @DisplayName("S5 파일 알림 어댑터를 하나 더 꽂는다. 알림을 저장 폴더의 알림 파일에 한 줄씩 더한다. Main은 한 줄만 바꿔 파일 알림을 끼우고 두 번째 입구는 콘솔 알림 그대로다")
    void s5() throws Exception {
        Class<?> adapter = Find.type("FileNotifyAdapter");
        assertEquals("hexa.adapter.out", adapter.getPackageName());
        assertTrue(Find.type("NotifyPort").isAssignableFrom(adapter));

        String main = Files.readString(SRC.resolve("Main.java"));
        assertTrue(main.contains("new FileNotifyAdapter()"), main);
        assertFalse(main.contains("ConsoleNotifyAdapter"), main);

        writeLateBooks();
        String out = runMain("4\n4\n0\n");
        assertTrue(out.contains("연체 알림 1건을 보냈습니다"), out);
        assertFalse(out.contains("에게 알림:"), out);
        assertEquals(List.of(LATE_LINE, LATE_LINE), Files.readAllLines(NOTICES));

        String check = run(Find.type("LoanCheck").getMethod("main", String[].class), "");
        assertTrue(check.contains("M1에게 알림: "), check);
        assertEquals(List.of(LATE_LINE, LATE_LINE), Files.readAllLines(NOTICES));
    }
}
