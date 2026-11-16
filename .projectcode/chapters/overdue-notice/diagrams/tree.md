# 폴더 구조

비교 기준: done/due-date. 새로 생긴 파일과 고친 파일을 표시했다.

```
hexa/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    hexa/
      adapter/
        in/
          ConsoleMenu.java   고침
        out/
          ConsoleNotifyAdapter.java   새로
          FileBookAdapter.java
          FileNotifyAdapter.java   새로
          FixedClockAdapter.java
          MemoryBookAdapter.java
          SystemClockAdapter.java
      application/
        port/
          in/
            BookView.java
            BorrowCommand.java
            LoanUseCase.java   고침
          out/
            BookPort.java
            ClockPort.java
            NotifyPort.java   새로
        LoanService.java   고침
      domain/
        Book.java
      LoanCheck.java   고침
      Main.java   고침
    .gitkeep
  .classpath
  .project
```

