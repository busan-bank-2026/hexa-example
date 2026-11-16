# 프로젝트 규칙

## 기술

| 항목 | 선택 |
|---|---|
| 언어 | Java 21 |
| IDE | 이클립스. hexa에 .project와 .classpath를 넣는다 |
| 빌드 도구 | 없음 |
| 학생 코드 라이브러리 | 없음 |
| 저장소 | HashMap. 6장부터 파일. 13장만 Oracle XE 21c |
| 테스트 | JUnit 5 (hexa/lib/junit-platform-console-standalone.jar) |
| DB 드라이버 | 13장만 ojdbc11.jar (오라클 설치 폴더에서 복사) |
| 파일 형식 | 한 줄에 하나. 쉼표로 나눈 글자 |
| 환경 | 폐쇄망. 인터넷에서 받는 것을 쓰지 않는다 |

## 폴더

| 무엇 | 폴더 |
|---|---|
| 학생 코드 | hexa |
| 행동 테스트 | tests |
| jar | hexa/lib. JUnit jar, 13장의 ojdbc jar |
| 컴파일 결과 | hexa/bin. git에 올리지 않는다 |

## 실행

- 실행: cd hexa, javac -d bin --source-path src src/hexa/Main.java, java -cp bin hexa.Main
- 테스트: tests를 bin-test로 컴파일한 뒤 java -jar hexa/lib/junit-platform-console-standalone.jar로 실행한다
- 빌드: javac -d bin --source-path src src/hexa/Main.java
- 초기화: 6장부터 hexa/data 폴더의 저장 파일 지우기

## 캡처

| 종류 | 주소나 명령 |
|---|---|
| run | cd hexa, javac -d bin --source-path src src/hexa/Main.java, java -cp bin hexa.Main |

## 이클립스

- hexa 폴더를 프로젝트로 연다
- Main 클래스를 열고 Run을 누른다
- 테스트는 테스트 파일에서 Run As, JUnit Test를 고른다
