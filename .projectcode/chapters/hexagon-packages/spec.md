# hexagon-packages 육각형 패키지

종류: 리팩터
배울 것: domain, application, adapter 패키지, 의존 방향, Main 조립

## 먼저 볼 문제

모든 클래스가 hexa 패키지 한곳에 있다
- 무엇이 도메인이고 무엇이 포트이고 무엇이 어댑터인지 폴더만 봐서는 모른다
- 누가 누구를 알아도 되는지 드러나지 않는다

## 할 일

- S1 책을 domain 패키지로 옮긴다. 도메인은 다른 패키지를 모른다
- S2 서비스와 두 포트를 application 패키지로 옮긴다. 들어오는 포트는 port.in, 나가는 포트는 port.out에 둔다. application은 domain만 안다
- S3 콘솔 메뉴와 메모리 저장을 adapter 패키지로 옮긴다. 콘솔 메뉴는 adapter.in, 메모리 저장은 adapter.out에 둔다. 어댑터는 포트와 도메인만 안다
- S4 Main과 두 번째 입구는 hexa 패키지에 남아 어댑터와 서비스를 만들어 끼운다. 모든 패키지를 아는 것은 이 두 입구뿐이다

## 의존 방향

```
입구(Main, 두 번째 입구) -> adapter -> application -> domain
```

## 그대로인 것

- 콘솔 메뉴와 두 번째 입구의 동작과 글은 store-port와 같다
- 실행은 그대로 hexa.Main이다
- 대출 중인 책을 다시 대출하면 오류로 멈춘다

## 안 하는 것

- 의존 방향을 테스트로 지키기: dependency-check 장
- 파일 저장 어댑터: file-store 장
