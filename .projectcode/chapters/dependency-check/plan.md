# dependency-check 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/lib/junit-platform-console-standalone.jar | 새로 | tests/lib의 JUnit jar를 복사한다. 붙여넣기다 |
| hexa/.classpath | 고침 | test 소스 폴더와 lib의 jar를 더한다 |
| hexa/test/hexa/DependencyRuleTest.java | 새로 | 학생이 쓰는 의존 규칙 테스트 |
| tests/hexa/DependencyCheckTest.java | 새로 | S1부터 S4까지 검증용 행동 테스트 |

## 코드 계획

나누는 이유
- test 폴더: 학생 테스트를 src와 나눠 둔다. 프로그램 코드와 그 코드를 검사하는 코드를 섞지 않는다
- importsIn: 규칙마다 같은 일(폴더의 java 파일에서 import hexa. 줄 모으기)을 한다. 한 번만 쓴다

.classpath
- classpathentry kind="src" path="test"를 src 다음에 더한다
- classpathentry kind="lib" path="lib/junit-platform-console-standalone.jar"를 더한다

DependencyRuleTest.java
- package hexa. 클래스는 public 없이 쓴다
- 새로 배우는 것은 @Test와 assertFalse 둘이다
- importsIn(folder)는 Files.walk로 폴더의 .java 파일을 돌며 import hexa.로 시작하는 줄을 모은다
- 폴더는 이클립스 실행 폴더인 hexa 기준 src/hexa/domain처럼 쓴다
- domainKnowsOnlyDomain: domain의 import가 hexa.application.이나 hexa.adapter.로 시작하면 실패한다
- applicationDoesNotKnowAdapter: application의 import가 hexa.adapter.로 시작하면 실패한다
- adaptersDoNotKnowEachOther: adapter/in은 hexa.adapter.out.을, adapter/out은 hexa.adapter.in.을 import하면 실패한다
- assertFalse의 두 번째 칸에 그 import 줄을 넘겨 빨간불에 어긴 줄이 보이게 한다

DependencyCheckTest.java
- 검증용이다. 학생 테스트를 학생처럼 돌려 본다
- 학생 테스트를 임시 폴더에 컴파일하고 java -jar hexa/lib의 jar execute --select-class hexa.DependencyRuleTest로 돌린다
- 깨 보기는 hexa/src를 임시 폴더에 복사하고 그 복사본에 import 한 줄을 넣어 돌린다. 학생 코드는 건드리지 않는다
- S1 hexa/lib에 jar가 있고 .classpath에 test 폴더와 jar가 있다. 학생 테스트에 @Test가 있고 hexa 폴더에서 돌리면 통과한다
- S2 지금 코드에서 통과한다. Book에 hexa.adapter.out import나 hexa.application import를 넣으면 실패하고 그 줄이 나온다
- S3 LoanService에 hexa.adapter.out import를 넣으면 실패한다
- S4 ConsoleMenu에 hexa.adapter.out import, FileBookAdapter에 hexa.adapter.in import를 넣으면 실패한다

## 핵심 코드

@Test
void domainKnowsOnlyDomain() throws IOException {
    for (String line : importsIn("src/hexa/domain")) {
        assertFalse(line.startsWith("import hexa.application."), line);
        assertFalse(line.startsWith("import hexa.adapter."), line);
    }
}

<classpathentry kind="src" path="test"/>
<classpathentry kind="lib" path="lib/junit-platform-console-standalone.jar"/>

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | junit-platform-console-standalone.jar, .classpath, DependencyRuleTest.java |
| S2 | DependencyRuleTest.java |
| S3 | DependencyRuleTest.java |
| S4 | DependencyRuleTest.java |

## 라이브러리

- 추가 없음. contract.md의 JUnit 5 jar를 hexa에 붙인다. 학생 테스트만 쓰고 학생 코드 src는 쓰지 않는다

## 다른 장 파일

- hexa/.classpath: project-setup
