# file-store 작업

- [ ] 테스트 작성
- [ ] T1 파일 저장 어댑터 만들기
  - 근거: S1, S2, S3
  - 파일: hexa/src/hexa/adapter/out/FileBookAdapter.java, hexa/src/hexa/domain/Book.java
  - 실행하면: Main은 아직 메모리를 쓴다. 껐다 켜면 B1을 다시 대출할 수 있다
  - Why: 책과 파일 한 줄 사이를 바꾸는 일을 어댑터 안에 두면 도메인과 서비스는 파일을 모른다
  - 다른 장: layered-loan, hexagon-packages
- [ ] T2 Main에 파일 저장 끼우기
  - 근거: S4
  - 파일: hexa/src/hexa/Main.java, .gitignore
  - 실행하면: hexa/data/books.txt에 B1,객체지향의 사실과 오해,M1이 남고 다시 켜서 B1을 대출하면 이미 대출 중인 책입니다로 멈춘다
  - Why: 같은 포트를 구현한 어댑터라 Main의 한 줄만 바꾸면 서비스를 고치지 않고 저장 방법이 바뀐다
  - 다른 장: project-setup, layered-loan, use-case-port, store-port, hexagon-packages
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
