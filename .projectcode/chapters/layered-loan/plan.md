# layered-loan 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/Main.java | 고침 | 시작 글, 메뉴 반복, 서비스 부르기 |
| hexa/src/hexa/Book.java | 새로 | 책. 대출과 반납 규칙을 가진다 |
| hexa/src/hexa/BookRepository.java | 새로 | HashMap에 책을 저장한다 |
| hexa/src/hexa/LoanService.java | 새로 | 대출과 반납 순서. 콘솔 입력과 출력도 직접 한다 |
| tests/hexa/LayeredLoanTest.java | 새로 | S1부터 S5까지 검증용 행동 테스트 |

## 코드 계획

나누는 이유
- Book: 대출 규칙은 책이 가진다. DDD 전술 설계에서 배운 엔티티다
- BookRepository: 저장을 맡는 계층이다
- LoanService: 대출 순서를 맡는 계층이다
- Main: 메뉴를 맡는 계층이다

Main.java
- 시작 글을 찍는다
- Scanner 하나를 만들어 LoanService에 넘긴다. System.in에 Scanner를 둘 만들면 입력이 꼬인다
- while 반복에서 메뉴 1 대출  2 반납  0 종료와 > 를 찍고 한 줄을 읽는다
- 1이면 borrow, 2이면 returnBook을 부른다
- 0이면 프로그램을 끝냅니다를 찍고 반복을 끝낸다
- 다른 값이면 아무것도 하지 않고 메뉴를 다시 찍는다

Book.java
- 필드는 id, title, borrowerId다. borrowerId가 null이면 대출 가능이다
- borrow(memberId)는 대출 중이면 IllegalStateException("이미 대출 중인 책입니다")을 던진다
- returnBook()은 borrowerId를 null로 되돌린다
- getId, getTitle이 있다

BookRepository.java
- HashMap<String, Book>에 처음 책 3권을 넣는다. B1 객체지향의 사실과 오해, B2 클린 아키텍처, B3 도메인 주도 설계 첫걸음
- findById(id)는 없으면 null을 돌려준다
- save(book)는 id로 넣는다

LoanService.java
- 생성자로 Scanner를 받는다
- BookRepository를 필드에서 직접 new한다
- borrow()는 회원 번호와 책 번호를 콘솔에서 읽고 book.borrow 뒤 저장하고 제목 대출했습니다를 찍는다
- returnBook()은 책 번호를 콘솔에서 읽고 book.returnBook 뒤 저장하고 제목 반납했습니다를 찍는다
- 예외를 잡지 않는다. 대출 중인 책과 없는 책은 프로그램이 멈춘다

일부러 남기는 문제
- 서비스가 콘솔에서 직접 읽고 찍는다: use-case-port 장
- 서비스가 저장 클래스를 직접 new한다: store-port 장
- 예외를 잡지 않는다: loan-errors 장

LayeredLoanTest.java
- 검증용이다. 학생에게 주지 않고 책에 넣지 않는다
- System.in에 입력 글을 넣고 System.out을 모아 Main.main을 실행한다
- S3은 Main.main이 IllegalStateException을 던지는지 확인한다
- ProjectSetupTest의 S1 시작 글 확인은 입력이 없으면 멈춘다. 그래서 그 테스트에도 0 입력을 넣어야 한다. 앞 장 테스트를 고치므로 Touches: project-setup이다

## 핵심 코드

public class LoanService {
    private final Scanner scanner;
    private final BookRepository repository = new BookRepository();

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

public void borrow(String memberId) {
    if (borrowerId != null) {
        throw new IllegalStateException("이미 대출 중인 책입니다");
    }
    this.borrowerId = memberId;
}

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | Main.java |
| S2 | Main.java, Book.java, BookRepository.java, LoanService.java |
| S3 | Book.java, LoanService.java |
| S4 | Main.java, Book.java, LoanService.java |
| S5 | Main.java |

## 라이브러리

- 추가 없음

## 다른 장 파일

- hexa/src/hexa/Main.java: project-setup
- tests/hexa/ProjectSetupTest.java: project-setup
