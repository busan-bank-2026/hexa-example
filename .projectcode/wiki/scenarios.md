# 시나리오별 구조도

## 책 대출

```mermaid
flowchart LR
  A[콘솔 메뉴 1] --> B[ConsoleMenu]
  B --> C[LoanUseCase]
  C --> D[LoanService.borrow]
  D --> E[Book.borrow]
  D --> F[BookRepository]
```

만든 장: project-setup, layered-loan, use-case-port
맡는 파일
- hexa/src/hexa/Main.java
- hexa/src/hexa/ConsoleMenu.java
- hexa/src/hexa/LoanUseCase.java
- hexa/src/hexa/LoanService.java
- hexa/src/hexa/Book.java
- hexa/src/hexa/BookRepository.java

## 책 반납

```mermaid
flowchart LR
  A[콘솔 메뉴 2] --> B[ConsoleMenu]
  B --> C[LoanUseCase]
  C --> D[LoanService.returnBook]
  D --> E[Book.returnBook]
  D --> F[BookRepository]
```

만든 장: layered-loan, use-case-port
맡는 파일
- hexa/src/hexa/ConsoleMenu.java
- hexa/src/hexa/LoanUseCase.java
- hexa/src/hexa/LoanService.java
- hexa/src/hexa/Book.java
- hexa/src/hexa/BookRepository.java

## 두 번째 입구로 대출과 반납

```mermaid
flowchart LR
  A[LoanCheck] --> B[LoanUseCase]
  B --> C[LoanService]
  C --> D[Book]
  C --> E[BookRepository]
```

만든 장: use-case-port
맡는 파일
- hexa/src/hexa/LoanCheck.java
- hexa/src/hexa/LoanUseCase.java
- hexa/src/hexa/LoanService.java
