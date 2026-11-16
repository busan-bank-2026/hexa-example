# borrow-command ERD

```mermaid
erDiagram
  books {
    String id PK
    String title
    String borrowerId
  }
```

- 저장 파일 data/books.txt의 한 줄이 책 하나다
- 칸은 쉼표로 나눈 id, title, borrowerId 순서다

이 장에서 생기거나 바뀐 것
- 없음. 빈 회원 번호가 borrowerId에 들어가지 않게 입력에서 막는다
