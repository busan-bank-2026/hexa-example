# hexagon-packages 계획

## 파일

| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| hexa/src/hexa/domain/Book.java | 옮김 | 도메인. hexa/src/hexa/Book.java에서 옮긴다 |
| hexa/src/hexa/application/LoanService.java | 옮김 | 서비스. hexa/src/hexa/LoanService.java에서 옮긴다 |
| hexa/src/hexa/application/port/in/LoanUseCase.java | 옮김 | 들어오는 포트. hexa/src/hexa/LoanUseCase.java에서 옮긴다 |
| hexa/src/hexa/application/port/out/BookPort.java | 옮김 | 나가는 포트. hexa/src/hexa/BookPort.java에서 옮긴다 |
| hexa/src/hexa/adapter/in/ConsoleMenu.java | 옮김 | 드라이빙 어댑터. hexa/src/hexa/ConsoleMenu.java에서 옮긴다 |
| hexa/src/hexa/adapter/out/MemoryBookAdapter.java | 옮김 | 드리븐 어댑터. hexa/src/hexa/MemoryBookAdapter.java에서 옮긴다 |
| hexa/src/hexa/Main.java | 고침 | 옮긴 클래스를 import한다 |
| hexa/src/hexa/LoanCheck.java | 고침 | 옮긴 클래스를 import한다 |
| tests/hexa/HexagonPackagesTest.java | 새로 | S1, S2, S3, S4 검증용 행동 테스트 |

## 코드 계획

나누는 이유
- domain: 대출 규칙이다. 바깥 어느 것도 모른다
- application: 유스케이스와 그것이 바깥에 바라는 포트다. 도메인만 안다
- application.port.in, port.out: 들어오는 쪽과 나가는 쪽 포트를 나눠 방향을 이름으로 보인다
- adapter.in, adapter.out: 포트를 부르는 쪽과 포트를 구현하는 쪽이다
- hexa: 입구다. 모든 것을 만들어 끼운다

옮기는 법
- 클래스 내용은 그대로다. package 줄과 import 줄만 바뀐다
- 모든 클래스와 쓰는 생성자, 메서드가 이미 public이라 public을 더할 곳은 없다
- 한 task에서 옮긴 클래스를 쓰는 다른 파일은 같은 task에서 import를 더한다

LoanCheck
- hexa 패키지에 남긴다. 어댑터와 서비스를 직접 만들어 끼우는 입구라서 Main과 같은 자리다
- adapter.in에 두면 어댑터가 다른 어댑터와 서비스 구현을 알게 된다

T1 도메인
- Book.java를 domain으로 옮긴다
- LoanService, LoanUseCase, BookPort, ConsoleMenu, MemoryBookAdapter, LoanCheck에 import hexa.domain.Book을 더한다

T2 애플리케이션
- LoanService를 application, LoanUseCase를 application.port.in, BookPort를 application.port.out으로 옮긴다
- LoanService는 두 포트를 import한다
- ConsoleMenu, MemoryBookAdapter, Main, LoanCheck에 쓰는 것만 import를 더한다

T3 어댑터
- ConsoleMenu를 adapter.in, MemoryBookAdapter를 adapter.out으로 옮긴다
- Main과 LoanCheck에 두 어댑터 import를 더한다

HexagonPackagesTest.java
- 검증용이다. 패키지는 Find로 찾은 클래스의 getPackageName으로 본다
- 의존 방향은 hexa/src 소스 파일의 import hexa. 줄로 본다
- S1 Book이 hexa.domain에 있고 domain 파일에 import hexa.가 없다
- S2 LoanService, LoanUseCase, BookPort가 제 패키지에 있고 application 파일은 hexa.domain과 hexa.application만 import한다
- S3 ConsoleMenu, MemoryBookAdapter가 제 패키지에 있고 adapter 파일은 hexa.domain과 hexa.application.port만 import한다
- S4 hexa 바로 아래 파일은 Main.java와 LoanCheck.java뿐이고 둘 다 adapter와 application을 import한다

## 핵심 코드

package hexa.domain;

package hexa.application;

import hexa.application.port.in.LoanUseCase;
import hexa.application.port.out.BookPort;
import hexa.domain.Book;

package hexa;

import hexa.adapter.in.ConsoleMenu;
import hexa.adapter.out.MemoryBookAdapter;
import hexa.application.LoanService;
import hexa.application.port.in.LoanUseCase;
import hexa.application.port.out.BookPort;

## 할 일 연결

| 할 일 | 파일 |
|---|---|
| S1 | domain/Book.java와 Book을 쓰는 파일 |
| S2 | application/LoanService.java, application/port/in/LoanUseCase.java, application/port/out/BookPort.java와 이들을 쓰는 파일 |
| S3 | adapter/in/ConsoleMenu.java, adapter/out/MemoryBookAdapter.java, Main.java, LoanCheck.java |
| S4 | Main.java, LoanCheck.java |

## 라이브러리

- 추가 없음

## 다른 장 파일

- hexa/src/hexa/Book.java: layered-loan
- hexa/src/hexa/LoanService.java: layered-loan, use-case-port, store-port
- hexa/src/hexa/LoanUseCase.java: use-case-port
- hexa/src/hexa/BookPort.java: store-port
- hexa/src/hexa/ConsoleMenu.java: use-case-port
- hexa/src/hexa/MemoryBookAdapter.java: store-port
- hexa/src/hexa/Main.java: project-setup, layered-loan, use-case-port, store-port
- hexa/src/hexa/LoanCheck.java: use-case-port, store-port
