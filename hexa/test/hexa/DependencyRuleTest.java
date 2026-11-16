package hexa;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class DependencyRuleTest {

    @Test
    void domainKnowsOnlyDomain() throws IOException {
        for (String line : importsIn("src/hexa/domain")) {
            assertFalse(line.startsWith("import hexa.application."), line);
            assertFalse(line.startsWith("import hexa.adapter."), line);
        }
    }

    @Test
    void applicationDoesNotKnowAdapter() throws IOException {
        for (String line : importsIn("src/hexa/application")) {
            assertFalse(line.startsWith("import hexa.adapter."), line);
        }
    }

    @Test
    void adaptersDoNotKnowEachOther() throws IOException {
        for (String line : importsIn("src/hexa/adapter/in")) {
            assertFalse(line.startsWith("import hexa.adapter.out."), line);
        }
        for (String line : importsIn("src/hexa/adapter/out")) {
            assertFalse(line.startsWith("import hexa.adapter.in."), line);
        }
    }

    private List<String> importsIn(String folder) throws IOException {
        List<String> imports = new ArrayList<>();
        try (Stream<Path> files = Files.walk(Path.of(folder))) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                for (String line : Files.readAllLines(file)) {
                    if (line.startsWith("import hexa.")) {
                        imports.add(line);
                    }
                }
            }
        }
        return imports;
    }
}
