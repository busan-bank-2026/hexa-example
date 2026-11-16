# 시나리오별 구조도

## 책 대출

```mermaid
flowchart LR
  A[콘솔 메뉴 1] --> B[Main]
  B --> C[LoanService.borrow]
  C --> D[Book.borrow]
  C --> E[BookRepository]
```

만든 장: project-setup, layered-loan
맡는 파일
- hexa/src/hexa/Main.java
- hexa/src/hexa/LoanService.java
- hexa/src/hexa/Book.java
- hexa/src/hexa/BookRepository.java

## 책 반납

```mermaid
flowchart LR
  A[콘솔 메뉴 2] --> B[Main]
  B --> C[LoanService.returnBook]
  C --> D[Book.returnBook]
  C --> E[BookRepository]
```

만든 장: layered-loan
맡는 파일
- hexa/src/hexa/Main.java
- hexa/src/hexa/LoanService.java
- hexa/src/hexa/Book.java
- hexa/src/hexa/BookRepository.java
