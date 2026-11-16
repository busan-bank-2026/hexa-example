# overdue-notice 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/application/port/out/NotifyPort.java | 새로 | 나가는 포트. 회원에게 알림 글을 보낸다 |
| hexa/src/hexa/application/port/in/LoanUseCase.java | 고침 | 연체 알림 유스케이스를 더한다 |
| hexa/src/hexa/application/LoanService.java | 고침 | 연체된 책마다 알림 포트로 보내고 보낸 수를 돌려준다 |
| hexa/src/hexa/adapter/out/ConsoleNotifyAdapter.java | 새로 | 알림을 콘솔에 찍는다 |
| hexa/src/hexa/adapter/out/FileNotifyAdapter.java | 새로 | 알림을 data/notices.txt에 한 줄씩 더한다 |
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 | 메뉴 4 연체 알림을 더한다 |
| hexa/src/hexa/Main.java | 고침 | T1은 콘솔 알림, T2는 파일 알림을 끼운다 |
| hexa/src/hexa/LoanCheck.java | 고침 | 콘솔 알림을 끼우고 15일 뒤 연체 알림을 보낸다 |
| tests/hexa/OverdueNoticeTest.java | 새로 | S1부터 S5까지 검증용 행동 테스트 |
| tests/hexa/Find.java | 고침 | 알림 포트가 있으면 아무것도 안 하는 알림을 끼워 서비스를 만든다 |
| tests/hexa/BookListTest.java | 고침 | 메뉴 글에 4 연체 알림이 들어간다 |
| tests/hexa/DueDateTest.java | 고침 | 서비스를 만들 때 알림 포트가 있으면 함께 넘긴다 |

## 코드 계획

나누는 이유
- NotifyPort: 알림은 프로그램 밖으로 나가는 일이다. 저장, 시계처럼 나가는 포트로 두면 서비스는 어디로 가는지 모른다
- ConsoleNotifyAdapter, FileNotifyAdapter: 같은 포트를 구현한 두 알림이다. Main이 한 줄로 고른다
- 메서드 이름은 send다. notify는 자바의 모든 객체가 가진 메서드 이름이라 헷갈린다

NotifyPort.java
- interface NotifyPort { void send(String memberId, String message); }

LoanUseCase.java
- int notifyOverdue()를 더한다. 보낸 알림 수를 돌려준다. book-list처럼 같은 포트에 메서드를 더하는 것이 가장 단순하다

LoanService.java
- private final NotifyPort notifyPort를 더하고 생성자 LoanService(BookPort bookPort, ClockPort clockPort, NotifyPort notifyPort)로 바꾼다
- notifyOverdue는 bookPort.findAll()을 돌며 book.isOverdue(clockPort.today())인 책만 보낸다
- 받는 사람은 book.getBorrowerId(), 글은 제목 + " 반납 기한 " + 기한 + "이 지났습니다"다
- 보낸 수를 세어 돌려준다

ConsoleNotifyAdapter.java
- send는 회원 번호 + "에게 알림: " + 글을 찍는다

FileNotifyAdapter.java
- private final Path file = Path.of("data/notices.txt")
- send는 폴더를 만들고 회원 번호,글 한 줄을 Files.write의 CREATE, APPEND로 끝에 더한다
- IOException은 FileBookAdapter처럼 UncheckedIOException으로 바꾼다
- 파일 읽기 쓰기 줄은 붙여넣기다

ConsoleMenu.java
- 메뉴 글을 1 대출  2 반납  3 목록  4 연체 알림  0 종료로 바꾼다
- 4를 고르면 loanUseCase.notifyOverdue()의 수로 연체 알림 N건을 보냈습니다를 찍는다

Main.java
- T1: new LoanService(bookPort, new SystemClockAdapter(), new ConsoleNotifyAdapter())
- T2: new ConsoleNotifyAdapter()를 new FileNotifyAdapter()로 바꾼다. import 한 줄과 이 한 줄만 바뀐다

LoanCheck.java
- 두 서비스 모두 같은 ConsoleNotifyAdapter 하나를 받는다
- 15일 뒤 연체 줄 다음에 later.notifyOverdue()를 부르고 연체 알림: N건을 찍는다
- 반납 줄은 그대로 마지막이다

