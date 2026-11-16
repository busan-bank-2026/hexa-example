# file-store 코드 요약

## 테스트 작성
커밋: fc4afbd
- 학생 코드 변경 없음

## T1 파일 저장 어댑터 만들기
커밋: 5af26a2

- toLine, toBook은 저장 모델 매핑이라 따라 친다
- readAll, writeAll은 파일 읽기 쓰기라 붙여넣는다

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/adapter/out/FileBookAdapter.java | 새로 |
| hexa/src/hexa/domain/Book.java | 고침 |

```java
// hexa/src/hexa/adapter/out/FileBookAdapter.java
package hexa.adapter.out;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hexa.application.port.out.BookPort;
import hexa.domain.Book;

public class FileBookAdapter implements BookPort {
    private final Path file = Path.of("data/books.txt");

    public FileBookAdapter() {
        if (Files.notExists(file)) {
            save(new Book("B1", "객체지향의 사실과 오해"));
            save(new Book("B2", "클린 아키텍처"));
            save(new Book("B3", "도메인 주도 설계 첫걸음"));
        }
    }

    @Override
    public Book findById(String id) {
        return readAll().get(id);
    }

    @Override
    public void save(Book book) {
        Map<String, Book> books = readAll();
        books.put(book.getId(), book);
        writeAll(books);
    }

    private String toLine(Book book) {
        String borrowerId = book.getBorrowerId() == null ? "" : book.getBorrowerId();
        return book.getId() + "," + book.getTitle() + "," + borrowerId;
    }

    private Book toBook(String line) {
        String[] parts = line.split(",", -1);
        Book book = new Book(parts[0], parts[1]);
        if (!parts[2].isEmpty()) {
            book.borrow(parts[2]);
        }
        return book;
    }

    private Map<String, Book> readAll() {
        Map<String, Book> books = new LinkedHashMap<>();
        if (Files.notExists(file)) {
            return books;
        }
        try {
            for (String line : Files.readAllLines(file)) {
                Book book = toBook(line);
                books.put(book.getId(), book);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return books;
    }

    private void writeAll(Map<String, Book> books) {
        List<String> lines = new ArrayList<>();
        for (Book book : books.values()) {
            lines.add(toLine(book));
        }
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, lines);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
```

```diff
// hexa/src/hexa/domain/Book.java
@@ -28,4 +28,8 @@ public class Book {
     public String getTitle() {
         return title;
     }
+
+    public String getBorrowerId() {
+        return borrowerId;
+    }
 }
```

## T2 Main에 파일 저장 끼우기
커밋: 7f888bf

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/Main.java | 고침 |

- 학생 코드 밖에서 .gitignore에 hexa/data/를 더했다

```diff
// hexa/src/hexa/Main.java
@@ -1,7 +1,7 @@
 package hexa;
 
 import hexa.adapter.in.ConsoleMenu;
-import hexa.adapter.out.MemoryBookAdapter;
+import hexa.adapter.out.FileBookAdapter;
 import hexa.application.LoanService;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
@@ -10,7 +10,7 @@ public class Main {
 
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
-        BookPort bookPort = new MemoryBookAdapter();
+        BookPort bookPort = new FileBookAdapter();
         LoanUseCase loanUseCase = new LoanService(bookPort);
         new ConsoleMenu(loanUseCase).run();
     }
```
