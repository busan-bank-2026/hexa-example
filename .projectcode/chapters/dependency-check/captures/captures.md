# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-도메인-규칙.txt | dependency-check | T1 | 70daf56 | 학생 테스트 실행 두 번 | 처음에는 domainKnowsOnlyDomain이 초록불이고 1 tests successful이다. Book에 어댑터 import를 넣고 다시 돌리면 빨간불에 import hexa.adapter.out.MemoryBookAdapter; 줄이 보인다 |
| T2-세-규칙.txt | dependency-check | T2 | 2ffe037 | 학생 테스트 실행 두 번 | 처음에는 규칙 테스트 세 개가 초록불이고 3 tests successful이다. LoanService에 어댑터 import를 넣고 다시 돌리면 applicationDoesNotKnowAdapter만 빨간불이다 |
| done-규칙-통과.txt | dependency-check | done | 2ffe037 | 학생 테스트 실행 | spec 실행 예시처럼 규칙 테스트 세 개가 초록불이고 3 tests successful이다 |
| done-깨-보기.txt | dependency-check | done | 2ffe037 | Main 실행과 학생 테스트 실행 | Book에 어댑터 import를 넣어도 Main은 컴파일되고 메뉴가 돈다. 학생 테스트는 domainKnowsOnlyDomain이 빨간불이고 2 tests successful, 1 tests failed다 |