OverdueNoticeTest.java
- 검증용이다. Main을 부르는 테스트는 앞뒤로 data/books.txt, data/notices.txt, data 폴더를 지운다
- S1 메뉴에 4 연체 알림이 있다. 저장 파일의 B1 기한을 2020-01-01로 써 두고 4를 고르면 연체 알림 1건을 보냈습니다가 나온다. 연체가 없으면 0건이다
- S2 Proxy로 만든 2026-03-02 시계로 B1과 B2를 빌리고 B2는 기한 안 시계로 반납 없이 둔다. 받은 알림을 모으는 Proxy 알림을 끼운다. 2026-03-17 시계로 알리면 B1 한 건, 받는 사람 M1, 글에 제목과 2026-03-16과 지났습니다가 있다. 기한 날 2026-03-16 시계로는 0건이다
- S3 NotifyPort가 port.out의 인터페이스이고 send(String, String)가 있다. LoanService의 필드에 NotifyPort가 있다. application 소스에 System.out과 notices가 없다
- S4 ConsoleNotifyAdapter가 adapter.out에서 NotifyPort를 구현하고 send가 M1에게 알림: 글을 찍는다. LoanCheck.java는 new ConsoleNotifyAdapter()를 쓰고 돌리면 M1에게 알림:과 연체 알림: 1건이 나온다
- S5 FileNotifyAdapter가 adapter.out에서 NotifyPort를 구현한다. Main.java는 new FileNotifyAdapter()를 쓰고 new ConsoleNotifyAdapter()를 쓰지 않는다. 지난 기한 저장 파일로 Main에서 4를 두 번 고르면 콘솔에 알림 글이 없고 data/notices.txt에 M1,객체지향의 사실과 오해 반납 기한 2020-01-01이 지났습니다 줄이 두 번 있다

앞 장 테스트
- Find.newLoanService는 NotifyPort가 있으면 아무것도 안 하는 Proxy 알림을 세 번째 칸에 넣는다. Touches: store-port, borrow-command
- BookListTest S1은 메뉴 글 1 대출  2 반납  3 목록  4 연체 알림  0 종료를 본다. Touches: book-list
- DueDateTest의 service 도우미는 NotifyPort가 있으면 아무것도 안 하는 알림을 함께 넘긴다. Touches: due-date

## 핵심 코드

public interface NotifyPort {
    void send(String memberId, String message);
}

for (Book book : bookPort.findAll()) {
    if (book.isOverdue(clockPort.today())) {
        notifyPort.send(book.getBorrowerId(), book.getTitle() + " 반납 기한 " + book.getDueDate() + "이 지났습니다");
        count++;
    }
}

LoanUseCase loanUseCase = new LoanService(bookPort, new SystemClockAdapter(), new FileNotifyAdapter());

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | LoanUseCase.java, LoanService.java, ConsoleMenu.java |
| S2 | LoanService.java |
| S3 | NotifyPort.java, LoanService.java |
| S4 | ConsoleNotifyAdapter.java, Main.java, LoanCheck.java |
| S5 | FileNotifyAdapter.java, Main.java |

## 라이브러리

- 추가 없음. JDK의 java.nio.file만 쓴다

## 다른 장 파일

- hexa/src/hexa/application/port/in/LoanUseCase.java: use-case-port, hexagon-packages, book-list, borrow-command
- hexa/src/hexa/application/LoanService.java: layered-loan, use-case-port, store-port, hexagon-packages, book-list, borrow-command, loan-errors, due-date
- hexa/src/hexa/adapter/in/ConsoleMenu.java: project-setup, layered-loan, use-case-port, hexagon-packages, book-list, borrow-command, loan-errors, due-date
- hexa/src/hexa/Main.java: project-setup, layered-loan, use-case-port, store-port, hexagon-packages, file-store, due-date
- hexa/src/hexa/LoanCheck.java: use-case-port, store-port, hexagon-packages, book-list, borrow-command, due-date
- tests/hexa/Find.java: store-port, borrow-command
- tests/hexa/BookListTest.java: book-list
- tests/hexa/DueDateTest.java: due-date
