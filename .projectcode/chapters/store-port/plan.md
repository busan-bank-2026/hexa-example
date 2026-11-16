# store-port 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/BookPort.java | 새로 | 나가는 포트. 책 찾기와 저장 |
| hexa/src/hexa/MemoryBookAdapter.java | 새로 | 드리븐 어댑터. HashMap에 책을 저장한다 |
| hexa/src/hexa/BookRepository.java | 고침, 지움 | T1에서 포트를 구현하고 T3에서 MemoryBookAdapter로 바뀌며 지운다 |
| hexa/src/hexa/LoanService.java | 고침 | 포트를 생성자로 받는다 |
| hexa/src/hexa/Main.java | 고침 | 메모리 어댑터를 만들어 서비스에 넘긴다 |
| hexa/src/hexa/LoanCheck.java | 고침 | 메모리 어댑터를 만들어 서비스에 넘긴다 |
| tests/hexa/StorePortTest.java | 새로 | S1, S2, S3 검증용 행동 테스트 |
| tests/hexa/UseCasePortTest.java | 고침 | 서비스를 만드는 법이 바뀌어 맞춘다 |

## 코드 계획

나누는 이유
- BookPort: 서비스가 바깥 저장에 바라는 것에 이름을 붙인다. 서비스는 이것만 안다
- MemoryBookAdapter: 메모리로 저장하는 방법이다. 다른 방법은 다른 어댑터가 된다

BookPort.java
- findById(id)는 책을 돌려준다. 없으면 null이다
- save(book)

BookRepository.java
- T1에서 implements BookPort와 @Override만 더한다
- T3에서 이름을 MemoryBookAdapter로 바꾼다. 내용은 같다

LoanService.java
- T1에서 필드 타입을 BookPort로 바꾼다. new BookRepository()는 남긴다
- T2에서 생성자로 BookPort를 받는다. 필드 이름은 bookPort다

Main.java, LoanCheck.java
- T2에서 BookPort bookPort = new BookRepository()를 만들어 new LoanService(bookPort)에 넘긴다
- T3에서 new MemoryBookAdapter()로 바꾼다

StorePortTest.java
- 검증용이다. 클래스가 장 중간에 생기고 지워져서 리플렉션으로 찾는다
- S1 BookPort가 인터페이스이고 findById, save가 있다. 서비스 필드 타입이 BookPort다
- S2 LoanService에 BookPort를 받는 생성자만 있다
- S3 MemoryBookAdapter가 BookPort를 구현하고 처음 책 3권을 찾는다. BookRepository가 없다

UseCasePortTest.java
- 서비스를 만들 때 BookPort 생성자가 있으면 메모리 어댑터를 넘긴다. 앞 장 테스트를 고치므로 Touches: use-case-port다

## 핵심 코드

public interface BookPort {
    Book findById(String id);
    void save(Book book);
}

private final BookPort bookPort;

public LoanService(BookPort bookPort) {
    this.bookPort = bookPort;
}

BookPort bookPort = new MemoryBookAdapter();
LoanUseCase loanUseCase = new LoanService(bookPort);

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | BookPort.java, BookRepository.java, LoanService.java |
| S2 | LoanService.java, Main.java, LoanCheck.java |
| S3 | MemoryBookAdapter.java, BookRepository.java, Main.java, LoanCheck.java |

## 라이브러리

- 추가 없음

## 다른 장 파일

- hexa/src/hexa/BookRepository.java: layered-loan
- hexa/src/hexa/LoanService.java: layered-loan, use-case-port
- hexa/src/hexa/Main.java: project-setup, layered-loan, use-case-port
- hexa/src/hexa/LoanCheck.java: use-case-port
- tests/hexa/UseCasePortTest.java: use-case-port
