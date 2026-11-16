# due-date 코드 요약

## 테스트 작성
커밋: 3d73f34
- 학생 코드 변경 없음

## T1 반납 기한 정하고 보여 주기
커밋: af32bbd

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 |
| hexa/src/hexa/adapter/out/FileBookAdapter.java | 고침 |
| hexa/src/hexa/application/LoanService.java | 고침 |
| hexa/src/hexa/application/port/in/BookView.java | 고침 |
| hexa/src/hexa/domain/Book.java | 고침 |

```diff
// hexa/src/hexa/adapter/in/ConsoleMenu.java
@@ -28,6 +28,7 @@ public class ConsoleMenu {
                     String bookId = scanner.nextLine();
                     BookView book = loanUseCase.borrow(new BorrowCommand(memberId, bookId));
                     System.out.println(book.title() + " 대출했습니다");
+                    System.out.println("반납 기한: " + book.dueDate());
                 } else if (choice.equals("2")) {
                     System.out.print("책 번호: ");
                     String bookId = scanner.nextLine();
@@ -35,7 +36,13 @@ public class ConsoleMenu {
                     System.out.println(book.title() + " 반납했습니다");
                 } else if (choice.equals("3")) {
                     for (BookView book : loanUseCase.listBooks()) {
-                        String status = book.borrowed() ? "대출 중" : "대출 가능";
+                        String status = "대출 가능";
+                        if (book.borrowed()) {
+                            status = "대출 중 반납 기한 " + book.dueDate();
+                        }
+                        if (book.overdue()) {
+                            status = status + " 연체";
+                        }
                         System.out.println(book.id() + " " + book.title() + " " + status);
                     }
                 } else if (choice.equals("0")) {
```

```diff
// hexa/src/hexa/adapter/out/FileBookAdapter.java
@@ -4,6 +4,7 @@ import java.io.IOException;
 import java.io.UncheckedIOException;
 import java.nio.file.Files;
 import java.nio.file.Path;
+import java.time.LocalDate;
 import java.util.ArrayList;
 import java.util.LinkedHashMap;
 import java.util.List;
@@ -42,14 +43,15 @@ public class FileBookAdapter implements BookPort {
 
     private String toLine(Book book) {
         String borrowerId = book.getBorrowerId() == null ? "" : book.getBorrowerId();
-        return book.getId() + "," + book.getTitle() + "," + borrowerId;
+        String dueDate = book.getDueDate() == null ? "" : book.getDueDate().toString();
+        return book.getId() + "," + book.getTitle() + "," + borrowerId + "," + dueDate;
     }
 
     private Book toBook(String line) {
         String[] parts = line.split(",", -1);
         Book book = new Book(parts[0], parts[1]);
         if (!parts[2].isEmpty()) {
-            book.borrow(parts[2]);
+            book.borrow(parts[2], LocalDate.parse(parts[3]));
         }
         return book;
     }
```

```diff
// hexa/src/hexa/application/LoanService.java
@@ -1,5 +1,6 @@
 package hexa.application;
 
+import java.time.LocalDate;
 import java.util.ArrayList;
 import java.util.List;
 
@@ -19,7 +20,7 @@ public class LoanService implements LoanUseCase {
     @Override
     public BookView borrow(BorrowCommand command) {
         Book book = findBook(command.bookId());
-        book.borrow(command.memberId());
+        book.borrow(command.memberId(), LocalDate.now().plusDays(14));
         bookPort.save(book);
         return toView(book);
     }
@@ -50,6 +51,7 @@ public class LoanService implements LoanUseCase {
     }
 
     private BookView toView(Book book) {
-        return new BookView(book.getId(), book.getTitle(), book.getBorrowerId() != null);
+        return new BookView(book.getId(), book.getTitle(), book.getBorrowerId() != null, book.getDueDate(),
+                book.isOverdue(LocalDate.now()));
     }
 }
```

```diff
// hexa/src/hexa/application/port/in/BookView.java
@@ -1,4 +1,6 @@
 package hexa.application.port.in;
 
-public record BookView(String id, String title, boolean borrowed) {
+import java.time.LocalDate;
+
+public record BookView(String id, String title, boolean borrowed, LocalDate dueDate, boolean overdue) {
 }
```

```diff
// hexa/src/hexa/domain/Book.java
@@ -1,20 +1,24 @@
 package hexa.domain;
 
+import java.time.LocalDate;
+
 public class Book {
     private final String id;
     private final String title;
     private String borrowerId;
+    private LocalDate dueDate;
 
     public Book(String id, String title) {
         this.id = id;
         this.title = title;
     }
 
-    public void borrow(String memberId) {
+    public void borrow(String memberId, LocalDate dueDate) {
         if (borrowerId != null) {
             throw new IllegalStateException("이미 대출 중인 책입니다");
         }
         this.borrowerId = memberId;
+        this.dueDate = dueDate;
     }
 
     public void returnBook() {
@@ -22,6 +26,11 @@ public class Book {
             throw new IllegalStateException("대출 중이 아닌 책입니다");
         }
         this.borrowerId = null;
+        this.dueDate = null;
+    }
+
+    public boolean isOverdue(LocalDate today) {
+        return dueDate != null && today.isAfter(dueDate);
     }
 
     public String getId() {
@@ -35,4 +44,8 @@ public class Book {
     public String getBorrowerId() {
         return borrowerId;
     }
+
+    public LocalDate getDueDate() {
+        return dueDate;
+    }
 }
```

