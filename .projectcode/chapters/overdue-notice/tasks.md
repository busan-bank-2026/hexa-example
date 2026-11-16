# overdue-notice 작업

- [ ] 테스트 작성
- [ ] T1 알림 포트로 연체 알림 보내기
  - 근거: S1, S2, S3, S4
  - 파일: hexa/src/hexa/application/port/out/NotifyPort.java, hexa/src/hexa/application/port/in/LoanUseCase.java, hexa/src/hexa/application/LoanService.java, hexa/src/hexa/adapter/out/ConsoleNotifyAdapter.java, hexa/src/hexa/adapter/in/ConsoleMenu.java, hexa/src/hexa/Main.java, hexa/src/hexa/LoanCheck.java
  - 실행하면: 메뉴에 4 연체 알림이 생기고 고르면 연체 알림 N건을 보냈습니다가 나온다. 두 번째 입구를 돌리면 M1에게 알림: 줄과 연체 알림: 1건이 바로 나온다
  - Why: 알림은 프로그램 밖으로 나가는 일이라 포트로 두면 서비스는 어디로 가는지 모른 채 연체만 골라 보낸다
  - 다른 장: use-case-port, hexagon-packages, book-list, borrow-command, layered-loan, store-port, loan-errors, due-date, project-setup, file-store
- [ ] T2 파일 알림 어댑터 하나 더 꽂기
  - 근거: S5
  - 파일: hexa/src/hexa/adapter/out/FileNotifyAdapter.java, hexa/src/hexa/Main.java
  - 실행하면: Main의 연체 알림이 콘솔 대신 data/notices.txt에 한 줄씩 쌓인다. 두 번째 입구는 T1처럼 콘솔에 알린다
  - Why: 같은 포트에 어댑터를 하나 더 꽂으면 서비스를 고치지 않고 Main 한 줄로 알림이 가는 곳을 바꾼다
  - 다른 장: project-setup, layered-loan, use-case-port, store-port, hexagon-packages, file-store, due-date
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
