# dependency-check 작업

- [x] 테스트 작성
- [x] T1 JUnit을 붙이고 도메인 규칙 테스트하기
  - 근거: S1, S2
  - 파일: hexa/lib/junit-platform-console-standalone.jar, hexa/.classpath, hexa/test/hexa/DependencyRuleTest.java
  - 실행하면: Run As, JUnit Test로 도메인 규칙 테스트가 초록불이다. Book에 어댑터 import를 넣으면 빨간불에 그 줄이 보인다
  - Why: 의존 방향을 눈으로 지키면 놓치므로 테스트가 소스의 import를 읽어 대신 지킨다
  - 다른 장: project-setup
- [ ] T2 application과 어댑터 규칙 더하기
  - 근거: S3, S4
  - 파일: hexa/test/hexa/DependencyRuleTest.java
  - 실행하면: 규칙 테스트 세 개가 초록불이다. LoanService에 어댑터 import를 넣으면 application 규칙이 빨간불이다
  - Why: 육각형 안쪽이 바깥을 모르는 규칙과 어댑터끼리 모르는 규칙도 같은 방법으로 지킨다
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
