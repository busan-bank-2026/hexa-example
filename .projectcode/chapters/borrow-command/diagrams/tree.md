# 폴더 구조

비교 기준: done/book-list. 새로 생긴 파일과 고친 파일을 표시했다.

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
          FileBookAdapter.java
          MemoryBookAdapter.java
      application/
        port/
          in/
            BookView.java
            BorrowCommand.java   새로
            LoanUseCase.java   고침
          out/
            BookPort.java
        LoanService.java   고침
      domain/
        Book.java
      LoanCheck.java   고침
      Main.java
    .gitkeep
  .classpath
  .project
```

