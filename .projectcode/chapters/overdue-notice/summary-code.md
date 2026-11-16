# overdue-notice 코드 요약

## 테스트 작성
커밋: db0fbd3
- 학생 코드 변경 없음

## 테스트 보강
커밋: deb8147
- 학생 코드 변경 없음

## T1 알림 포트로 연체 알림 보내기
커밋: 7ec3140

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/LoanCheck.java | 고침 |
| hexa/src/hexa/Main.java | 고침 |
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 |
| hexa/src/hexa/adapter/out/ConsoleNotifyAdapter.java | 새로 |
| hexa/src/hexa/application/LoanService.java | 고침 |
| hexa/src/hexa/application/port/in/LoanUseCase.java | 고침 |
| hexa/src/hexa/application/port/out/NotifyPort.java | 새로 |

```diff
// hexa/src/hexa/LoanCheck.java
@@ -2,6 +2,7 @@ package hexa;
 
 import java.time.LocalDate;
 
+import hexa.adapter.out.ConsoleNotifyAdapter;
 import hexa.adapter.out.FixedClockAdapter;
 import hexa.adapter.out.MemoryBookAdapter;
 import hexa.application.LoanService;
@@ -9,21 +10,26 @@ import hexa.application.port.in.BookView;
 import hexa.application.port.in.BorrowCommand;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
+import hexa.application.port.out.NotifyPort;
 
 public class LoanCheck {
 
     public static void main(String[] args) {
         BookPort bookPort = new MemoryBookAdapter();
-        LoanUseCase loanUseCase = new LoanService(bookPort, new FixedClockAdapter(LocalDate.of(2026, 3, 2)));
+        NotifyPort notifyPort = new ConsoleNotifyAdapter();
+        LoanUseCase loanUseCase = new LoanService(bookPort, new FixedClockAdapter(LocalDate.of(2026, 3, 2)), notifyPort);
 
         BookView borrowed = loanUseCase.borrow(new BorrowCommand("M1", "B1"));
         System.out.println("B1을 M1에게 대출: " + borrowed.title());
         System.out.println("반납 기한: " + borrowed.dueDate());
 
-        LoanUseCase later = new LoanService(bookPort, new FixedClockAdapter(LocalDate.of(2026, 3, 17)));
+        LoanUseCase later = new LoanService(bookPort, new FixedClockAdapter(LocalDate.of(2026, 3, 17)), notifyPort);
         BookView late = later.listBooks().get(0);
         System.out.println("15일 뒤 B1 연체: " + late.overdue());
 
+        int count = later.notifyOverdue();
+        System.out.println("연체 알림: " + count + "건");
+
         BookView returned = later.returnBook("B1");
         System.out.println("B1 반납: " + returned.title());
     }
```

```diff
// hexa/src/hexa/Main.java
@@ -1,6 +1,7 @@
 package hexa;
 
 import hexa.adapter.in.ConsoleMenu;
+import hexa.adapter.out.ConsoleNotifyAdapter;
 import hexa.adapter.out.FileBookAdapter;
 import hexa.adapter.out.SystemClockAdapter;
 import hexa.application.LoanService;
@@ -12,7 +13,7 @@ public class Main {
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
         BookPort bookPort = new FileBookAdapter();
-        LoanUseCase loanUseCase = new LoanService(bookPort, new SystemClockAdapter());
+        LoanUseCase loanUseCase = new LoanService(bookPort, new SystemClockAdapter(), new ConsoleNotifyAdapter());
         new ConsoleMenu(loanUseCase).run();
     }
 }
```

```diff
// hexa/src/hexa/adapter/in/ConsoleMenu.java
@@ -16,7 +16,7 @@ public class ConsoleMenu {
 
     public void run() {
         while (true) {
-            System.out.println("1 대출  2 반납  3 목록  0 종료");
+            System.out.println("1 대출  2 반납  3 목록  4 연체 알림  0 종료");
             System.out.print("> ");
             String choice = scanner.nextLine();
 
@@ -45,6 +45,9 @@ public class ConsoleMenu {
                         }
                         System.out.println(book.id() + " " + book.title() + " " + status);
                     }
+                } else if (choice.equals("4")) {
+                    int count = loanUseCase.notifyOverdue();
+                    System.out.println("연체 알림 " + count + "건을 보냈습니다");
                 } else if (choice.equals("0")) {
                     System.out.println("프로그램을 끝냅니다");
                     break;
```

