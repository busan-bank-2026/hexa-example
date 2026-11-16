# project-setup 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/Main.java | 새로 | 시작 글을 콘솔에 보여준다 |
| hexa/.project | 새로 | 이클립스 프로젝트 이름 hexa |
| hexa/.classpath | 새로 | 소스 src, JRE JavaSE-21, 출력 bin |
| hexa/.settings/org.eclipse.jdt.core.prefs | 새로 | Java 21로 컴파일하고 공백 4칸으로 포맷한다 |
| hexa/.settings/org.eclipse.core.resources.prefs | 새로 | 파일 인코딩을 UTF-8로 정한다 |
| hexa/src/.gitkeep | 새로 | 빈 src 폴더를 git에 남긴다. T1 시점에 이클립스가 src를 찾는다 |
| tests/lib/junit-platform-console-standalone.jar | 새로 | 검증용 JUnit 5 실행기. ddd-example에서 복사한다 |
| tests/project-setup/ProjectSetupTest.java | 새로 | S1, S2 행동 테스트 |
| .gitignore | 고침 | hexa/bin, tests/bin을 올리지 않는다 |

## 코드 계획

Main.java
- 패키지는 hexa다
- main 메서드 하나에서 시작 글 한 줄을 출력한다

.project
- 이름은 hexa, Java 빌더와 Java 성격만 넣는다

.classpath
- 소스 폴더 src, JRE JavaSE-21, 출력 bin만 넣는다
- JUnit과 tests 링크는 넣지 않는다. 학생 프로젝트는 Main 실행만 한다

.settings
- jdt.core.prefs는 Java 21로 컴파일하고 포맷을 공백 4칸으로 맞춘다
- resources.prefs는 프로젝트 인코딩을 UTF-8로 정한다. 작업공간 인코딩이 MS949여도 한글이 깨지지 않는다

ProjectSetupTest.java
- 검증용이다. 학생에게 주지 않고 책에 넣지 않는다
- System.out을 잠시 바꿔 Main.main의 출력을 모은다
- S1 출력에 도서 대출 프로그램을 시작합니다가 들어 있는지 확인한다
- S2 이클립스 파일에 프로젝트 이름 hexa, src, Java 21, UTF-8이 있는지 확인한다
- 파일 경로는 저장소 루트 기준으로 찾는다

포맷
- 이클립스 포맷터를 공백 4칸으로 맞춘다. lint 도구는 두지 않는다

## 핵심 코드

public static void main(String[] args) {
    System.out.println("도서 대출 프로그램을 시작합니다");
}

## 검증 명령

저장소 루트에서 실행한다.
javac -encoding UTF-8 -d hexa/bin --source-path hexa/src hexa/src/hexa/Main.java
javac -encoding UTF-8 -d tests/bin -cp "hexa/bin;tests/lib/junit-platform-console-standalone.jar" tests/*/*.java
java -jar tests/lib/junit-platform-console-standalone.jar execute -cp "hexa/bin;tests/bin" --scan-classpath

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | Main.java |
| S2 | .project, .classpath, org.eclipse.jdt.core.prefs, org.eclipse.core.resources.prefs, src/.gitkeep |

## 라이브러리

- 추가 없음. contract의 JUnit 5를 검증용으로만 쓴다

## 다른 장 파일

- 없음
