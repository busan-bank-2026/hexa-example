# loan-errors 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 | 메뉴 한 번의 일을 try로 감싸 예외 메시지를 찍고 메뉴로 돌아간다 |
| hexa/src/hexa/application/LoanService.java | 고침 | 없는 책 번호면 책을 찾을 수 없습니다 예외를 던진다 |
| hexa/src/hexa/domain/Book.java | 고침 | 대출 중이 아닌 책을 반납하면 예외를 던진다 |
| tests/hexa/LoanErrorsTest.java | 새로 | S1부터 S5까지 검증용 행동 테스트 |
| tests/hexa/LayeredLoanTest.java | 고침 | S3이 멈춤 대신 거절 메시지를 본다 |
| tests/hexa/FileStoreTest.java | 고침 | S4가 멈춤 대신 거절 메시지를 본다 |
| tests/hexa/BorrowCommandTest.java | 고침 | S3이 멈춤 대신 빈 값 메시지를 본다 |

## 코드 계획

나누는 이유
- 예외를 메시지로 바꾸는 일은 콘솔 메뉴에만 둔다. 메시지를 어떻게 보여 줄지는 어댑터마다 다르기 때문이다
- 서비스와 도메인은 예외를 던지기만 한다. 콘솔이 아닌 입구도 같은 실패를 받는다
- 예외 클래스를 새로 만들지 않는다. 도메인은 아무것도 import하지 않고 콘솔 메뉴는 도메인을 import하지 않는다. 둘 다 아는 자바 표준 예외가 가장 단순하다
- 서비스의 findBook은 대출과 반납 두 곳에서 같은 찾기와 검사를 쓰려고 나눈다

ConsoleMenu.java
- 메뉴 번호를 읽은 뒤의 if 묶음 전체를 try로 감싼다
- catch (IllegalArgumentException | IllegalStateException e)에서 e.getMessage()를 한 줄 찍는다
- catch 뒤 while이 이어져 메뉴를 다시 보여 준다
- 0의 break는 try 안에 그대로 둔다

LoanService.java
- private Book findBook(String bookId)를 더한다. bookPort.findById 결과가 null이면 IllegalArgumentException("책을 찾을 수 없습니다")
- borrow와 returnBook은 bookPort.findById 대신 findBook을 부른다

Book.java
- returnBook에서 borrowerId가 null이면 IllegalStateException("대출 중이 아닌 책입니다")
- borrow의 이미 대출 중 검사는 그대로다

LoanErrorsTest.java
- 검증용이다. Main을 부르는 테스트는 앞뒤로 data/books.txt와 data 폴더를 지운다
- S1 Main에 M1 B1 대출, M2 B1 대출, 0을 넣으면 멈추지 않고 이미 대출 중인 책입니다와 프로그램을 끝냅니다가 나온다. 대출했습니다는 한 번이다. 저장 파일의 B1은 M1이다
- S2 서비스로 B9를 대출하거나 반납하면 IllegalArgumentException 책을 찾을 수 없습니다다. Main에 B9 대출과 반납을 넣으면 메시지가 두 번 나오고 프로그램을 끝냅니다가 나온다
- S3 Main에 빈 회원, 빈칸 회원, 빈칸 책 번호로 대출을 넣으면 회원 번호가 비었습니다 두 번, 책 번호가 비었습니다 한 번이 나오고 끝난다. 대출했습니다가 없고 저장 파일의 세 책은 회원 칸이 비어 있다
- S4 새 책의 returnBook은 IllegalStateException 대출 중이 아닌 책입니다다. Main에 B2 반납을 넣으면 그 메시지가 나오고 반납했습니다가 없다
- S5 domain과 application 소스에 System.out과 catch가 없다. 학생 코드에서 getMessage()를 쓰는 파일은 ConsoleMenu.java 하나다

앞 장 테스트
- LayeredLoanTest S3은 대출 중인 책을 다시 빌리면 거절 메시지가 나오고 프로그램을 끝냅니다까지 간다는 것을 본다. 이름 끝의 오류로 프로그램이 멈춘다를 뺀다. Touches: layered-loan
- FileStoreTest S4의 두 번째 실행은 이미 대출 중인 책입니다가 나오고 대출했습니다가 없다는 것을 본다. Touches: file-store
- BorrowCommandTest S3은 빈 값 메시지가 나오고 대출했습니다가 없다는 것을 본다. 이름의 오류로 멈춘다를 거절한다로 바꾼다. Touches: borrow-command

## 핵심 코드

try {
    if (choice.equals("1")) {
        ...
    }
} catch (IllegalArgumentException | IllegalStateException e) {
    System.out.println(e.getMessage());
}

private Book findBook(String bookId) {
    Book book = bookPort.findById(bookId);
    if (book == null) {
        throw new IllegalArgumentException("책을 찾을 수 없습니다");
    }
    return book;
}

public void returnBook() {
    if (borrowerId == null) {
        throw new IllegalStateException("대출 중이 아닌 책입니다");
    }
    this.borrowerId = null;
}

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | ConsoleMenu.java |
| S2 | LoanService.java, ConsoleMenu.java |
| S3 | ConsoleMenu.java |
| S4 | Book.java, ConsoleMenu.java |
| S5 | ConsoleMenu.java |

## 라이브러리

- 추가 없음

## 다른 장 파일

- hexa/src/hexa/adapter/in/ConsoleMenu.java: project-setup, layered-loan, use-case-port, hexagon-packages, book-list, borrow-command
- hexa/src/hexa/application/LoanService.java: layered-loan, use-case-port, store-port, hexagon-packages, book-list, borrow-command
- hexa/src/hexa/domain/Book.java: layered-loan, hexagon-packages, file-store
- tests/hexa/LayeredLoanTest.java: layered-loan
- tests/hexa/FileStoreTest.java: file-store
- tests/hexa/BorrowCommandTest.java: borrow-command
