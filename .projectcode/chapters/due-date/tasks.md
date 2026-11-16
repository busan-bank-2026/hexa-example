# due-date 작업

- [x] 테스트 작성
- [x] T1 반납 기한 정하고 보여 주기
  - 근거: S1, S2, S3
  - 파일: hexa/src/hexa/domain/Book.java, hexa/src/hexa/application/port/in/BookView.java, hexa/src/hexa/application/LoanService.java, hexa/src/hexa/adapter/out/FileBookAdapter.java, hexa/src/hexa/adapter/in/ConsoleMenu.java
  - 실행하면: 대출하면 반납 기한: 14일 뒤 날짜가 나오고 목록과 저장 파일에 기한이 남는다. 연체 표시는 14일을 기다려야 볼 수 있다
  - Why: 서비스가 시스템 날짜를 직접 읽으면 기한은 정해지지만 오늘이 아닌 날을 실행해 볼 수 없다
  - 다른 장: layered-loan, use-case-port, store-port, hexagon-packages, file-store, book-list, borrow-command, loan-errors, project-setup
- [x] T2 시계 포트로 오늘 갈아 끼우기
  - 근거: S4, S5
  - 파일: hexa/src/hexa/application/port/out/ClockPort.java, hexa/src/hexa/adapter/out/SystemClockAdapter.java, hexa/src/hexa/adapter/out/FixedClockAdapter.java, hexa/src/hexa/application/LoanService.java, hexa/src/hexa/Main.java, hexa/src/hexa/LoanCheck.java
  - 실행하면: Main은 T1과 같다. 두 번째 입구를 돌리면 2026-03-02에 빌린 B1이 15일 뒤 연체로 바로 나온다
  - Why: 오늘 날짜도 프로그램 밖에서 오는 값이라 포트로 묻으면 시계를 갈아 끼워 어느 날이든 바로 실행해 볼 수 있다
  - 다른 장: layered-loan, use-case-port, store-port, hexagon-packages, file-store, book-list, borrow-command, loan-errors, project-setup
- [x] 수렴 점검
- [x] 캡처
- [x] 도식
