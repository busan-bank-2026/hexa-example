# layered-loan 코드 요약

## 테스트 작성
커밋: e22730b
- 학생 코드 변경 없음

## T1 메뉴 보여주기
커밋: 8be97da

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/Main.java | 고침 |

```diff
// hexa/src/hexa/Main.java
@@ -1,8 +1,22 @@
 package hexa;
 
+import java.util.Scanner;
+
 public class Main {
 
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
+        Scanner scanner = new Scanner(System.in);
+
+        while (true) {
+            System.out.println("1 대출  2 반납  0 종료");
+            System.out.print("> ");
+            String choice = scanner.nextLine();
+
+            if (choice.equals("0")) {
+                System.out.println("프로그램을 끝냅니다");
+                break;
+            }
+        }
     }
 }
```

## T2 책 대출하기
커밋: 4fdc7bc

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/Book.java | 새로 |
| hexa/src/hexa/BookRepository.java | 새로 |
| hexa/src/hexa/LoanService.java | 새로 |
| hexa/src/hexa/Main.java | 고침 |

```java
// hexa/src/hexa/Book.java
package hexa;

public class Book {
    private final String id;
    private final String title;
    private String borrowerId;

    public Book(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public void borrow(String memberId) {
        this.borrowerId = memberId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
```

```java
// hexa/src/hexa/BookRepository.java
package hexa;

import java.util.HashMap;
import java.util.Map;

public class BookRepository {
    private final Map<String, Book> books = new HashMap<>();

    public BookRepository() {
        save(new Book("B1", "객체지향의 사실과 오해"));
        save(new Book("B2", "클린 아키텍처"));
        save(new Book("B3", "도메인 주도 설계 첫걸음"));
    }

    public Book findById(String id) {
        return books.get(id);
    }

    public void save(Book book) {
        books.put(book.getId(), book);
    }
}
```

```java
// hexa/src/hexa/LoanService.java
package hexa;

import java.util.Scanner;

public class LoanService {
    private final Scanner scanner;
    private final BookRepository repository = new BookRepository();

    public LoanService(Scanner scanner) {
        this.scanner = scanner;
    }

    public void borrow() {
        System.out.print("회원 번호: ");
        String memberId = scanner.nextLine();
        System.out.print("책 번호: ");
        Book book = repository.findById(scanner.nextLine());
        book.borrow(memberId);
        repository.save(book);
        System.out.println(book.getTitle() + " 대출했습니다");
    }
}
```

```diff
// hexa/src/hexa/Main.java
@@ -7,13 +7,16 @@ public class Main {
     public static void main(String[] args) {
         System.out.println("도서 대출 프로그램을 시작합니다");
         Scanner scanner = new Scanner(System.in);
+        LoanService loanService = new LoanService(scanner);
 
         while (true) {
             System.out.println("1 대출  2 반납  0 종료");
             System.out.print("> ");
             String choice = scanner.nextLine();
 
-            if (choice.equals("0")) {
+            if (choice.equals("1")) {
+                loanService.borrow();
+            } else if (choice.equals("0")) {
                 System.out.println("프로그램을 끝냅니다");
                 break;
             }
```

## T3 대출 중인 책 거절하기
커밋: c1582da

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/Book.java | 고침 |

```diff
// hexa/src/hexa/Book.java
@@ -11,6 +11,9 @@ public class Book {
     }
 
     public void borrow(String memberId) {
+        if (borrowerId != null) {
+            throw new IllegalStateException("이미 대출 중인 책입니다");
+        }
         this.borrowerId = memberId;
     }
 
```

## T4 책 반납하기
커밋: da53826

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/Book.java | 고침 |
| hexa/src/hexa/LoanService.java | 고침 |
| hexa/src/hexa/Main.java | 고침 |

```diff
// hexa/src/hexa/Book.java
@@ -17,6 +17,10 @@ public class Book {
         this.borrowerId = memberId;
     }
 
+    public void returnBook() {
+        this.borrowerId = null;
+    }
+
     public String getId() {
         return id;
     }
```

```diff
// hexa/src/hexa/LoanService.java
@@ -19,4 +19,12 @@ public class LoanService {
         repository.save(book);
         System.out.println(book.getTitle() + " 대출했습니다");
     }
+
+    public void returnBook() {
+        System.out.print("책 번호: ");
+        Book book = repository.findById(scanner.nextLine());
+        book.returnBook();
+        repository.save(book);
+        System.out.println(book.getTitle() + " 반납했습니다");
+    }
 }
```

```diff
// hexa/src/hexa/Main.java
@@ -16,6 +16,8 @@ public class Main {
 
             if (choice.equals("1")) {
                 loanService.borrow();
+            } else if (choice.equals("2")) {
+                loanService.returnBook();
             } else if (choice.equals("0")) {
                 System.out.println("프로그램을 끝냅니다");
                 break;
```

