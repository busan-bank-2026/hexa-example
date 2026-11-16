package hexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UseCasePortTest {

    private String captureOut(ThrowingRunnable action) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private Object newService() throws Exception {
        return Class.forName("hexa.LoanService").getConstructor().newInstance();
    }

    private String title(Object book) throws Exception {
        return (String) book.getClass().getMethod("getTitle").invoke(book);
    }

    @Test
    @DisplayName("S1 대출과 반납을 유스케이스 포트로 연다")
    void s1() throws Exception {
        Class<?> port = Class.forName("hexa.LoanUseCase");
        assertTrue(port.isInterface());
        assertTrue(port.isAssignableFrom(Class.forName("hexa.LoanService")));

        Method borrow = port.getMethod("borrow", String.class, String.class);
        Method returnBook = port.getMethod("returnBook", String.class);
        Object service = newService();
        assertEquals("클린 아키텍처", title(borrow.invoke(service, "M1", "B2")));
        assertEquals("클린 아키텍처", title(returnBook.invoke(service, "B2")));

        Class<?> menu = Class.forName("hexa.ConsoleMenu");
        assertTrue(menu.getConstructor(port) != null);
    }

    @Test
    @DisplayName("S2 서비스는 콘솔을 쓰지 않는다. 받은 값으로 대출과 반납을 하고 결과로 책을 돌려준다")
    void s2() throws Exception {
        Object service = newService();
        Method borrow = service.getClass().getMethod("borrow", String.class, String.class);
        Method returnBook = service.getClass().getMethod("returnBook", String.class);
        Object[] books = new Object[2];
        String out = captureOut(() -> {
            books[0] = borrow.invoke(service, "M1", "B1");
            books[1] = returnBook.invoke(service, "B1");
            books[0] = borrow.invoke(service, "M2", "B1");
        });
        assertEquals("", out);
        assertEquals("객체지향의 사실과 오해", title(books[0]));
        assertEquals("객체지향의 사실과 오해", title(books[1]));
    }

    @Test
    @DisplayName("S4 콘솔 없이 포트를 부르는 두 번째 입구를 만든다. 실행하면 대출과 반납 결과를 찍는다")
    void s4() throws Exception {
        Method main = Class.forName("hexa.LoanCheck").getMethod("main", String[].class);
        String out = captureOut(() -> main.invoke(null, (Object) new String[0]));
        assertTrue(out.contains("B1을 M1에게 대출: 객체지향의 사실과 오해"));
        assertTrue(out.contains("B1 반납: 객체지향의 사실과 오해"));
    }
}
