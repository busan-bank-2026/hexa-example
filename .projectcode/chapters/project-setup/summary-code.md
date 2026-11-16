# project-setup 코드 요약

## T1 이클립스 프로젝트 만들기
커밋: 5847823

| 파일 | 변경 |
|---|---|
| hexa/.project | 새로 |
| hexa/.classpath | 새로 |
| hexa/.settings/org.eclipse.jdt.core.prefs | 새로 |
| hexa/.settings/org.eclipse.core.resources.prefs | 새로 |
| hexa/src/.gitkeep | 새로 |

```xml
<!-- hexa/.project -->
<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
	<name>hexa</name>
	<comment></comment>
	<projects>
	</projects>
	<buildSpec>
		<buildCommand>
			<name>org.eclipse.jdt.core.javabuilder</name>
			<arguments>
			</arguments>
		</buildCommand>
	</buildSpec>
	<natures>
		<nature>org.eclipse.jdt.core.javanature</nature>
	</natures>
</projectDescription>
```

```xml
<!-- hexa/.classpath -->
<?xml version="1.0" encoding="UTF-8"?>
<classpath>
    <classpathentry kind="src" path="src"/>
    <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-21"/>
    <classpathentry kind="output" path="bin"/>
</classpath>
```

```
# hexa/.settings/org.eclipse.jdt.core.prefs
eclipse.preferences.version=1
org.eclipse.jdt.core.compiler.codegen.targetPlatform=21
org.eclipse.jdt.core.compiler.compliance=21
org.eclipse.jdt.core.compiler.release=enabled
org.eclipse.jdt.core.compiler.source=21
org.eclipse.jdt.core.formatter.tabulation.char=space
org.eclipse.jdt.core.formatter.tabulation.size=4
org.eclipse.jdt.core.formatter.indentation.size=4
```

```
# hexa/.settings/org.eclipse.core.resources.prefs
eclipse.preferences.version=1
encoding/<project>=UTF-8
```

hexa/src/.gitkeep은 빈 파일이다. 빈 src 폴더를 git에 남긴다

## T2 시작 글 보여주기
커밋: 992490f

| 파일 | 변경 |
|---|---|
| hexa/src/hexa/Main.java | 새로 |

```java
// hexa/src/hexa/Main.java
package hexa;

public class Main {

    public static void main(String[] args) {
        System.out.println("도서 대출 프로그램을 시작합니다");
    }
}
```

## 테스트 보강
커밋: 4639bef
- 학생 코드 변경 없음
