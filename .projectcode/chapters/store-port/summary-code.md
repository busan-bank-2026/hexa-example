# store-port 코드 요약

## 테스트 작성
커밋: 561091e
- 학생 코드 변경 없음

## T1 저장 포트 열기
커밋: e0ebca4

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/BookPort.java | 새로 |
| hexa/src/hexa/BookRepository.java | 고침 |
| hexa/src/hexa/LoanService.java | 고침 |

```java
// hexa/src/hexa/BookPort.java
package hexa;

public interface BookPort {
    Book findById(String id);

    void save(Book book);
}
```

```diff
// hexa/src/hexa/BookRepository.java
@@ -3,7 +3,7 @@ package hexa;
 import java.util.HashMap;
 import java.util.Map;
 
-public class BookRepository {
+public class BookRepository implements BookPort {
     private final Map<String, Book> books = new HashMap<>();
 
     public BookRepository() {
@@ -12,10 +12,12 @@ public class BookRepository {
         save(new Book("B3", "도메인 주도 설계 첫걸음"));
     }
 
+    @Override
     public Book findById(String id) {
         return books.get(id);
     }
 
+    @Override
     public void save(Book book) {
         books.put(book.getId(), book);
     }
```

```diff
// hexa/src/hexa/LoanService.java
@@ -1,21 +1,21 @@
 package hexa;
 
 public class LoanService implements LoanUseCase {
-    private final BookRepository repository = new BookRepository();
+    private final BookPort bookPort = new BookRepository();
 
     @Override
     public Book borrow(String memberId, String bookId) {
-        Book book = repository.findById(bookId);
+        Book book = bookPort.findById(bookId);
         book.borrow(memberId);
-        repository.save(book);
+        bookPort.save(book);
         return book;
     }
 
     @Override
     public Book returnBook(String bookId) {
-        Book book = repository.findById(bookId);
+        Book book = bookPort.findById(bookId);
         book.returnBook();
-        repository.save(book);
+        bookPort.save(book);
         return book;
     }
 }
```

## T2 서비스가 포트를 받게 하기
커밋: 71fd74b

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/LoanService.java | 고침 |
| hexa/src/hexa/Main.java | 고침 |
| hexa/src/hexa/LoanCheck.java | 고침 |

```diff
// hexa/src/hexa/LoanService.java
@@ -1,7 +1,11 @@
 package hexa;
 
 public class LoanService implements LoanUseCase {
-    private final BookPort bookPort = new BookRepository();
+    private final BookPort bookPort;
+
+    public LoanService(BookPort bookPort) {
+        this.bookPort = bookPort;
+    }
 
     @Override
     public Book borrow(String memberId, String bookId) {
```

```diff
// hexa/src/hexa/Main.java
@@ -4,7 +4,8 @@ public class Main {
 
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
-        LoanUseCase loanUseCase = new LoanService();
+        BookPort bookPort = new BookRepository();
+        LoanUseCase loanUseCase = new LoanService(bookPort);
         new ConsoleMenu(loanUseCase).run();
     }
 }
```

```diff
// hexa/src/hexa/LoanCheck.java
@@ -3,7 +3,8 @@ package hexa;
 public class LoanCheck {
 
     public static void main(String[] args) {
-        LoanUseCase loanUseCase = new LoanService();
+        BookPort bookPort = new BookRepository();
+        LoanUseCase loanUseCase = new LoanService(bookPort);
 
         Book borrowed = loanUseCase.borrow("M1", "B1");
         System.out.println("B1을 M1에게 대출: " + borrowed.getTitle());
```

## T3 메모리 저장을 어댑터로 바꾸기
커밋: f828125

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/MemoryBookAdapter.java | 새로 |
| hexa/src/hexa/BookRepository.java | 지움 |
| hexa/src/hexa/Main.java | 고침 |
| hexa/src/hexa/LoanCheck.java | 고침 |

```java
// hexa/src/hexa/MemoryBookAdapter.java
package hexa;

import java.util.HashMap;
import java.util.Map;

public class MemoryBookAdapter implements BookPort {
    private final Map<String, Book> books = new HashMap<>();

    public MemoryBookAdapter() {
        save(new Book("B1", "객체지향의 사실과 오해"));
        save(new Book("B2", "클린 아키텍처"));
        save(new Book("B3", "도메인 주도 설계 첫걸음"));
    }

    @Override
    public Book findById(String id) {
        return books.get(id);
    }

    @Override
    public void save(Book book) {
        books.put(book.getId(), book);
    }
}
```

```diff
// hexa/src/hexa/Main.java
@@ -4,7 +4,7 @@ public class Main {
 
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
-        BookPort bookPort = new BookRepository();
+        BookPort bookPort = new MemoryBookAdapter();
         LoanUseCase loanUseCase = new LoanService(bookPort);
         new ConsoleMenu(loanUseCase).run();
     }
```

```diff
// hexa/src/hexa/LoanCheck.java
@@ -3,7 +3,7 @@ package hexa;
 public class LoanCheck {
 
     public static void main(String[] args) {
-        BookPort bookPort = new BookRepository();
+        BookPort bookPort = new MemoryBookAdapter();
         LoanUseCase loanUseCase = new LoanService(bookPort);
 
         Book borrowed = loanUseCase.borrow("M1", "B1");
```
