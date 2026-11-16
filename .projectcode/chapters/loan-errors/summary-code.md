# loan-errors 코드 요약

## 테스트 작성
커밋: 694fbaf
- 학생 코드 변경 없음

## T1 콘솔 메뉴가 예외를 메시지로 바꾸기
커밋: fe9e9d2

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 |

```diff
// hexa/src/hexa/adapter/in/ConsoleMenu.java
@@ -20,26 +20,30 @@ public class ConsoleMenu {
             System.out.print("> ");
             String choice = scanner.nextLine();
 
-            if (choice.equals("1")) {
-                System.out.print("회원 번호: ");
-                String memberId = scanner.nextLine();
-                System.out.print("책 번호: ");
-                String bookId = scanner.nextLine();
-                BookView book = loanUseCase.borrow(new BorrowCommand(memberId, bookId));
-                System.out.println(book.title() + " 대출했습니다");
-            } else if (choice.equals("2")) {
-                System.out.print("책 번호: ");
-                String bookId = scanner.nextLine();
-                BookView book = loanUseCase.returnBook(bookId);
-                System.out.println(book.title() + " 반납했습니다");
-            } else if (choice.equals("3")) {
-                for (BookView book : loanUseCase.listBooks()) {
-                    String status = book.borrowed() ? "대출 중" : "대출 가능";
-                    System.out.println(book.id() + " " + book.title() + " " + status);
+            try {
+                if (choice.equals("1")) {
+                    System.out.print("회원 번호: ");
+                    String memberId = scanner.nextLine();
+                    System.out.print("책 번호: ");
+                    String bookId = scanner.nextLine();
+                    BookView book = loanUseCase.borrow(new BorrowCommand(memberId, bookId));
+                    System.out.println(book.title() + " 대출했습니다");
+                } else if (choice.equals("2")) {
+                    System.out.print("책 번호: ");
+                    String bookId = scanner.nextLine();
+                    BookView book = loanUseCase.returnBook(bookId);
+                    System.out.println(book.title() + " 반납했습니다");
+                } else if (choice.equals("3")) {
+                    for (BookView book : loanUseCase.listBooks()) {
+                        String status = book.borrowed() ? "대출 중" : "대출 가능";
+                        System.out.println(book.id() + " " + book.title() + " " + status);
+                    }
+                } else if (choice.equals("0")) {
+                    System.out.println("프로그램을 끝냅니다");
+                    break;
                 }
-            } else if (choice.equals("0")) {
-                System.out.println("프로그램을 끝냅니다");
-                break;
+            } catch (IllegalArgumentException | IllegalStateException e) {
+                System.out.println(e.getMessage());
             }
         }
     }
```

## T2 없는 책 번호를 오류로 알리기
커밋: c92dff8

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/application/LoanService.java | 고침 |

```diff
// hexa/src/hexa/application/LoanService.java
@@ -18,7 +18,7 @@ public class LoanService implements LoanUseCase {
 
     @Override
     public BookView borrow(BorrowCommand command) {
-        Book book = bookPort.findById(command.bookId());
+        Book book = findBook(command.bookId());
         book.borrow(command.memberId());
         bookPort.save(book);
         return toView(book);
@@ -26,7 +26,7 @@ public class LoanService implements LoanUseCase {
 
     @Override
     public BookView returnBook(String bookId) {
-        Book book = bookPort.findById(bookId);
+        Book book = findBook(bookId);
         book.returnBook();
         bookPort.save(book);
         return toView(book);
@@ -41,6 +41,14 @@ public class LoanService implements LoanUseCase {
         return views;
     }
 
+    private Book findBook(String bookId) {
+        Book book = bookPort.findById(bookId);
+        if (book == null) {
+            throw new IllegalArgumentException("책을 찾을 수 없습니다");
+        }
+        return book;
+    }
+
     private BookView toView(Book book) {
         return new BookView(book.getId(), book.getTitle(), book.getBorrowerId() != null);
     }
```

## T3 대출 중이 아닌 책 반납 거절하기
커밋: 86cded1

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/domain/Book.java | 고침 |

```diff
// hexa/src/hexa/domain/Book.java
@@ -18,6 +18,9 @@ public class Book {
     }
 
     public void returnBook() {
+        if (borrowerId == null) {
+            throw new IllegalStateException("대출 중이 아닌 책입니다");
+        }
         this.borrowerId = null;
     }
 
```
