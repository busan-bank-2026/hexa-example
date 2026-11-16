# use-case-port 코드 요약

## 테스트 작성
커밋: f9f289c
- 학생 코드 변경 없음

## T1 메뉴를 어댑터로 옮기기
커밋: 0be13e0

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/ConsoleMenu.java | 새로 |
| hexa/src/hexa/Main.java | 고침 |

```java
// hexa/src/hexa/ConsoleMenu.java
package hexa;

import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final LoanService loanService = new LoanService(scanner);

    public void run() {
        while (true) {
            System.out.println("1 대출  2 반납  0 종료");
            System.out.print("> ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                loanService.borrow();
            } else if (choice.equals("2")) {
                loanService.returnBook();
            } else if (choice.equals("0")) {
                System.out.println("프로그램을 끝냅니다");
                break;
            }
        }
    }
}
```

```diff
// hexa/src/hexa/Main.java
@@ -1,27 +1,9 @@
 package hexa;
 
-import java.util.Scanner;
-
 public class Main {
 
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
-        Scanner scanner = new Scanner(System.in);
-        LoanService loanService = new LoanService(scanner);
-
-        while (true) {
-            System.out.println("1 대출  2 반납  0 종료");
-            System.out.print("> ");
-            String choice = scanner.nextLine();
-
-            if (choice.equals("1")) {
-                loanService.borrow();
-            } else if (choice.equals("2")) {
-                loanService.returnBook();
-            } else if (choice.equals("0")) {
-                System.out.println("프로그램을 끝냅니다");
-                break;
-            }
-        }
+        new ConsoleMenu().run();
     }
 }
```

## T2 서비스에서 콘솔 떼어 내기
커밋: 20c13cc

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/ConsoleMenu.java | 고침 |
| hexa/src/hexa/LoanService.java | 고침 |
| hexa/src/hexa/Main.java | 고침 |

```diff
// hexa/src/hexa/ConsoleMenu.java
@@ -4,7 +4,11 @@ import java.util.Scanner;
 
 public class ConsoleMenu {
     private final Scanner scanner = new Scanner(System.in);
-    private final LoanService loanService = new LoanService(scanner);
+    private final LoanService loanService;
+
+    public ConsoleMenu(LoanService loanService) {
+        this.loanService = loanService;
+    }
 
     public void run() {
         while (true) {
@@ -13,9 +17,17 @@ public class ConsoleMenu {
             String choice = scanner.nextLine();
 
             if (choice.equals("1")) {
-                loanService.borrow();
+                System.out.print("회원 번호: ");
+                String memberId = scanner.nextLine();
+                System.out.print("책 번호: ");
+                String bookId = scanner.nextLine();
+                Book book = loanService.borrow(memberId, bookId);
+                System.out.println(book.getTitle() + " 대출했습니다");
             } else if (choice.equals("2")) {
-                loanService.returnBook();
+                System.out.print("책 번호: ");
+                String bookId = scanner.nextLine();
+                Book book = loanService.returnBook(bookId);
+                System.out.println(book.getTitle() + " 반납했습니다");
             } else if (choice.equals("0")) {
                 System.out.println("프로그램을 끝냅니다");
                 break;
```

```diff
// hexa/src/hexa/LoanService.java
@@ -1,30 +1,19 @@
 package hexa;
 
-import java.util.Scanner;
-
 public class LoanService {
-    private final Scanner scanner;
     private final BookRepository repository = new BookRepository();
 
-    public LoanService(Scanner scanner) {
-        this.scanner = scanner;
-    }
-
-    public void borrow() {
-        System.out.print("회원 번호: ");
-        String memberId = scanner.nextLine();
-        System.out.print("책 번호: ");
-        Book book = repository.findById(scanner.nextLine());
+    public Book borrow(String memberId, String bookId) {
+        Book book = repository.findById(bookId);
         book.borrow(memberId);
         repository.save(book);
-        System.out.println(book.getTitle() + " 대출했습니다");
+        return book;
     }
 
-    public void returnBook() {
-        System.out.print("책 번호: ");
-        Book book = repository.findById(scanner.nextLine());
+    public Book returnBook(String bookId) {
+        Book book = repository.findById(bookId);
         book.returnBook();
         repository.save(book);
-        System.out.println(book.getTitle() + " 반납했습니다");
+        return book;
     }
 }
```