## T2 시계 포트로 오늘 갈아 끼우기
커밋: 05b4f32

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/LoanCheck.java | 고침 |
| hexa/src/hexa/Main.java | 고침 |
| hexa/src/hexa/adapter/out/FixedClockAdapter.java | 새로 |
| hexa/src/hexa/adapter/out/SystemClockAdapter.java | 새로 |
| hexa/src/hexa/application/LoanService.java | 고침 |
| hexa/src/hexa/application/port/out/ClockPort.java | 새로 |

```diff
// hexa/src/hexa/LoanCheck.java
@@ -1,5 +1,8 @@
 package hexa;
 
+import java.time.LocalDate;
+
+import hexa.adapter.out.FixedClockAdapter;
 import hexa.adapter.out.MemoryBookAdapter;
 import hexa.application.LoanService;
 import hexa.application.port.in.BookView;
@@ -11,12 +14,17 @@ public class LoanCheck {
 
     public static void main(String[] args) {
         BookPort bookPort = new MemoryBookAdapter();
-        LoanUseCase loanUseCase = new LoanService(bookPort);
+        LoanUseCase loanUseCase = new LoanService(bookPort, new FixedClockAdapter(LocalDate.of(2026, 3, 2)));
 
         BookView borrowed = loanUseCase.borrow(new BorrowCommand("M1", "B1"));
         System.out.println("B1을 M1에게 대출: " + borrowed.title());
+        System.out.println("반납 기한: " + borrowed.dueDate());
+
+        LoanUseCase later = new LoanService(bookPort, new FixedClockAdapter(LocalDate.of(2026, 3, 17)));
+        BookView late = later.listBooks().get(0);
+        System.out.println("15일 뒤 B1 연체: " + late.overdue());
 
-        BookView returned = loanUseCase.returnBook("B1");
+        BookView returned = later.returnBook("B1");
         System.out.println("B1 반납: " + returned.title());
     }
 }
```

```diff
// hexa/src/hexa/Main.java
@@ -2,6 +2,7 @@ package hexa;
 
 import hexa.adapter.in.ConsoleMenu;
 import hexa.adapter.out.FileBookAdapter;
+import hexa.adapter.out.SystemClockAdapter;
 import hexa.application.LoanService;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
@@ -11,7 +12,7 @@ public class Main {
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
         BookPort bookPort = new FileBookAdapter();
-        LoanUseCase loanUseCase = new LoanService(bookPort);
+        LoanUseCase loanUseCase = new LoanService(bookPort, new SystemClockAdapter());
         new ConsoleMenu(loanUseCase).run();
     }
 }
```

```java
// hexa/src/hexa/adapter/out/FixedClockAdapter.java
package hexa.adapter.out;

import java.time.LocalDate;

import hexa.application.port.out.ClockPort;

public class FixedClockAdapter implements ClockPort {
    private final LocalDate today;

    public FixedClockAdapter(LocalDate today) {
        this.today = today;
    }

    @Override
    public LocalDate today() {
        return today;
    }
}
```

```java
// hexa/src/hexa/adapter/out/SystemClockAdapter.java
package hexa.adapter.out;

import java.time.LocalDate;

import hexa.application.port.out.ClockPort;

public class SystemClockAdapter implements ClockPort {

    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}
```

```diff
// hexa/src/hexa/application/LoanService.java
@@ -1,6 +1,5 @@
 package hexa.application;
 
-import java.time.LocalDate;
 import java.util.ArrayList;
 import java.util.List;
 
@@ -8,19 +7,22 @@ import hexa.application.port.in.BookView;
 import hexa.application.port.in.BorrowCommand;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
+import hexa.application.port.out.ClockPort;
 import hexa.domain.Book;
 
 public class LoanService implements LoanUseCase {
     private final BookPort bookPort;
+    private final ClockPort clockPort;
 
-    public LoanService(BookPort bookPort) {
+    public LoanService(BookPort bookPort, ClockPort clockPort) {
         this.bookPort = bookPort;
+        this.clockPort = clockPort;
     }
 
     @Override
     public BookView borrow(BorrowCommand command) {
         Book book = findBook(command.bookId());
-        book.borrow(command.memberId(), LocalDate.now().plusDays(14));
+        book.borrow(command.memberId(), clockPort.today().plusDays(14));
         bookPort.save(book);
         return toView(book);
     }
@@ -52,6 +54,6 @@ public class LoanService implements LoanUseCase {
 
     private BookView toView(Book book) {
         return new BookView(book.getId(), book.getTitle(), book.getBorrowerId() != null, book.getDueDate(),
-                book.isOverdue(LocalDate.now()));
+                book.isOverdue(clockPort.today()));
     }
 }
```

```java
// hexa/src/hexa/application/port/out/ClockPort.java
package hexa.application.port.out;

import java.time.LocalDate;

public interface ClockPort {
    LocalDate today();
}
```

## 수렴 점검, 캡처, 도식
- 학생 코드 변경 없음
