package hexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HexagonPackagesTest {

    private static final Path SRC = Path.of("hexa/src/hexa");

    private String packageOf(String simpleName) throws Exception {
        return Find.type(simpleName).getPackageName();
    }

    private List<String> imports(Path folder) throws Exception {
        List<String> result = new ArrayList<>();
        if (!Files.isDirectory(folder)) {
            return result;
        }
        try (Stream<Path> files = Files.walk(folder)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                for (String line : Files.readAllLines(file)) {
                    if (line.startsWith("import hexa.")) {
                        result.add(line);
                    }
                }
            }
        }
        return result;
    }

    @Test
    @DisplayName("S1 책을 domain 패키지로 옮긴다. 도메인은 다른 패키지를 모른다")
    void s1() throws Exception {
        assertEquals("hexa.domain", packageOf("Book"));
        assertTrue(Files.exists(SRC.resolve("domain/Book.java")));
        assertEquals(List.of(), imports(SRC.resolve("domain")));
    }

    @Test
    @DisplayName("S2 서비스와 두 포트를 application 패키지로 옮긴다. 들어오는 포트는 port.in, 나가는 포트는 port.out에 둔다. application은 domain만 안다")
    void s2() throws Exception {
        assertEquals("hexa.application", packageOf("LoanService"));
        assertEquals("hexa.application.port.in", packageOf("LoanUseCase"));
        assertEquals("hexa.application.port.out", packageOf("BookPort"));
        for (String line : imports(SRC.resolve("application"))) {
            assertTrue(line.startsWith("import hexa.domain.") || line.startsWith("import hexa.application."), line);
        }
        assertTrue(imports(SRC.resolve("application")).contains("import hexa.domain.Book;"));
    }

    @Test
    @DisplayName("S3 콘솔 메뉴와 메모리 저장을 adapter 패키지로 옮긴다. 콘솔 메뉴는 adapter.in, 메모리 저장은 adapter.out에 둔다. 어댑터는 포트와 도메인만 안다")
    void s3() throws Exception {
        assertEquals("hexa.adapter.in", packageOf("ConsoleMenu"));
        assertEquals("hexa.adapter.out", packageOf("MemoryBookAdapter"));
        List<String> adapterImports = imports(SRC.resolve("adapter"));
        assertFalse(adapterImports.isEmpty());
        for (String line : adapterImports) {
            assertTrue(line.startsWith("import hexa.domain.") || line.startsWith("import hexa.application.port."), line);
        }
    }

    @Test
    @DisplayName("S4 Main과 두 번째 입구는 hexa 패키지에 남아 어댑터와 서비스를 만들어 끼운다. 모든 패키지를 아는 것은 이 두 입구뿐이다")
    void s4() throws Exception {
        assertEquals("hexa", packageOf("Main"));
        assertEquals("hexa", packageOf("LoanCheck"));

        List<String> rootFiles = new ArrayList<>();
        try (Stream<Path> files = Files.list(SRC)) {
            files.filter(Files::isRegularFile).forEach(p -> rootFiles.add(p.getFileName().toString()));
        }
        rootFiles.sort(null);
        assertEquals(List.of("LoanCheck.java", "Main.java"), rootFiles);

        for (String entry : List.of("Main.java", "LoanCheck.java")) {
            String source = Files.readString(SRC.resolve(entry));
            assertTrue(source.contains("import hexa.adapter.out."), entry);
            assertTrue(source.contains("import hexa.application.LoanService;"), entry);
        }
    }
}
