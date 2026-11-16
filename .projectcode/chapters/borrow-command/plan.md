# borrow-command 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/application/port/in/BorrowCommand.java | 새로 | 입력 모델. 회원 번호와 책 번호를 담는 record. 만들 때 빈 값을 막는다 |
| hexa/src/hexa/application/port/in/LoanUseCase.java | 고침 | borrow가 BorrowCommand를 받는다 |
| hexa/src/hexa/application/LoanService.java | 고침 | BorrowCommand에서 책 번호와 회원 번호를 꺼내 대출한다 |
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 | 읽은 두 값으로 BorrowCommand를 만들어 borrow를 부른다 |
| hexa/src/hexa/LoanCheck.java | 고침 | BorrowCommand를 만들어 borrow를 부른다 |
| tests/hexa/BorrowCommandTest.java | 새로 | S1, S2, S3 검증용 행동 테스트 |
| tests/hexa/Find.java | 고침 | 대출을 부르는 도우미 borrowMethod, borrow를 더한다 |
| tests/hexa/UseCasePortTest.java | 고침 | 대출을 Find.borrow로 부른다 |
| tests/hexa/BookListTest.java | 고침 | 대출을 Find.borrowMethod, Find.borrow로 부른다 |

## 코드 계획

나누는 이유
- BorrowCommand: 포트 안으로 들어오는 입력 모델이다. 대출에 필요한 값을 한 묶음으로 받고 묶음을 만들 때 형식을 검사한다
- BorrowCommand는 들어오는 포트와 같은 port.in에 둔다. BookView처럼 포트의 메서드 모양의 일부라서다
- 형식 검사는 BorrowCommand의 compact 생성자에 둔다. 어느 어댑터가 만들든 같은 검사를 지나간다
- 업무 규칙 검사(이미 대출 중)는 Book.borrow에 그대로 둔다. 책의 지금 상태를 알아야 해서다
- 반납은 String bookId 그대로 둔다. 값이 하나라 묶을 것이 없다

BorrowCommand.java
- public record BorrowCommand(String memberId, String bookId)
- compact 생성자에서 memberId.isBlank()면 IllegalArgumentException("회원 번호가 비었습니다")
- bookId.isBlank()면 IllegalArgumentException("책 번호가 비었습니다")
- 검사는 T2에서 넣는다. T1에서는 칸만 있는 record다

LoanUseCase.java
- BookView borrow(BorrowCommand command)

LoanService.java
- bookPort.findById(command.bookId())로 책을 찾고 book.borrow(command.memberId())를 부른다
- 나머지 줄은 그대로다

ConsoleMenu.java
- 읽은 memberId, bookId로 new BorrowCommand(memberId, bookId)를 만들어 borrow에 넘긴다
- import hexa.application.port.in.BorrowCommand를 더한다
- 오류를 잡지 않는다. 빈 값이면 프로그램이 멈춘다

LoanCheck.java
- loanUseCase.borrow(new BorrowCommand("M1", "B1"))로 부른다. 찍는 글은 그대로다

BorrowCommandTest.java
- 검증용이다. Main을 부르는 테스트는 앞뒤로 data/books.txt와 data 폴더를 지운다
- S1 BorrowCommand는 port.in의 record이고 칸은 memberId, bookId다. LoanUseCase에 borrow(BorrowCommand)가 있고 borrow(String, String)은 없다. 대출하면 BookView가 돌아온다. ConsoleMenu.java와 LoanCheck.java에 new BorrowCommand(가 있다
- S2 회원 번호나 책 번호가 빈 글자, 빈칸, 탭과 빈칸이면 생성자가 IllegalArgumentException을 던진다. M1, B1은 만들어진다
- S3 Main에 1, 빈 줄, B1을 넣으면 IllegalArgumentException으로 멈추고 대출했습니다가 없다. 1, 빈칸, B2도 같다. 저장 파일의 B2 줄은 회원 칸이 비어 있고 다시 켜서 3을 고르면 B2는 대출 가능이다

앞 장 테스트
- Find에 borrowMethod(), borrow(service, memberId, bookId)를 더한다. BorrowCommand가 있으면 BorrowCommand를 만들어 넘기고, 없으면 글자 두 개를 넘긴다. Touches: store-port
- UseCasePortTest S1, S2는 Find.borrow로 대출한다. Touches: use-case-port
- BookListTest S2, S3은 Find.borrowMethod와 Find.borrow로 대출한다. Touches: book-list

## 핵심 코드

public record BorrowCommand(String memberId, String bookId) {
    public BorrowCommand {
        if (memberId.isBlank()) {
            throw new IllegalArgumentException("회원 번호가 비었습니다");
        }
        if (bookId.isBlank()) {
            throw new IllegalArgumentException("책 번호가 비었습니다");
        }
    }
}

BookView borrow(BorrowCommand command);

BookView book = loanUseCase.borrow(new BorrowCommand(memberId, bookId));

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | BorrowCommand.java, LoanUseCase.java, LoanService.java, ConsoleMenu.java, LoanCheck.java |
| S2 | BorrowCommand.java |
| S3 | BorrowCommand.java, ConsoleMenu.java |

## 라이브러리

- 추가 없음

## 다른 장 파일

- hexa/src/hexa/application/port/in/LoanUseCase.java: use-case-port, hexagon-packages, book-list
- hexa/src/hexa/application/LoanService.java: layered-loan, use-case-port, store-port, hexagon-packages, book-list
- hexa/src/hexa/adapter/in/ConsoleMenu.java: project-setup, layered-loan, use-case-port, hexagon-packages, book-list
- hexa/src/hexa/LoanCheck.java: use-case-port, store-port, hexagon-packages, book-list
- tests/hexa/Find.java: store-port
- tests/hexa/UseCasePortTest.java: use-case-port
- tests/hexa/BookListTest.java: book-list
