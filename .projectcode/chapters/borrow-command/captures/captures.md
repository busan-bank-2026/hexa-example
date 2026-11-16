# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-아직-빈-회원.txt | borrow-command | T1 | 6ac2d39 | 대출 목록 콘솔과 저장 파일 | 대출 요청을 만들어 포트를 불러도 결과 글은 그대로다. 회원 번호에 빈칸 하나를 넣고 B2를 대출하면 아직 클린 아키텍처 대출했습니다가 나오고 목록에 B2 대출 중, 저장 파일에 B2,클린 아키텍처, 뒤 빈칸 회원이 남는다 |
| T2-빈-값-막기.txt | borrow-command | T2 | 0892c0b | 대출 목록 콘솔과 저장 파일 | 같은 입력에 대출했습니다가 나오지 않고 IllegalArgumentException 회원 번호가 비었습니다로 멈춘다. 저장 파일의 B2 줄은 회원 칸이 비어 있다 |
| done-빈-회원-번호.txt | borrow-command | done | 0892c0b | 대출 콘솔 | spec 실행 예시처럼 회원 번호를 비우고 B1을 대출하면 IllegalArgumentException 회원 번호가 비었습니다로 멈춘다. 저장 파일의 세 책은 모두 회원 칸이 비어 있다 |
| done-두-번째-입구.txt | borrow-command | done | 0892c0b | LoanCheck 콘솔 | book-list 장 끝 사진과 같게 대출과 반납 두 줄이 나온다. 두 번째 입구는 대출 요청을 만들어 포트를 부른다 |
