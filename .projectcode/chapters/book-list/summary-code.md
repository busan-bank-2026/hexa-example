# book-list 코드 요약

## 테스트 작성
커밋: 0c27b0f
- 학생 코드 변경 없음

## T1 포트가 응답 모델을 돌려주게 하기
커밋: a03a986

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/application/port/in/BookView.java | 새로 |
| hexa/src/hexa/application/port/in/LoanUseCase.java | 고침 |
| hexa/src/hexa/application/LoanService.java | 고침 |
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 |
| hexa/src/hexa/LoanCheck.java | 고침 |

```java
// hexa/src/hexa/application/port/in/BookView.java
package hexa.application.port.in;

public record BookView(String id, String title, boolean borrowed) {
}
```

```diff
// hexa/src/hexa/application/port/in/LoanUseCase.java
@@ -1,9 +1,7 @@
 package hexa.application.port.in;
 
-import hexa.domain.Book;
-
 public interface LoanUseCase {
-    Book borrow(String memberId, String bookId);
+    BookView borrow(String memberId, String bookId);
 
-    Book returnBook(String bookId);
+    BookView returnBook(String bookId);
 }
```

```diff
// hexa/src/hexa/application/LoanService.java
@@ -1,5 +1,6 @@
 package hexa.application;
 
+import hexa.application.port.in.BookView;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
 import hexa.domain.Book;
@@ -12,18 +13,22 @@ public class LoanService implements LoanUseCase {
     }
 
     @Override
-    public Book borrow(String memberId, String bookId) {
+    public BookView borrow(String memberId, String bookId) {
         Book book = bookPort.findById(bookId);
         book.borrow(memberId);
         bookPort.save(book);
-        return book;
+        return toView(book);
     }
 
     @Override
-    public Book returnBook(String bookId) {
+    public BookView returnBook(String bookId) {
         Book book = bookPort.findById(bookId);
         book.returnBook();
         bookPort.save(book);
-        return book;
+        return toView(book);
+    }
+
+    private BookView toView(Book book) {
+        return new BookView(book.getId(), book.getTitle(), book.getBorrowerId() != null);
     }
 }
```

```diff
// hexa/src/hexa/adapter/in/ConsoleMenu.java
@@ -2,8 +2,8 @@ package hexa.adapter.in;
 
 import java.util.Scanner;
 
+import hexa.application.port.in.BookView;
 import hexa.application.port.in.LoanUseCase;
-import hexa.domain.Book;
 
 public class ConsoleMenu {
     private final Scanner scanner = new Scanner(System.in);
@@ -24,13 +24,13 @@ public class ConsoleMenu {
                 String memberId = scanner.nextLine();
                 System.out.print("책 번호: ");
                 String bookId = scanner.nextLine();
-                Book book = loanUseCase.borrow(memberId, bookId);
-                System.out.println(book.getTitle() + " 대출했습니다");
+                BookView book = loanUseCase.borrow(memberId, bookId);
+                System.out.println(book.title() + " 대출했습니다");
             } else if (choice.equals("2")) {
                 System.out.print("책 번호: ");
                 String bookId = scanner.nextLine();
-                Book book = loanUseCase.returnBook(bookId);
-                System.out.println(book.getTitle() + " 반납했습니다");
+                BookView book = loanUseCase.returnBook(bookId);
+                System.out.println(book.title() + " 반납했습니다");
             } else if (choice.equals("0")) {
                 System.out.println("프로그램을 끝냅니다");
                 break;
```

```diff
// hexa/src/hexa/LoanCheck.java
@@ -2,9 +2,9 @@ package hexa;
 
 import hexa.adapter.out.MemoryBookAdapter;
 import hexa.application.LoanService;
+import hexa.application.port.in.BookView;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
-import hexa.domain.Book;
 
 public class LoanCheck {
 
@@ -12,10 +12,10 @@ public class LoanCheck {
         BookPort bookPort = new MemoryBookAdapter();
         LoanUseCase loanUseCase = new LoanService(bookPort);
 
-        Book borrowed = loanUseCase.borrow("M1", "B1");
-        System.out.println("B1을 M1에게 대출: " + borrowed.getTitle());
+        BookView borrowed = loanUseCase.borrow("M1", "B1");
+        System.out.println("B1을 M1에게 대출: " + borrowed.title());
 
-        Book returned = loanUseCase.returnBook("B1");
-        System.out.println("B1 반납: " + returned.getTitle());
+        BookView returned = loanUseCase.returnBook("B1");
+        System.out.println("B1 반납: " + returned.title());
     }
 }
```

