# book-list 작업

- [x] 테스트 작성
- [x] T1 포트가 응답 모델을 돌려주게 하기
  - 근거: S3, S4
  - 파일: hexa/src/hexa/application/port/in/BookView.java, hexa/src/hexa/application/port/in/LoanUseCase.java, hexa/src/hexa/application/LoanService.java, hexa/src/hexa/adapter/in/ConsoleMenu.java, hexa/src/hexa/LoanCheck.java
  - 실행하면: 대출과 반납 결과 글은 그대로다. 콘솔 메뉴와 두 번째 입구에서 import hexa.domain.Book이 사라진다
  - Why: 읽기만 되는 응답 모델을 돌려주면 어댑터가 받은 결과로 책의 상태를 바꿀 수 없다
  - 다른 장: project-setup, layered-loan, use-case-port, store-port, hexagon-packages
- [ ] T2 조회 유스케이스로 책 목록 보기
  - 근거: S1, S2
  - 파일: hexa/src/hexa/application/port/out/BookPort.java, hexa/src/hexa/adapter/out/MemoryBookAdapter.java, hexa/src/hexa/adapter/out/FileBookAdapter.java, hexa/src/hexa/application/port/in/LoanUseCase.java, hexa/src/hexa/application/LoanService.java, hexa/src/hexa/adapter/in/ConsoleMenu.java
  - 실행하면: 메뉴에 3 목록이 생긴다. B1을 대출하고 3을 고르면 B1 객체지향의 사실과 오해 대출 중이 찍힌다
  - Why: 상태를 바꾸지 않는 조회도 포트로 열면 콘솔은 저장 방법을 모르고 응답 모델만 받아 찍는다
  - 다른 장: layered-loan, use-case-port, store-port, hexagon-packages, file-store
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
