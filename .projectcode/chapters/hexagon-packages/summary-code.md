# hexagon-packages 코드 요약

## 테스트 작성
커밋: a21171c
- 학생 코드 변경 없음

## T1 책을 domain 패키지로 옮기기
커밋: 823159f

- 옮긴 파일은 내용이 같고 package 줄과 import 줄만 바뀐다

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/BookPort.java | 고침 |
| hexa/src/hexa/ConsoleMenu.java | 고침 |
| hexa/src/hexa/LoanCheck.java | 고침 |
| hexa/src/hexa/LoanService.java | 고침 |
| hexa/src/hexa/LoanUseCase.java | 고침 |
| hexa/src/hexa/MemoryBookAdapter.java | 고침 |
| hexa/src/hexa/domain/Book.java | 옮김. hexa/src/hexa/Book.java에서 |

```diff
// hexa/src/hexa/BookPort.java
@@ -1,5 +1,7 @@
 package hexa;
 
+import hexa.domain.Book;
+
 public interface BookPort {
     Book findById(String id);
 
```

```diff
// hexa/src/hexa/ConsoleMenu.java
@@ -2,6 +2,8 @@ package hexa;
 
 import java.util.Scanner;
 
+import hexa.domain.Book;
+
 public class ConsoleMenu {
     private final Scanner scanner = new Scanner(System.in);
     private final LoanUseCase loanUseCase;
```

```diff
// hexa/src/hexa/LoanCheck.java
@@ -1,5 +1,7 @@
 package hexa;
 
+import hexa.domain.Book;
+
 public class LoanCheck {
 
     public static void main(String[] args) {
```

```diff
// hexa/src/hexa/LoanService.java
@@ -1,5 +1,7 @@
 package hexa;
 
+import hexa.domain.Book;
+
 public class LoanService implements LoanUseCase {
     private final BookPort bookPort;
 
```

```diff
// hexa/src/hexa/LoanUseCase.java
@@ -1,5 +1,7 @@
 package hexa;
 
+import hexa.domain.Book;
+
 public interface LoanUseCase {
     Book borrow(String memberId, String bookId);
 
```

```diff
// hexa/src/hexa/MemoryBookAdapter.java
@@ -3,6 +3,8 @@ package hexa;
 import java.util.HashMap;
 import java.util.Map;
 
+import hexa.domain.Book;
+
 public class MemoryBookAdapter implements BookPort {
     private final Map<String, Book> books = new HashMap<>();
 
```

```diff
// hexa/src/hexa/domain/Book.java
@@ -1,4 +1,4 @@
-package hexa;
+package hexa.domain;
 
 public class Book {
     private final String id;
```

## T2 서비스와 포트를 application 패키지로 옮기기
커밋: bdea041

- 옮긴 파일은 내용이 같고 package 줄과 import 줄만 바뀐다

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/ConsoleMenu.java | 고침 |
| hexa/src/hexa/LoanCheck.java | 고침 |
| hexa/src/hexa/Main.java | 고침 |
| hexa/src/hexa/MemoryBookAdapter.java | 고침 |
| hexa/src/hexa/application/LoanService.java | 옮김. hexa/src/hexa/LoanService.java에서 |
| hexa/src/hexa/application/port/in/LoanUseCase.java | 옮김. hexa/src/hexa/LoanUseCase.java에서 |
| hexa/src/hexa/application/port/out/BookPort.java | 옮김. hexa/src/hexa/BookPort.java에서 |

```diff
// hexa/src/hexa/ConsoleMenu.java
@@ -2,6 +2,7 @@ package hexa;
 
 import java.util.Scanner;
 
+import hexa.application.port.in.LoanUseCase;
 import hexa.domain.Book;
 
 public class ConsoleMenu {
```

```diff
// hexa/src/hexa/LoanCheck.java
@@ -1,5 +1,8 @@
 package hexa;
 
+import hexa.application.LoanService;
+import hexa.application.port.in.LoanUseCase;
+import hexa.application.port.out.BookPort;
 import hexa.domain.Book;
 
 public class LoanCheck {
```

```diff
// hexa/src/hexa/Main.java
@@ -1,5 +1,9 @@
 package hexa;
 
+import hexa.application.LoanService;
+import hexa.application.port.in.LoanUseCase;
+import hexa.application.port.out.BookPort;
+
 public class Main {
 
     public static void main(String[] args) {
```

```diff
// hexa/src/hexa/MemoryBookAdapter.java
@@ -3,6 +3,7 @@ package hexa;
 import java.util.HashMap;
 import java.util.Map;
 
+import hexa.application.port.out.BookPort;
 import hexa.domain.Book;
 
 public class MemoryBookAdapter implements BookPort {
```

```diff
// hexa/src/hexa/application/LoanService.java
@@ -1,5 +1,7 @@
-package hexa;
+package hexa.application;
 
+import hexa.application.port.in.LoanUseCase;
+import hexa.application.port.out.BookPort;
 import hexa.domain.Book;
 
 public class LoanService implements LoanUseCase {
```

```diff
// hexa/src/hexa/application/port/in/LoanUseCase.java
@@ -1,4 +1,4 @@
-package hexa;
+package hexa.application.port.in;
 
 import hexa.domain.Book;
 
```

```diff
// hexa/src/hexa/application/port/out/BookPort.java
@@ -1,4 +1,4 @@
-package hexa;
+package hexa.application.port.out;
 
 import hexa.domain.Book;
 
```

## T3 어댑터를 adapter 패키지로 옮기기
커밋: df26ed4

- 옮긴 파일은 내용이 같고 package 줄과 import 줄만 바뀐다

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/LoanCheck.java | 고침 |
| hexa/src/hexa/Main.java | 고침 |
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 옮김. hexa/src/hexa/ConsoleMenu.java에서 |
| hexa/src/hexa/adapter/out/MemoryBookAdapter.java | 옮김. hexa/src/hexa/MemoryBookAdapter.java에서 |

```diff
// hexa/src/hexa/LoanCheck.java
@@ -1,5 +1,6 @@
 package hexa;
 
+import hexa.adapter.out.MemoryBookAdapter;
 import hexa.application.LoanService;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
```

```diff
// hexa/src/hexa/Main.java
@@ -1,5 +1,7 @@
 package hexa;
 
+import hexa.adapter.in.ConsoleMenu;
+import hexa.adapter.out.MemoryBookAdapter;
 import hexa.application.LoanService;
 import hexa.application.port.in.LoanUseCase;
 import hexa.application.port.out.BookPort;
```

```diff
// hexa/src/hexa/adapter/in/ConsoleMenu.java
@@ -1,4 +1,4 @@
-package hexa;
+package hexa.adapter.in;
 
 import java.util.Scanner;
 
```

```diff
// hexa/src/hexa/adapter/out/MemoryBookAdapter.java
@@ -1,4 +1,4 @@
-package hexa;
+package hexa.adapter.out;
 
 import java.util.HashMap;
 import java.util.Map;
```

