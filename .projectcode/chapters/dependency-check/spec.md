# dependency-check 의존 방향 검사

종류: 기능
배울 것: 의존 규칙을 테스트로 지키기

## 먼저 볼 문제

도메인이 어댑터를 import해도 아무도 모른다
- Book에 어댑터 import를 한 줄 넣어도 컴파일되고 Main이 돈다
- 의존 방향은 사람이 눈으로 지키고 있다

## 할 일

- S1 hexa 프로젝트에 JUnit을 붙인다. lib 폴더의 JUnit jar와 test 소스 폴더를 더해 이클립스에서 Run As, JUnit Test로 학생 테스트를 돌린다
- S2 의존 규칙 테스트가 domain 소스를 읽어 application이나 adapter를 import하면 실패한다
- S3 의존 규칙 테스트가 application 소스를 읽어 adapter를 import하면 실패한다
- S4 의존 규칙 테스트가 어댑터끼리 import하면 실패한다. adapter.in은 adapter.out을, adapter.out은 adapter.in을 모른다

## 실행 예시

지금 코드에서 돌리면

```
DependencyRuleTest ✔
├─ adaptersDoNotKnowEachOther() ✔
├─ domainKnowsOnlyDomain() ✔
└─ applicationDoesNotKnowAdapter() ✔
3 tests successful
```

Book에 import hexa.adapter.out.MemoryBookAdapter;를 넣고 돌리면

```
domainKnowsOnlyDomain() ✘ import hexa.adapter.out.MemoryBookAdapter; ==> expected: <false> but was: <true>
2 tests successful
1 tests failed
```

## 흐름

Run As, JUnit Test -> 패키지 폴더의 소스 읽기 -> import 줄 모으기 -> 규칙에 어긋난 줄이 있으면 빨간불

## 그대로인 것

- 대출, 반납, 목록, 연체 알림과 두 번째 입구는 overdue-notice와 같다
- 학생 코드 src는 바뀌지 않는다

## 안 하는 것

- ArchUnit 같은 아키텍처 검사 라이브러리: 안 만듦. 내려받아야 한다
- 도메인 동작 단위 테스트: 안 만듦. 이 장은 의존 규칙만 본다
- import 없이 전체 이름으로 쓴 클래스 찾기: 안 만듦
