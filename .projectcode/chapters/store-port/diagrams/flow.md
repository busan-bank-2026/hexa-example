# store-port 흐름

```mermaid
flowchart LR
  A["입구 (Main, LoanCheck)"] --> B[메모리 어댑터 만들기]
  B --> C[서비스에 포트로 넘기기]
  C --> D["서비스 (LoanService)"]
  D --> E["포트 (BookPort)"]
  E --> F["어댑터 (MemoryBookAdapter)"]
  F --> G[HashMap에서 찾고 저장]
```
