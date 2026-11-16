# use-case-port 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/LoanUseCase.java | 새로 | 들어오는 포트. 대출과 반납 |
| hexa/src/hexa/ConsoleMenu.java | 새로 | 드라이빙 어댑터. 콘솔 메뉴, 입력, 결과 글 |
| hexa/src/hexa/LoanCheck.java | 새로 | 두 번째 입구. 콘솔 없이 포트를 부른다 |
| hexa/src/hexa/LoanService.java | 고침 | 포트를 구현한다. 콘솔을 쓰지 않는다 |
| hexa/src/hexa/Main.java | 고침 | 시작 글을 찍고 서비스를 메뉴에 넘긴다 |
| tests/hexa/UseCasePortTest.java | 새로 | S1, S2, S4 검증용 행동 테스트 |

## 코드 계획

나누는 이유
- LoanUseCase: 앱이 바깥에 여는 문에 이름을 붙인다. 어댑터는 이것만 안다
- ConsoleMenu: 콘솔을 다루는 일을 서비스와 Main에서 떼어 한곳에 모은다
- LoanCheck: 콘솔 말고도 포트를 부를 수 있다는 것을 보여 준다

LoanUseCase.java
- 메서드는 borrow(memberId, bookId)와 returnBook(bookId) 두 개다. 둘 다 Book을 돌려준다
- 메서드가 둘뿐이라 유스케이스마다 인터페이스를 나누지 않는다

LoanService.java
- LoanUseCase를 구현한다
- Scanner 필드, 생성자, System.out을 모두 뺀다
- borrow는 책을 찾고 book.borrow 뒤 저장하고 책을 돌려준다
- returnBook은 책을 찾고 book.returnBook 뒤 저장하고 책을 돌려준다
- BookRepository를 필드에서 직접 new하는 것은 그대로 둔다. store-port 장에서 고친다

ConsoleMenu.java
- 생성자로 LoanUseCase를 받는다. 필드 타입도 LoanUseCase다
- Scanner를 필드에서 하나 만든다
- run()은 Main에 있던 메뉴 반복을 옮긴 것이다. 글은 layered-loan과 같다
- 1이면 회원 번호와 책 번호를 읽어 borrow를 부르고 제목 대출했습니다를 찍는다
- 2이면 책 번호를 읽어 returnBook을 부르고 제목 반납했습니다를 찍는다
- 예외를 잡지 않는다

Main.java
- 시작 글을 찍는다
- LoanUseCase loanUseCase = new LoanService()로 만들어 new ConsoleMenu(loanUseCase).run()을 부른다

LoanCheck.java
- main에서 new LoanService()를 LoanUseCase로 받는다
- B1을 M1에게 대출하고 B1을 M1에게 대출: 제목을 찍는다
- B1을 반납하고 B1 반납: 제목을 찍는다

UseCasePortTest.java
- 검증용이다. 학생에게 주지 않고 책에 넣지 않는다
- S1, S2 콘솔 입력 없이 LoanUseCase로 대출과 반납을 불러 돌아온 책의 제목을 확인한다. System.out에 아무것도 찍히지 않는지도 본다
- S4 LoanCheck.main의 출력을 확인한다
- S3은 LayeredLoanTest가 그대로 확인한다

## 핵심 코드

public interface LoanUseCase {
    Book borrow(String memberId, String bookId);
    Book returnBook(String bookId);
}

private final LoanUseCase loanUseCase;

Book book = loanUseCase.borrow(memberId, bookId);
System.out.println(book.getTitle() + " 대출했습니다");

LoanUseCase loanUseCase = new LoanService();
Book book = loanUseCase.borrow("M1", "B1");
System.out.println("B1을 M1에게 대출: " + book.getTitle());

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | LoanUseCase.java, LoanService.java, ConsoleMenu.java, Main.java |
| S2 | LoanService.java, ConsoleMenu.java |
| S3 | ConsoleMenu.java, Main.java |
| S4 | LoanCheck.java |

## 라이브러리

- 추가 없음

## 다른 장 파일

- hexa/src/hexa/Main.java: project-setup, layered-loan
- hexa/src/hexa/LoanService.java: layered-loan
