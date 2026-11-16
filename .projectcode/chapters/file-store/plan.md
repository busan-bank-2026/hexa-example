# file-store 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/adapter/out/FileBookAdapter.java | 새로 | 드리븐 어댑터. 책을 data/books.txt에 저장한다 |
| hexa/src/hexa/domain/Book.java | 고침 | 대출한 회원 번호를 읽는 getBorrowerId를 더한다 |
| hexa/src/hexa/Main.java | 고침 | MemoryBookAdapter 대신 FileBookAdapter를 끼운다 |
| .gitignore | 고침 | hexa/data/를 git에 올리지 않는다 |
| tests/hexa/FileStoreTest.java | 새로 | S1, S2, S3, S4 검증용 행동 테스트 |
| tests/hexa/ProjectSetupTest.java | 고침 | Main을 돌리기 전과 뒤에 저장 파일을 지운다 |
| tests/hexa/LayeredLoanTest.java | 고침 | Main을 돌리기 전과 뒤에 저장 파일을 지운다 |
| tests/hexa/HexagonPackagesTest.java | 고침 | Main이 끼우는 저장 어댑터가 바뀌어 adapter.out import만 본다 |

## 코드 계획

나누는 이유
- FileBookAdapter: 파일로 저장하는 방법이다. MemoryBookAdapter와 같은 BookPort를 구현해서 Main이 갈아 끼울 수 있다
- toLine, toBook: 저장 모델 매핑이다. 파일 한 줄과 Book 사이를 바꾸는 일을 어댑터 안에 모은다
- readAll, writeAll: 파일 읽기 쓰기다. 헥사고날이 아닌 기반 코드라 붙여넣는다

FileBookAdapter.java
- 저장 파일은 Path.of("data/books.txt")다. 실행한 폴더 기준이다. 이클립스는 hexa 폴더에서 실행해서 hexa/data/books.txt가 된다
- 생성자는 파일이 없으면 처음 책 3권을 save한다
- findById는 readAll()에서 id로 찾는다. 없으면 null이다
- save는 readAll()에 책을 넣고 writeAll()로 모두 다시 쓴다
- readAll은 줄마다 toBook으로 바꿔 LinkedHashMap에 담는다. 파일 순서를 지킨다
- writeAll은 책마다 toLine으로 바꿔 쓴다. data 폴더가 없으면 만든다
- IOException은 UncheckedIOException으로 감싸 던진다. 포트의 메서드 모양을 바꾸지 않는다
- 파일은 java.nio.file의 기본 글자 방식 UTF-8로 읽고 쓴다

저장 모델 매핑
- toLine: 책 번호, 제목, 대출한 회원 번호를 쉼표로 잇는다. 대출 중이 아니면 회원 번호 칸을 비운다
- toBook: split(",", -1)로 나눈다. -1은 끝의 빈 칸을 버리지 않게 한다
- 회원 번호 칸이 비어 있지 않으면 book.borrow(회원 번호)로 되살린다

Book.java
- getBorrowerId()만 더한다. 한 줄로 쓰려면 대출한 회원을 읽어야 한다
- 되살리기는 생성자를 더하지 않고 borrow를 부른다. 방금 만든 책은 대출 중이 아니라 borrow가 실패하지 않는다
- 이렇게 하면 Book에 대출 상태를 바꾸는 새 길이 생기지 않는다

Main.java
- new MemoryBookAdapter() 한 줄을 new FileBookAdapter()로 바꾸고 import를 바꾼다
- LoanService와 ConsoleMenu는 고치지 않는다

LoanCheck.java
- 고치지 않는다. 메모리 저장을 그대로 쓴다
- 실행할 때마다 대출하고 반납하는 확인용 입구라 사용자의 저장 파일을 건드리지 않게 한다

.gitignore
- hexa/data/를 더한다. 실행할 때마다 바뀌는 파일이다

FileStoreTest.java
- 검증용이다. 테스트는 저장소 루트에서 돌아서 저장 파일은 data/books.txt다
- 테스트마다 앞뒤로 data/books.txt와 data 폴더를 지운다
- S1 FileBookAdapter가 BookPort를 구현한다. M1이 대출한 B1을 저장하면 파일에 B1,객체지향의 사실과 오해,M1 줄이 있고 B2 줄은 회원 칸이 비어 있다
- S2 M1이 대출한 B1을 저장하고 새 어댑터로 찾으면 다시 대출할 때 IllegalStateException이 난다. B2는 대출된다
- S3 파일이 없을 때 어댑터를 만들면 파일에 3줄이 생기고 B1, B2, B3를 찾는다
- S4 Main.java가 FileBookAdapter를 끼우고 LoanService.java에는 File이 없다. Main을 두 번 돌리면 둘째 실행의 B1 대출이 오류로 멈추고, 셋째 실행에서 B1을 반납할 수 있다

앞 장 테스트
- ProjectSetupTest, LayeredLoanTest는 Main을 여러 번 돌린다. 저장이 남으면 서로 섞여서 앞뒤로 저장 파일을 지운다. Touches: project-setup, layered-loan
- HexagonPackagesTest S4는 Main과 LoanCheck가 import hexa.adapter.out.을 갖는지 본다. Touches: hexagon-packages

## 핵심 코드

BookPort bookPort = new FileBookAdapter();

private String toLine(Book book) {
    String borrowerId = book.getBorrowerId() == null ? "" : book.getBorrowerId();
    return book.getId() + "," + book.getTitle() + "," + borrowerId;
}

private Book toBook(String line) {
    String[] parts = line.split(",", -1);
    Book book = new Book(parts[0], parts[1]);
    if (!parts[2].isEmpty()) {
        book.borrow(parts[2]);
    }
    return book;
}

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | FileBookAdapter.java, Book.java |
| S2 | FileBookAdapter.java |
| S3 | FileBookAdapter.java |
| S4 | Main.java, .gitignore |

## 라이브러리

- 추가 없음. JDK의 java.nio.file만 쓴다

## 다른 장 파일

- hexa/src/hexa/domain/Book.java: layered-loan, hexagon-packages
- hexa/src/hexa/Main.java: project-setup, layered-loan, use-case-port, store-port, hexagon-packages
- tests/hexa/ProjectSetupTest.java: project-setup
- tests/hexa/LayeredLoanTest.java: layered-loan
- tests/hexa/HexagonPackagesTest.java: hexagon-packages
