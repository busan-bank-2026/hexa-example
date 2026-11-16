# 폴더 구조

기준 장: dependency-check

```
hexa/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  lib/
    junit-platform-console-standalone.jar
  src/
    hexa/
      adapter/
        in/
          ConsoleMenu.java
        out/
          ConsoleNotifyAdapter.java
          FileBookAdapter.java
          FileNotifyAdapter.java
          FixedClockAdapter.java
          MemoryBookAdapter.java
          SystemClockAdapter.java
      application/
        port/
          in/
            BookView.java
            BorrowCommand.java
            LoanUseCase.java
          out/
            BookPort.java
            ClockPort.java
            NotifyPort.java
        LoanService.java
      domain/
        Book.java
      LoanCheck.java
      Main.java
    .gitkeep
  test/
    hexa/
      DependencyRuleTest.java
  .classpath
  .project
```
