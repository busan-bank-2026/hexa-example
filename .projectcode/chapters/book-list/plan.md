# book-list 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/application/port/in/BookView.java | 새로 | 응답 모델. 책 번호, 제목, 대출 여부를 담는 record |
| hexa/src/hexa/application/port/in/LoanUseCase.java | 고침 | 대출과 반납이 BookView를 돌려준다. 목록 조회 listBooks를 더한다 |
| hexa/src/hexa/application/LoanService.java | 고침 | 책을 BookView로 바꿔 돌려준다. listBooks를 구현한다 |
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 고침 | BookView로 결과 글을 찍는다. 메뉴 3 목록을 더한다 |
| hexa/src/hexa/LoanCheck.java | 고침 | BookView로 결과 글을 찍는다 |
| hexa/src/hexa/application/port/out/BookPort.java | 고침 | 전체 찾기 findAll을 더한다 |
| hexa/src/hexa/adapter/out/MemoryBookAdapter.java | 고침 | findAll을 구현한다. 넣은 순서를 지키게 LinkedHashMap으로 바꾼다 |
| hexa/src/hexa/adapter/out/FileBookAdapter.java | 고침 | findAll을 구현한다 |
| tests/hexa/BookListTest.java | 새로 | S1, S2, S3, S4 검증용 행동 테스트 |
| tests/hexa/LayeredLoanTest.java | 고침 | 메뉴 글이 바뀌어 메뉴 앞부분만 센다 |
| tests/hexa/UseCasePortTest.java | 고침 | 포트가 BookView를 돌려줘서 제목을 title()로 읽는다 |

## 코드 계획

나누는 이유
- BookView: 포트 밖으로 나가는 응답 모델이다. 읽기만 되는 record라 받은 쪽이 상태를 바꿀 수 없다
- BookView는 들어오는 포트와 같은 port.in에 둔다. 포트의 메서드 모양의 일부라서다
- toView: 대출, 반납, 목록 세 곳에서 책을 BookView로 바꿔서 LoanService 안에 함수 하나로 모은다
- 조회는 LoanUseCase에 listBooks로 더한다. 콘솔 메뉴가 포트 하나만 받는 모양을 그대로 둔다

BookView.java
- public record BookView(String id, String title, boolean borrowed)
- 대출한 회원 번호는 담지 않는다. 목록과 결과 글에 쓰지 않는다

LoanUseCase.java
- BookView borrow(String memberId, String bookId)
- BookView returnBook(String bookId)
- List<BookView> listBooks()
- import hexa.domain.Book을 지운다. 들어오는 포트는 도메인을 모른다

LoanService.java
- borrow, returnBook은 마지막 줄을 return toView(book)으로 바꾼다
- listBooks는 bookPort.findAll()의 책마다 toView로 바꿔 새 ArrayList에 담는다. 저장은 부르지 않는다
- toView는 book.getBorrowerId()가 null이 아니면 borrowed가 true다

ConsoleMenu.java
- 메뉴 글을 1 대출  2 반납  3 목록  0 종료로 바꾼다
- 대출과 반납은 Book 대신 BookView를 받고 book.title()로 찍는다
- 3을 고르면 listBooks()의 책마다 번호, 제목, 상태를 빈칸으로 이어 찍는다. 상태는 borrowed()가 true면 대출 중, 아니면 대출 가능이다
- import hexa.domain.Book 대신 import hexa.application.port.in.BookView를 쓴다

LoanCheck.java
- Book 대신 BookView를 받고 title()로 찍는다. 찍는 글은 그대로다
- import hexa.domain.Book 대신 import hexa.application.port.in.BookView를 쓴다

BookPort.java
- List<Book> findAll()을 더한다

MemoryBookAdapter.java
- HashMap을 LinkedHashMap으로 바꾼다. HashMap은 B2, B3, B1 순서로 꺼내서 목록 순서가 섞인다
- findAll은 new ArrayList<>(books.values())를 돌려준다

FileBookAdapter.java
- findAll은 new ArrayList<>(readAll().values())를 돌려준다. readAll이 파일 순서를 지킨다

BookListTest.java
- 검증용이다. Main을 부르는 테스트는 앞뒤로 data/books.txt와 data 폴더를 지운다
- S1 Main에서 3을 고르면 새 메뉴 글과 세 책이 대출 가능으로 찍힌다. B1을 대출한 뒤 3을 고르면 B1은 대출 중, B2는 대출 가능이다
- S2 BookPort에 findAll이 있고 두 어댑터가 B1, B2, B3 순서로 돌려준다. listBooks를 두 번 불러도 같고, B2를 대출하면 B2만 borrowed가 true다
- S3 BookView는 port.in의 record이고 칸은 id, title, borrowed다. borrow, returnBook이 BookView를 돌려준다. BookView에 borrow, returnBook 메서드가 없다
- S4 ConsoleMenu.java와 LoanCheck.java에 import hexa.domain.이 없다

앞 장 테스트
- LayeredLoanTest S1은 메뉴 글 전체를 세던 것을 1 대출  2 반납까지만 센다. 메뉴에 3 목록이 들어가도 메뉴가 다시 나오는지 본다. Touches: layered-loan
- UseCasePortTest의 title은 getTitle 대신 title()을 부른다. Touches: use-case-port

## 핵심 코드

public record BookView(String id, String title, boolean borrowed) {
}

List<BookView> listBooks();

private BookView toView(Book book) {
    return new BookView(book.getId(), book.getTitle(), book.getBorrowerId() != null);
}

for (BookView book : loanUseCase.listBooks()) {
    String status = book.borrowed() ? "대출 중" : "대출 가능";
    System.out.println(book.id() + " " + book.title() + " " + status);
}

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | ConsoleMenu.java |
| S2 | LoanUseCase.java, LoanService.java, BookPort.java, MemoryBookAdapter.java, FileBookAdapter.java |
| S3 | BookView.java, LoanUseCase.java, LoanService.java |
| S4 | ConsoleMenu.java, LoanCheck.java |

## 라이브러리

- 추가 없음

## 다른 장 파일

- hexa/src/hexa/application/port/in/LoanUseCase.java: use-case-port, hexagon-packages
- hexa/src/hexa/application/LoanService.java: layered-loan, use-case-port, store-port, hexagon-packages
- hexa/src/hexa/adapter/in/ConsoleMenu.java: project-setup, layered-loan, use-case-port, hexagon-packages
- hexa/src/hexa/LoanCheck.java: use-case-port, store-port, hexagon-packages
- hexa/src/hexa/application/port/out/BookPort.java: store-port, hexagon-packages
- hexa/src/hexa/adapter/out/MemoryBookAdapter.java: layered-loan, store-port, hexagon-packages
- hexa/src/hexa/adapter/out/FileBookAdapter.java: file-store
- tests/hexa/LayeredLoanTest.java: layered-loan
- tests/hexa/UseCasePortTest.java: use-case-port
