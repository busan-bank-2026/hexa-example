# borrow-command 코드 요약

## 테스트 작성
커밋: f260739
- 학생 코드 변경 없음

## T1 대출 요청을 입력 모델로 받기
커밋: 6ac2d39

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/application/port/in/BorrowCommand.java | 새로 |
| hexa/src/hexa/application/port/in/LoanUseCase.java | 고침 |
| hexa/src/hexa/application/LoanService.java | 고침 |
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 |
| hexa/src/hexa/LoanCheck.java | 고침 |

```java
// hexa/src/hexa/application/port/in/BorrowCommand.java
package hexa.application.port.in;

public record BorrowCommand(String memberId, String bookId) {
}
```

```diff
// hexa/src/hexa/application/port/in/LoanUseCase.java
@@ -3,7 +3,7 @@ package hexa.application.port.in;
 import java.util.List;
 
 public interface LoanUseCase {
-    BookView borrow(String memberId, String bookId);
+    BookView borrow(BorrowCommand command);
 
     BookView returnBook(String bookId);
 
```

```diff
// hexa/src/hexa/application/LoanService.java
@@ -4,6 +4,7 @@ import java.util.ArrayList;
 import java.util.List;
 
 import hexa.application.port.in.BookView;
+import hexa.application.port.in.BorrowCommand;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
 import hexa.domain.Book;
@@ -16,9 +17,9 @@ public class LoanService implements LoanUseCase {
     }
 
     @Override
-    public BookView borrow(String memberId, String bookId) {
-        Book book = bookPort.findById(bookId);
-        book.borrow(memberId);
+    public BookView borrow(BorrowCommand command) {
+        Book book = bookPort.findById(command.bookId());
+        book.borrow(command.memberId());
         bookPort.save(book);
         return toView(book);
     }
```

```diff
// hexa/src/hexa/adapter/in/ConsoleMenu.java
@@ -3,6 +3,7 @@ package hexa.adapter.in;
 import java.util.Scanner;
 
 import hexa.application.port.in.BookView;
+import hexa.application.port.in.BorrowCommand;
 import hexa.application.port.in.LoanUseCase;
 
 public class ConsoleMenu {
@@ -24,7 +25,7 @@ public class ConsoleMenu {
                 String memberId = scanner.nextLine();
                 System.out.print("책 번호: ");
                 String bookId = scanner.nextLine();
-                BookView book = loanUseCase.borrow(memberId, bookId);
+                BookView book = loanUseCase.borrow(new BorrowCommand(memberId, bookId));
                 System.out.println(book.title() + " 대출했습니다");
             } else if (choice.equals("2")) {
                 System.out.print("책 번호: ");
```

```diff
// hexa/src/hexa/LoanCheck.java
@@ -3,6 +3,7 @@ package hexa;
 import hexa.adapter.out.MemoryBookAdapter;
 import hexa.application.LoanService;
 import hexa.application.port.in.BookView;
+import hexa.application.port.in.BorrowCommand;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
 
@@ -12,7 +13,7 @@ public class LoanCheck {
         BookPort bookPort = new MemoryBookAdapter();
         LoanUseCase loanUseCase = new LoanService(bookPort);
 
-        BookView borrowed = loanUseCase.borrow("M1", "B1");
+        BookView borrowed = loanUseCase.borrow(new BorrowCommand("M1", "B1"));
         System.out.println("B1을 M1에게 대출: " + borrowed.title());
 
         BookView returned = loanUseCase.returnBook("B1");
```

## T2 대출 요청이 빈 값을 막기
커밋: 0892c0b

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/application/port/in/BorrowCommand.java | 고침 |

```diff
// hexa/src/hexa/application/port/in/BorrowCommand.java
@@ -1,4 +1,12 @@
 package hexa.application.port.in;
 
 public record BorrowCommand(String memberId, String bookId) {
+    public BorrowCommand {
+        if (memberId.isBlank()) {
+            throw new IllegalArgumentException("회원 번호가 비었습니다");
+        }
+        if (bookId.isBlank()) {
+            throw new IllegalArgumentException("책 번호가 비었습니다");
+        }
+    }
 }
```
