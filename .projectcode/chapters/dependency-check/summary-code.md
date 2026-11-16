# dependency-check 코드 요약

## 테스트 작성
커밋: 0e2e22e
- 학생 코드 변경 없음

## T1 JUnit을 붙이고 도메인 규칙 테스트하기
커밋: 70daf56

| 파일 | 변경 |
|---|---|
| hexa/lib/junit-platform-console-standalone.jar | 새로. tests/lib의 jar를 복사 |
| hexa/.classpath | 고침 |
| hexa/test/hexa/DependencyRuleTest.java | 새로 |

```diff
// hexa/.classpath
@@ -1,6 +1,8 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <classpath>
     <classpathentry kind="src" path="src"/>
+    <classpathentry kind="src" path="test"/>
     <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-21"/>
+    <classpathentry kind="lib" path="lib/junit-platform-console-standalone.jar"/>
     <classpathentry kind="output" path="bin"/>
 </classpath>
```

```java
// hexa/test/hexa/DependencyRuleTest.java
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
```

## T2 application과 어댑터 규칙 더하기
커밋: 2ffe037

| 파일 | 변경 |
|---|---|
| hexa/test/hexa/DependencyRuleTest.java | 고침 |

```diff
// hexa/test/hexa/DependencyRuleTest.java
@@ -21,6 +21,23 @@ class DependencyRuleTest {
         }
     }
 
+    @Test
+    void applicationDoesNotKnowAdapter() throws IOException {
+        for (String line : importsIn("src/hexa/application")) {
+            assertFalse(line.startsWith("import hexa.adapter."), line);
+        }
+    }
+
+    @Test
+    void adaptersDoNotKnowEachOther() throws IOException {
+        for (String line : importsIn("src/hexa/adapter/in")) {
+            assertFalse(line.startsWith("import hexa.adapter.out."), line);
+        }
+        for (String line : importsIn("src/hexa/adapter/out")) {
+            assertFalse(line.startsWith("import hexa.adapter.in."), line);
+        }
+    }
+
     private List<String> importsIn(String folder) throws IOException {
         List<String> imports = new ArrayList<>();
         try (Stream<Path> files = Files.walk(Path.of(folder))) {
```
