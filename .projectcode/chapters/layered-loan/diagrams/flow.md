# layered-loan 흐름

```mermaid
flowchart LR
  A["메뉴 (Main)"] --> B[번호 고르기]
  B --> C[서비스가 입력 받기]
  C --> D[저장소에서 책 찾기]
  D --> E["대출 반납 (Book)"]
  E --> F[저장소에 저장]
  F --> G[결과 글]
  G --> A
```
