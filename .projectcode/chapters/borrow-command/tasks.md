# borrow-command 작업

- [ ] 테스트 작성
- [ ] T1 대출 요청을 입력 모델로 받기
  - 근거: S1
  - 파일: hexa/src/hexa/application/port/in/BorrowCommand.java, hexa/src/hexa/application/port/in/LoanUseCase.java, hexa/src/hexa/application/LoanService.java, hexa/src/hexa/adapter/in/ConsoleMenu.java, hexa/src/hexa/LoanCheck.java
  - 실행하면: 바른 값으로 대출하면 결과 글은 그대로다. 회원 번호를 비워도 아직 대출했습니다가 나온다
  - Why: 대출에 필요한 값을 한 묶음으로 받아야 포트 안으로 들어오기 전에 검사할 자리가 생긴다
  - 다른 장: project-setup, layered-loan, use-case-port, store-port, hexagon-packages, book-list
- [ ] T2 대출 요청이 빈 값을 막기
  - 근거: S2, S3
  - 파일: hexa/src/hexa/application/port/in/BorrowCommand.java
  - 실행하면: 회원 번호를 비우고 대출하면 IllegalArgumentException 회원 번호가 비었습니다로 멈춘다
  - Why: 형식 검사는 책 상태를 몰라도 되니 입력 모델이 만들어질 때 막고 업무 규칙은 도메인 책에 둔다
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