## T2 조회 유스케이스로 책 목록 보기
커밋: 7a08b68

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/application/port/out/BookPort.java | 고침 |
| hexa/src/hexa/adapter/out/MemoryBookAdapter.java | 고침 |
| hexa/src/hexa/adapter/out/FileBookAdapter.java | 고침 |
| hexa/src/hexa/application/port/in/LoanUseCase.java | 고침 |
| hexa/src/hexa/application/LoanService.java | 고침 |
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 |

```diff
// hexa/src/hexa/application/port/out/BookPort.java
@@ -1,9 +1,13 @@
 package hexa.application.port.out;
 
+import java.util.List;
+
 import hexa.domain.Book;
 
 public interface BookPort {
     Book findById(String id);
 
+    List<Book> findAll();
+
     void save(Book book);
 }
```

```diff
// hexa/src/hexa/adapter/out/MemoryBookAdapter.java
@@ -1,13 +1,15 @@
 package hexa.adapter.out;
 
-import java.util.HashMap;
+import java.util.ArrayList;
+import java.util.LinkedHashMap;
+import java.util.List;
 import java.util.Map;
 
 import hexa.application.port.out.BookPort;
 import hexa.domain.Book;
 
 public class MemoryBookAdapter implements BookPort {
-    private final Map<String, Book> books = new HashMap<>();
+    private final Map<String, Book> books = new LinkedHashMap<>();
 
     public MemoryBookAdapter() {
         save(new Book("B1", "객체지향의 사실과 오해"));
@@ -20,6 +22,11 @@ public class MemoryBookAdapter implements BookPort {
         return books.get(id);
     }
 
+    @Override
+    public List<Book> findAll() {
+        return new ArrayList<>(books.values());
+    }
+
     @Override
     public void save(Book book) {
         books.put(book.getId(), book);
```

```diff
// hexa/src/hexa/adapter/out/FileBookAdapter.java
@@ -28,6 +28,11 @@ public class FileBookAdapter implements BookPort {
         return readAll().get(id);
     }
 
+    @Override
+    public List<Book> findAll() {
+        return new ArrayList<>(readAll().values());
+    }
+
     @Override
     public void save(Book book) {
         Map<String, Book> books = readAll();
```

```diff
// hexa/src/hexa/application/port/in/LoanUseCase.java
@@ -1,7 +1,11 @@
 package hexa.application.port.in;
 
+import java.util.List;
+
 public interface LoanUseCase {
     BookView borrow(String memberId, String bookId);
 
     BookView returnBook(String bookId);
+
+    List<BookView> listBooks();
 }
```

```diff
// hexa/src/hexa/application/LoanService.java
@@ -1,5 +1,8 @@
 package hexa.application;
 
+import java.util.ArrayList;
+import java.util.List;
+
 import hexa.application.port.in.BookView;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
@@ -28,6 +31,15 @@ public class LoanService implements LoanUseCase {
         return toView(book);
     }
 
+    @Override
+    public List<BookView> listBooks() {
+        List<BookView> views = new ArrayList<>();
+        for (Book book : bookPort.findAll()) {
+            views.add(toView(book));
+        }
+        return views;
+    }
+
     private BookView toView(Book book) {
         return new BookView(book.getId(), book.getTitle(), book.getBorrowerId() != null);
     }
```

```diff
// hexa/src/hexa/adapter/in/ConsoleMenu.java
@@ -15,7 +15,7 @@ public class ConsoleMenu {
 
     public void run() {
         while (true) {
-            System.out.println("1 대출  2 반납  0 종료");
+            System.out.println("1 대출  2 반납  3 목록  0 종료");
             System.out.print("> ");
             String choice = scanner.nextLine();
 
@@ -31,6 +31,11 @@ public class ConsoleMenu {
                 String bookId = scanner.nextLine();
                 BookView book = loanUseCase.returnBook(bookId);
                 System.out.println(book.title() + " 반납했습니다");
+            } else if (choice.equals("3")) {
+                for (BookView book : loanUseCase.listBooks()) {
+                    String status = book.borrowed() ? "대출 중" : "대출 가능";
+                    System.out.println(book.id() + " " + book.title() + " " + status);
+                }
             } else if (choice.equals("0")) {
                 System.out.println("프로그램을 끝냅니다");
                 break;
```
