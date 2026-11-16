# 폴더 구조

비교 기준: done/overdue-notice. 새로 생긴 파일과 고친 파일을 표시했다.

```
hexa/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  lib/
    junit-platform-console-standalone.jar   새로
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
      DependencyRuleTest.java   새로
  .classpath   고침
  .project
```

