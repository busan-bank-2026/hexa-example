# 프로젝트 규칙

## 기술

| 항목 | 선택 |
|---|---|
| 언어 | Java 21 |
| IDE | 이클립스. hexa에 .project와 .classpath를 넣는다 |
| 빌드 도구 | 없음 |
| 학생 코드 라이브러리 | 없음 |
| 저장소 | HashMap. 6장부터 파일. 13장만 Oracle XE 21c |
| 테스트 | JUnit 5 (tests/lib/junit-platform-console-standalone.jar). 검증용이다. 학생이 테스트를 쓰는 장에서 hexa에 붙인다 |
| DB 드라이버 | 13장만 ojdbc11.jar (오라클 설치 폴더에서 복사) |
| 파일 형식 | 한 줄에 하나. 쉼표로 나눈 글자 |
| 환경 | 폐쇄망. 인터넷에서 받는 것을 쓰지 않는다 |

## 폴더

| 무엇 | 폴더 |
|---|---|
| 학생 코드 | hexa |
| 행동 테스트 | tests. 검증용이다. 학생에게 주지 않는다 |
| jar | tests/lib에 검증용 JUnit jar. hexa/lib에 13장의 ojdbc jar |
| 컴파일 결과 | hexa/bin, tests/bin. git에 올리지 않는다 |

## 실행

- 실행: cd hexa, javac -d bin --source-path src src/hexa/Main.java, java -cp bin hexa.Main
- 테스트: 저장소 루트에서 javac -encoding UTF-8 -d hexa/bin $(find hexa/src -name "*.java"), javac -encoding UTF-8 -d tests/bin -cp "hexa/bin;tests/lib/junit-platform-console-standalone.jar" tests/hexa/*.java, java -jar tests/lib/junit-platform-console-standalone.jar execute -cp "hexa/bin;tests/bin" --scan-classpath
- 빌드: javac -d bin --source-path src src/hexa/Main.java
- 초기화: 6장부터 hexa/data 폴더의 저장 파일 지우기

## 캡처

| 종류 | 주소나 명령 |
|---|---|
| run | cd hexa, javac -d bin --source-path src src/hexa/Main.java, java -cp bin hexa.Main |

## 이클립스

- hexa 폴더를 프로젝트로 연다
- Main 클래스를 열고 Run을 누른다
