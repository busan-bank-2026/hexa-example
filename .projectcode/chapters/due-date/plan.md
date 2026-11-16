# due-date 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/domain/Book.java | 고침 | 반납 기한을 기억하고 연체인지 답한다 |
| hexa/src/hexa/application/port/in/BookView.java | 고침 | 응답 모델에 반납 기한과 연체를 더한다 |
| hexa/src/hexa/application/LoanService.java | 고침 | 오늘 + 14일을 기한으로 정하고 오늘로 연체를 본다 |
| hexa/src/hexa/adapter/out/FileBookAdapter.java | 고침 | 저장 파일 한 줄에 반납 기한을 더한다 |
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 | 대출 결과와 목록에 반납 기한과 연체를 보여 준다 |
| hexa/src/hexa/application/port/out/ClockPort.java | 새로 | 나가는 포트. 오늘 날짜를 묻는다 |
| hexa/src/hexa/adapter/out/SystemClockAdapter.java | 새로 | 시스템 날짜를 돌려주는 시계 |
| hexa/src/hexa/adapter/out/FixedClockAdapter.java | 새로 | 정해 둔 날짜를 돌려주는 시계 |
| hexa/src/hexa/Main.java | 고침 | 시스템 시계를 끼운다 |
| hexa/src/hexa/LoanCheck.java | 고침 | 정해 둔 시계 둘을 끼워 15일 뒤 연체를 보여 준다 |
| tests/hexa/DueDateTest.java | 새로 | S1부터 S5까지 검증용 행동 테스트 |
| tests/hexa/Find.java | 고침 | 시계 포트가 있으면 시스템 시계를 끼워 서비스를 만든다 |
| tests/hexa/StorePortTest.java | 고침 | 서비스 생성자의 첫 칸이 저장 포트인지 본다 |
| tests/hexa/FileStoreTest.java | 고침 | 대출에 기한을 넘기고 저장 줄의 앞부분을 본다 |
| tests/hexa/BookListTest.java | 고침 | 응답 모델의 앞 세 칸을 본다 |
| tests/hexa/BorrowCommandTest.java | 고침 | 저장 줄의 앞부분을 본다 |
| tests/hexa/LoanErrorsTest.java | 고침 | 저장 줄의 앞부분과 빈 회원 칸을 본다 |

## 코드 계획

나누는 이유
- ClockPort: 오늘 날짜는 프로그램 밖에서 오는 값이다. 저장처럼 나가는 포트로 묻으면 Main이 어떤 시계를 끼울지 정한다
- SystemClockAdapter, FixedClockAdapter: 같은 포트를 구현한 두 시계다. 진짜 날짜와 정해 둔 날짜를 갈아 끼운다
- Book.isOverdue: 기한이 지났는지는 책의 규칙이다. 오늘은 밖에서 받는다. 도메인은 시계를 모른다
- 14일은 서비스가 정한다. 저장 파일에서 되살릴 때 기한을 그대로 넘기려면 Book.borrow가 기한을 받는 것이 가장 단순하다

Book.java
- private LocalDate dueDate를 더한다
- borrow(String memberId, LocalDate dueDate)로 바꾼다. 이미 대출 중 검사는 그대로다
- returnBook은 borrowerId와 dueDate를 함께 비운다
- getDueDate()를 더한다
- isOverdue(LocalDate today)는 dueDate가 있고 today가 dueDate 뒤면 true다. 기한 날은 연체가 아니다

BookView.java
- record BookView(String id, String title, boolean borrowed, LocalDate dueDate, boolean overdue)

LoanService.java
- T1: borrow에서 LocalDate.now().plusDays(14)를 기한으로 넘긴다. toView는 book.isOverdue(LocalDate.now())를 담는다
- T2: private final ClockPort clockPort를 더하고 생성자 LoanService(BookPort bookPort, ClockPort clockPort)로 바꾼다
- T2: 두 곳의 LocalDate.now()를 clockPort.today()로 바꾼다

FileBookAdapter.java
- toLine은 끝에 쉼표와 반납 기한을 더한다. 대출 중이 아니면 칸을 비운다
- toBook은 회원 번호 칸이 있으면 book.borrow(회원 번호, LocalDate.parse(기한 칸))로 되살린다
- 앞 장 모양의 세 칸 줄은 읽지 않는다. 저장 파일을 지우고 시작한다

ConsoleMenu.java
- 대출했습니다 줄 다음에 반납 기한: 날짜를 찍는다
- 목록의 상태는 대출 가능, 대출 중이면 대출 중 반납 기한 날짜, 연체면 끝에 연체를 붙인다
- LocalDate를 import하지 않는다. 날짜는 글자로 이어 붙인다

ClockPort.java
- interface ClockPort { LocalDate today(); }

SystemClockAdapter.java
- today()는 LocalDate.now()다

FixedClockAdapter.java
- 생성자에서 받은 날짜를 today()로 돌려준다

Main.java
- new LoanService(bookPort, new SystemClockAdapter())

