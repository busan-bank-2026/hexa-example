package hexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StorePortTest {

    @Test
    @DisplayName("S1 서비스가 쓰는 저장을 나가는 포트로 연다. 책 찾기와 책 저장 두 가지다")
    void s1() throws Exception {
        Class<?> port = Find.type("BookPort");
        assertTrue(port.isInterface());
        port.getMethod("findById", String.class);
        port.getMethod("save", Find.type("Book"));

        boolean usesPort = false;
        for (Field field : Find.type("LoanService").getDeclaredFields()) {
            if (field.getType().equals(port)) {
                usesPort = true;
            }
        }
        assertTrue(usesPort);
    }

    @Test
    @DisplayName("S2 서비스는 저장 클래스를 만들지 않고 포트를 받는다. 무엇을 넣을지는 Main과 두 번째 입구가 정한다")
    void s2() throws Exception {
        Constructor<?>[] constructors = Find.type("LoanService").getConstructors();
        assertEquals(1, constructors.length);
        assertEquals(1, constructors[0].getParameterCount());
        assertEquals(Find.type("BookPort"), constructors[0].getParameterTypes()[0]);
    }

    @Test
    @DisplayName("S3 메모리 저장을 포트를 구현한 드리븐 어댑터로 바꾼다")
    void s3() throws Exception {
        Class<?> adapter = Find.type("MemoryBookAdapter");
        assertTrue(Find.type("BookPort").isAssignableFrom(adapter));
        assertFalse(Find.exists("BookRepository"));

        Object store = adapter.getConstructor().newInstance();
        Method findById = adapter.getMethod("findById", String.class);
        for (String id : new String[] {"B1", "B2", "B3"}) {
            Object book = findById.invoke(store, id);
            assertEquals(id, book.getClass().getMethod("getId").invoke(book));
        }
    }
}
