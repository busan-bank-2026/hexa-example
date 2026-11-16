# 폴더 구조

비교 기준: done/loan-errors. 새로 생긴 파일과 고친 파일을 표시했다.

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
          FileBookAdapter.java   고침
          FixedClockAdapter.java   새로
          MemoryBookAdapter.java
          SystemClockAdapter.java   새로
      application/
        port/
          in/
            BookView.java   고침
            BorrowCommand.java
            LoanUseCase.java
          out/
            BookPort.java
            ClockPort.java   새로
        LoanService.java   고침
      domain/
        Book.java   고침
      LoanCheck.java   고침
      Main.java   고침
    .gitkeep
  .classpath
  .project
```