```diff
// hexa/src/hexa/Main.java
@@ -4,6 +4,7 @@ public class Main {
 
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
-        new ConsoleMenu().run();
+        LoanService loanService = new LoanService();
+        new ConsoleMenu(loanService).run();
     }
 }
```

## T3 유스케이스 포트 열기
커밋: cdafde7

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/ConsoleMenu.java | 고침 |
| hexa/src/hexa/LoanService.java | 고침 |
| hexa/src/hexa/LoanUseCase.java | 새로 |
| hexa/src/hexa/Main.java | 고침 |

```diff
// hexa/src/hexa/ConsoleMenu.java
@@ -4,10 +4,10 @@ import java.util.Scanner;
 
 public class ConsoleMenu {
     private final Scanner scanner = new Scanner(System.in);
-    private final LoanService loanService;
+    private final LoanUseCase loanUseCase;
 
-    public ConsoleMenu(LoanService loanService) {
-        this.loanService = loanService;
+    public ConsoleMenu(LoanUseCase loanUseCase) {
+        this.loanUseCase = loanUseCase;
     }
 
     public void run() {
@@ -21,12 +21,12 @@ public class ConsoleMenu {
                 String memberId = scanner.nextLine();
                 System.out.print("책 번호: ");
                 String bookId = scanner.nextLine();
-                Book book = loanService.borrow(memberId, bookId);
+                Book book = loanUseCase.borrow(memberId, bookId);
                 System.out.println(book.getTitle() + " 대출했습니다");
             } else if (choice.equals("2")) {
                 System.out.print("책 번호: ");
                 String bookId = scanner.nextLine();
-                Book book = loanService.returnBook(bookId);
+                Book book = loanUseCase.returnBook(bookId);
                 System.out.println(book.getTitle() + " 반납했습니다");
             } else if (choice.equals("0")) {
                 System.out.println("프로그램을 끝냅니다");
```

```diff
// hexa/src/hexa/LoanService.java
@@ -1,8 +1,9 @@
 package hexa;
 
-public class LoanService {
+public class LoanService implements LoanUseCase {
     private final BookRepository repository = new BookRepository();
 
+    @Override
     public Book borrow(String memberId, String bookId) {
         Book book = repository.findById(bookId);
         book.borrow(memberId);
@@ -10,6 +11,7 @@ public class LoanService {
         return book;
     }
 
+    @Override
     public Book returnBook(String bookId) {
         Book book = repository.findById(bookId);
         book.returnBook();
```

```java
// hexa/src/hexa/LoanUseCase.java
package hexa;

public interface LoanUseCase {
    Book borrow(String memberId, String bookId);

    Book returnBook(String bookId);
}
```

```diff
// hexa/src/hexa/Main.java
@@ -4,7 +4,7 @@ public class Main {
 
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
-        LoanService loanService = new LoanService();
-        new ConsoleMenu(loanService).run();
+        LoanUseCase loanUseCase = new LoanService();
+        new ConsoleMenu(loanUseCase).run();
     }
 }
```

## T4 두 번째 입구 만들기
커밋: fddee46

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/LoanCheck.java | 새로 |

```java
// hexa/src/hexa/LoanCheck.java
package hexa;

public class LoanCheck {

    public static void main(String[] args) {
        LoanUseCase loanUseCase = new LoanService();

        Book borrowed = loanUseCase.borrow("M1", "B1");
        System.out.println("B1을 M1에게 대출: " + borrowed.getTitle());

        Book returned = loanUseCase.returnBook("B1");
        System.out.println("B1 반납: " + returned.getTitle());
    }
}
```