LoanCheck.java
- 메모리 저장 하나에 2026-03-02 시계를 끼운 서비스로 B1을 대출하고 반납 기한을 찍는다
- 같은 저장에 2026-03-17 시계를 끼운 서비스를 하나 더 만들어 목록 첫 책의 연체를 찍는다
- 그 서비스로 반납한다. 앞 장의 대출, 반납 두 줄은 그대로다

DueDateTest.java
- 검증용이다. Main을 부르는 테스트는 앞뒤로 data/books.txt와 data 폴더를 지운다
- S1 서비스로 B1을 대출하면 응답의 dueDate가 오늘 + 14일이다. Main에 M1 B1 대출을 넣으면 반납 기한: 날짜가 나온다
- S2 Main에 대출을 넣으면 저장 파일에 B1,객체지향의 사실과 오해,M1,날짜 줄이 있다. 다시 켜서 목록을 보면 기한이 나오고 반납하면 B1,객체지향의 사실과 오해,, 줄이 된다
- S3 대출 뒤 목록에 대출 중 반납 기한 날짜가 나오고 연체가 없다. 저장 파일의 기한을 2020-01-01로 써 두고 목록을 보면 연체가 붙는다
- S4 ClockPort가 port.out의 인터페이스이고 today()가 LocalDate를 돌려준다. domain과 application 소스에 LocalDate.now가 없다. Proxy로 만든 시계 2026-03-02를 끼우면 기한이 2026-03-16이다. 같은 저장에 2026-03-16 시계는 연체가 아니고 2026-03-17 시계는 연체다
- S5 SystemClockAdapter와 FixedClockAdapter가 adapter.out에서 ClockPort를 구현한다. 시스템 시계는 오늘, 정해 둔 시계는 받은 날을 돌려준다. Main.java는 new SystemClockAdapter(), LoanCheck.java는 new FixedClockAdapter(를 쓴다. LoanCheck를 돌리면 반납 기한: 2026-03-16과 15일 뒤 B1 연체: true가 나온다

앞 장 테스트
- Find.newLoanService는 ClockPort가 있으면 (BookPort, ClockPort) 생성자에 SystemClockAdapter를 넣는다. Touches: store-port, borrow-command
- StorePortTest S2는 생성자가 하나이고 첫 칸이 BookPort인지 본다. Touches: store-port
- FileStoreTest의 borrow 도우미는 borrow(String, LocalDate)가 있으면 오늘 + 14일을 넘긴다. S1은 줄의 앞부분 B1,객체지향의 사실과 오해,M1과 B2,클린 아키텍처,를 본다. Touches: file-store
- BookListTest S3은 응답 모델의 앞 세 칸이 id, title, borrowed인지 본다. Touches: book-list
- BorrowCommandTest S3은 B2 줄의 앞부분을 본다. Touches: borrow-command
- LoanErrorsTest S1은 B1 줄의 앞부분을, S3은 줄마다 회원 칸이 비었는지 본다. Touches: loan-errors

## 핵심 코드

public void borrow(String memberId, LocalDate dueDate) {
    ...
    this.borrowerId = memberId;
    this.dueDate = dueDate;
}

public boolean isOverdue(LocalDate today) {
    return dueDate != null && today.isAfter(dueDate);
}

public interface ClockPort {
    LocalDate today();
}

book.borrow(command.memberId(), clockPort.today().plusDays(14));

LoanUseCase loanUseCase = new LoanService(bookPort, new SystemClockAdapter());

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | Book.java, BookView.java, LoanService.java, ConsoleMenu.java |
| S2 | FileBookAdapter.java, Book.java |
| S3 | Book.java, BookView.java, LoanService.java, ConsoleMenu.java |
| S4 | ClockPort.java, LoanService.java |
| S5 | SystemClockAdapter.java, FixedClockAdapter.java, Main.java, LoanCheck.java |

## 라이브러리

- 추가 없음. JDK의 java.time.LocalDate만 쓴다

## 다른 장 파일

- hexa/src/hexa/domain/Book.java: layered-loan, hexagon-packages, file-store, loan-errors
- hexa/src/hexa/application/port/in/BookView.java: book-list
- hexa/src/hexa/application/LoanService.java: layered-loan, use-case-port, store-port, hexagon-packages, book-list, borrow-command, loan-errors
- hexa/src/hexa/adapter/out/FileBookAdapter.java: file-store, book-list
- hexa/src/hexa/adapter/in/ConsoleMenu.java: project-setup, layered-loan, use-case-port, hexagon-packages, book-list, borrow-command, loan-errors
- hexa/src/hexa/Main.java: project-setup, layered-loan, use-case-port, store-port, hexagon-packages, file-store
- hexa/src/hexa/LoanCheck.java: use-case-port, store-port, hexagon-packages, book-list, borrow-command
- tests/hexa/Find.java: store-port, borrow-command
- tests/hexa/StorePortTest.java: store-port
- tests/hexa/FileStoreTest.java: file-store
- tests/hexa/BookListTest.java: book-list
- tests/hexa/BorrowCommandTest.java: borrow-command
- tests/hexa/LoanErrorsTest.java: loan-errors