```java
// hexa/src/hexa/adapter/out/ConsoleNotifyAdapter.java
package hexa.adapter.out;

import hexa.application.port.out.NotifyPort;

public class ConsoleNotifyAdapter implements NotifyPort {

    @Override
    public void send(String memberId, String message) {
        System.out.println(memberId + "에게 알림: " + message);
    }
}
```

```diff
// hexa/src/hexa/application/LoanService.java
@@ -8,15 +8,18 @@ import hexa.application.port.in.BorrowCommand;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
 import hexa.application.port.out.ClockPort;
+import hexa.application.port.out.NotifyPort;
 import hexa.domain.Book;
 
 public class LoanService implements LoanUseCase {
     private final BookPort bookPort;
     private final ClockPort clockPort;
+    private final NotifyPort notifyPort;
 
-    public LoanService(BookPort bookPort, ClockPort clockPort) {
+    public LoanService(BookPort bookPort, ClockPort clockPort, NotifyPort notifyPort) {
         this.bookPort = bookPort;
         this.clockPort = clockPort;
+        this.notifyPort = notifyPort;
     }
 
     @Override
@@ -44,6 +47,18 @@ public class LoanService implements LoanUseCase {
         return views;
     }
 
+    @Override
+    public int notifyOverdue() {
+        int count = 0;
+        for (Book book : bookPort.findAll()) {
+            if (book.isOverdue(clockPort.today())) {
+                notifyPort.send(book.getBorrowerId(), book.getTitle() + " 반납 기한 " + book.getDueDate() + "이 지났습니다");
+                count++;
+            }
+        }
+        return count;
+    }
+
     private Book findBook(String bookId) {
         Book book = bookPort.findById(bookId);
         if (book == null) {
```

```diff
// hexa/src/hexa/application/port/in/LoanUseCase.java
@@ -8,4 +8,6 @@ public interface LoanUseCase {
     BookView returnBook(String bookId);
 
     List<BookView> listBooks();
+
+    int notifyOverdue();
 }
```

```java
// hexa/src/hexa/application/port/out/NotifyPort.java
package hexa.application.port.out;

public interface NotifyPort {
    void send(String memberId, String message);
}
```

## T2 파일 알림 어댑터 하나 더 꽂기
커밋: 8c7c984

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/Main.java | 고침 |
| hexa/src/hexa/adapter/out/FileNotifyAdapter.java | 새로 |

```diff
// hexa/src/hexa/Main.java
@@ -1,8 +1,8 @@
 package hexa;
 
 import hexa.adapter.in.ConsoleMenu;
-import hexa.adapter.out.ConsoleNotifyAdapter;
 import hexa.adapter.out.FileBookAdapter;
+import hexa.adapter.out.FileNotifyAdapter;
 import hexa.adapter.out.SystemClockAdapter;
 import hexa.application.LoanService;
 import hexa.application.port.in.LoanUseCase;
@@ -13,7 +13,7 @@ public class Main {
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
         BookPort bookPort = new FileBookAdapter();
-        LoanUseCase loanUseCase = new LoanService(bookPort, new SystemClockAdapter(), new ConsoleNotifyAdapter());
+        LoanUseCase loanUseCase = new LoanService(bookPort, new SystemClockAdapter(), new FileNotifyAdapter());
         new ConsoleMenu(loanUseCase).run();
     }
 }
```

```java
// hexa/src/hexa/adapter/out/FileNotifyAdapter.java
package hexa.adapter.out;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import hexa.application.port.out.NotifyPort;

public class FileNotifyAdapter implements NotifyPort {
    private final Path file = Path.of("data/notices.txt");

    @Override
    public void send(String memberId, String message) {
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, List.of(memberId + "," + message), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
```
