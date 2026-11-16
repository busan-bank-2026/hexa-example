# 따라치기 코드 리뷰

## project-setup T1

### .classpath

#### 개념 이클립스 프로젝트
- 이클립스는 폴더 안의 .project와 .classpath를 보고 그 폴더를 자바 프로젝트로 연다
- .project는 프로젝트 이름(hexa)과 자바 프로젝트라는 표시를 담는다
- .classpath는 소스 폴더(src), 쓰는 JDK, 컴파일 결과 폴더(bin)를 정한다
- .settings의 두 파일은 글자 인코딩 UTF-8과 자바 21 컴파일 설정이다
- 빌드 도구(Maven, Gradle)가 없어서 이 파일들이 프로젝트 설정의 전부다
- 이 장은 붙여넣기 장이다. 네 파일을 복사 버튼으로 그대로 붙인다
- 다 붙였으면 이클립스에서 hexa 폴더를 프로젝트로 연다

#### 코드
- kind="src" path="src"는 src 폴더의 자바 파일을 컴파일하라는 뜻이다
- kind="con"은 JavaSE-21 JDK를 쓴다는 뜻이다
- kind="output" path="bin"은 컴파일한 class 파일을 bin에 둔다는 뜻이다. bin은 git에 올리지 않는다

### .settings/org.eclipse.core.resources.prefs
- encoding/<project>=UTF-8은 한글이 깨지지 않게 모든 파일을 UTF-8로 읽고 쓰게 한다

### .settings/org.eclipse.jdt.core.prefs
- compliance와 source 21은 자바 21 문법(record 등)으로 컴파일하게 한다
- tabulation.char=space는 들여쓰기를 빈칸 4개로 맞춘다

## project-setup T2

### src/hexa/Main.java

#### 개념 폴더 구조
- 자바는 패키지 이름이 폴더 이름과 같아야 한다. package hexa면 src/hexa 폴더에 둔다
- 이 예제는 모든 코드를 hexa 패키지 아래에 둔다. 5장에서 hexa 아래를 domain, application, adapter로 나눈다
- main 메서드가 있는 클래스가 프로그램의 입구다
- 이클립스에서 Main을 열고 Run을 누르면 콘솔 창에 글이 나온다

#### 코드
- System.out.println은 콘솔에 한 줄을 찍는다

## layered-loan T1

### src/hexa/Main.java
- Scanner(System.in)는 콘솔 입력을 읽는다. nextLine은 엔터까지 한 줄을 글자로 준다
- while (true)는 0을 고를 때까지 메뉴를 되풀이한다. break가 반복을 끝낸다
- 메뉴에 없는 번호는 아무 if에도 걸리지 않아 메뉴가 다시 나온다
- 글자 비교는 == 대신 equals를 쓴다

## layered-loan T2

### src/hexa/Book.java
- borrowerId는 빌린 회원 번호다. null이면 아무도 빌리지 않은 책이다
- id와 title은 final이라 만든 뒤 바뀌지 않는다. borrowerId는 대출과 반납으로 바뀐다

### src/hexa/BookRepository.java
- HashMap에 책 번호를 키로 책을 담는다. 프로그램을 끄면 사라지는 메모리 저장이다
- 생성자에서 처음 책 3권을 넣는다

### src/hexa/LoanService.java

#### 개념 계층형 코드
- 화면(Main), 서비스(LoanService), 저장(BookRepository)을 위에서 아래로 쌓은 구조다
- 위 계층이 아래 계층을 직접 만들고 부른다. 이 예제의 출발점이다
- 지금 서비스는 콘솔에서 직접 읽고 찍는다. 그래서 콘솔 없이는 대출을 실행해 볼 수 없다
- 서비스가 저장 클래스를 필드에서 new로 만든다. 그래서 저장 방식을 바꾸려면 서비스를 고쳐야 한다
- 3장부터 이 두 문제를 포트와 어댑터로 하나씩 고친다

#### 코드
- 생성자로 Main의 Scanner를 받는다. Scanner를 두 개 만들면 입력이 엉킨다
- 책을 찾고, 대출하고, 저장하고, 결과를 찍는 순서가 메서드 하나에 모두 있다

## layered-loan T3

### src/hexa/Book.java
- 이미 빌린 책이면 IllegalStateException을 던진다. 지금 상태로는 할 수 없는 일에 쓰는 표준 예외다
- 이 규칙은 책의 상태를 아는 Book에 둔다
- 아직 예외를 잡는 곳이 없어 프로그램이 멈춘다. 9장에서 메시지로 바꾼다

## layered-loan T4

### src/hexa/Book.java
- return은 자바 예약어라 메서드 이름으로 쓸 수 없다. 그래서 returnBook이다

### src/hexa/LoanService.java
- 반납도 찾기, 바꾸기, 저장, 찍기 순서다. 콘솔 입력이 여전히 서비스 안에 있다

## use-case-port T1

### src/hexa/ConsoleMenu.java

#### 개념 드라이빙 어댑터
- 바깥에서 들어와 애플리케이션을 움직이는 쪽이다. 사람의 입력을 받아 서비스를 부른다
- 콘솔, 웹 요청, 테스트처럼 입구는 여러 개일 수 있다
- 앞 단계 Main에는 메뉴 반복이 들어 있었다. 입구 하나가 프로그램 시작과 섞여 있었다
- 이번 단계는 메뉴를 클래스로 옮기기만 한다. 서비스에서 콘솔을 떼는 일은 다음 단계다
- 이 예제에서는 ConsoleMenu가 드라이빙 어댑터다

#### 코드
- run 메서드가 앞 단계 Main의 while 반복을 그대로 가져왔다

### src/hexa/Main.java
- Main은 메뉴를 만들고 run을 부르기만 한다

## use-case-port T2

### src/hexa/ConsoleMenu.java
- 콘솔에서 읽고 찍는 일을 서비스에서 가져왔다. 회원 번호와 책 번호를 읽어 서비스에 넘긴다
- 서비스가 돌려준 책의 제목으로 결과 글을 찍는다
- 서비스를 생성자로 받는다. 무엇을 넘길지는 Main이 정한다

### src/hexa/LoanService.java
- Scanner와 System.out이 사라졌다. 서비스는 받은 값으로 일하고 결과를 돌려준다
- 그래서 콘솔이 아닌 곳에서도 borrow("M1", "B1")처럼 부를 수 있다

## use-case-port T3

### src/hexa/LoanUseCase.java

#### 개념 들어오는 포트
- 애플리케이션이 바깥에 열어 주는 기능 목록이다. 자바에서는 interface로 쓴다
- 드라이빙 어댑터는 서비스 클래스 대신 이 포트만 안다
- 앞 단계 ConsoleMenu는 LoanService를 직접 알았다. 서비스 안의 다른 public 메서드까지 부를 수 있었다
- 유스케이스는 사용자가 하려는 일 하나(대출, 반납)다. 포트는 그 일을 부르는 자리다
- 이 예제에서는 LoanUseCase가 들어오는 포트고 LoanService가 그 구현이다

### src/hexa/LoanService.java
- implements LoanUseCase는 포트에 적힌 메서드를 모두 만든다는 약속이다
- @Override는 포트의 메서드를 구현한 것임을 컴파일러가 확인하게 한다. 이름을 틀리면 오류가 난다

### src/hexa/ConsoleMenu.java
- 필드와 생성자 타입이 LoanService에서 LoanUseCase로 바뀌었다. 메뉴는 이제 포트만 안다

### src/hexa/Main.java
- 변수 타입은 포트, 만드는 것은 서비스다. 포트에 어떤 구현을 끼울지 Main이 정한다

## use-case-port T4

### src/hexa/LoanCheck.java

#### 개념 테스트도 입구
- 포트를 부를 수 있으면 콘솔이 아닌 입구도 만들 수 있다
- LoanCheck는 콘솔 입력 없이 포트를 불러 대출과 반납을 해 보는 두 번째 입구다
- 메뉴를 띄우고 번호를 치지 않아도 서비스를 실행해 볼 수 있다. 3장 처음의 문제가 풀렸다
- 테스트 코드도 같은 자리에 선다. 포트를 불러 결과를 확인하는 또 하나의 드라이빙 어댑터다
- 이클립스에서 LoanCheck를 열고 Run하면 두 줄이 찍힌다

## store-port T1

### src/hexa/BookPort.java

#### 개념 나가는 포트
- 애플리케이션이 바깥에 시키고 싶은 일을 적은 interface다. 여기서는 책 찾기와 저장이다
- 들어오는 포트는 바깥이 안을 부르는 자리고, 나가는 포트는 안이 바깥을 부르는 자리다
- 앞 단계 서비스는 BookRepository 클래스를 직접 알았다. 저장 방식을 바꾸면 서비스가 바뀌었다
- 나가는 포트를 쓰면 서비스는 무엇에 저장하는지 모르고 findById와 save만 안다
- 이 예제에서는 BookPort가 나가는 포트다. 10장 시계 포트, 11장 알림 포트도 같은 모양이다

### src/hexa/BookRepository.java
- implements BookPort로 지금 저장 클래스가 포트를 구현하게 했다. 이름은 이 장 마지막 단계에서 바꾼다

### src/hexa/LoanService.java
- 필드 타입이 BookPort가 되었다. 아직 필드에서 new BookRepository()로 직접 만든다

## store-port T2

### src/hexa/LoanService.java
- new가 사라지고 생성자로 BookPort를 받는다. 서비스는 이제 저장 클래스 이름을 모른다
- 필요한 것을 밖에서 받는 이 방식을 의존성 주입이라 한다

### src/hexa/Main.java
- 무엇에 저장할지는 Main이 정해 서비스에 넘긴다

### src/hexa/LoanCheck.java
- 두 번째 입구도 스스로 저장을 골라 넘긴다

## store-port T3

### src/hexa/MemoryBookAdapter.java

#### 개념 드리븐 어댑터
- 나가는 포트를 구현해 실제로 바깥 기술을 다루는 클래스다. 애플리케이션이 부르면 움직인다
- 드라이빙 어댑터는 애플리케이션을 부르고, 드리븐 어댑터는 애플리케이션에 불린다
- 앞 단계 BookRepository는 저장소라는 이름만 있고 어떤 저장인지 드러나지 않았다
- 이름에 기술(Memory)과 역할(Adapter)을 넣으면 다른 저장 어댑터와 나란히 둘 수 있다
- 이 예제에서는 MemoryBookAdapter가 첫 드리븐 어댑터다. 6장에서 FileBookAdapter를 더한다

#### 코드
- 내용은 BookRepository와 같고 클래스 이름만 바뀌었다
- 이클립스에서 BookRepository를 Refactor, Rename으로 바꾸면 쓰는 곳도 함께 바뀐다

## hexagon-packages T1

### src/hexa/domain/Book.java

#### 개념 domain 패키지
- 육각형 가운데에 업무 규칙만 담는 자리다. 대출 중이면 거절한다 같은 규칙이 여기 있다
- 앞 단계까지는 모든 클래스가 hexa 패키지 하나에 있어 누가 도메인인지 폴더로 알 수 없었다
- 도메인은 다른 패키지를 import하지 않는다. 콘솔, 파일, 포트가 바뀌어도 도메인은 그대로다
- 이 예제에서는 Book 하나가 도메인이다

#### 코드
- 첫 줄 package hexa.domain과 폴더 src/hexa/domain이 같아야 한다
- 이클립스에서 Book.java를 domain 패키지로 끌어 옮기면(Refactor, Move) package 줄과 import를 고쳐 준다
- 다른 패키지에서 쓰려면 클래스와 메서드가 public이어야 한다. Book은 이미 public이다

### src/hexa/ConsoleMenu.java
- 다른 패키지의 클래스는 import hexa.domain.Book처럼 불러야 쓸 수 있다

## hexagon-packages T2

### src/hexa/application/LoanService.java

#### 개념 application 패키지와 의존 방향
- application은 유스케이스를 해내는 서비스와 두 종류 포트를 담는다
- 들어오는 포트는 port.in, 나가는 포트는 port.out에 둔다. 폴더만 봐도 방향이 보인다
- application은 domain만 안다. 어댑터 이름은 import하지 않는다
- 의존 방향은 입구 -> adapter -> application -> domain 한쪽이다. 안쪽은 바깥을 모른다
- 그래서 바깥 기술을 바꿔도 안쪽 코드는 그대로다
- 12장에서 이 방향을 테스트로 지킨다

#### 코드
- import는 java 묶음 뒤에 빈 줄, 그 아래 hexa 묶음을 알파벳순으로 둔다. 이클립스 Source, Organize Imports와 같은 순서다

### src/hexa/application/port/in/LoanUseCase.java
- package hexa.application.port.in이라 폴더도 application/port/in이다

## hexagon-packages T3

### src/hexa/adapter/in/ConsoleMenu.java

#### 개념 adapter 패키지
- 바깥 기술을 다루는 코드를 모은다. 들어오는 쪽은 adapter.in, 나가는 쪽은 adapter.out이다
- 어댑터는 포트와 도메인을 알고, 다른 어댑터는 모른다
- ConsoleMenu는 adapter.in, MemoryBookAdapter는 adapter.out에 둔다
- 서비스 쪽 코드는 바뀌지 않는다. 바깥이 안쪽을 아는 방향이라서다

### src/hexa/Main.java

#### 개념 Main 조립
- 모든 패키지를 아는 곳은 입구인 Main과 LoanCheck 둘뿐이다
- 입구가 어댑터와 서비스를 new로 만들고 서로 끼운다. 이 일을 조립이라 한다
- 조립을 한곳에 모으면 다른 코드는 서로의 구현 이름을 몰라도 된다
- 스프링 같은 프레임워크가 해 주는 일을 여기서는 손으로 한다
- LoanCheck가 adapter.in에 가지 않은 것도 이 때문이다. 어댑터와 서비스 구현을 직접 만드는 입구다

## file-store T1

### src/hexa/adapter/out/FileBookAdapter.java

#### 개념 저장 모델 매핑
- 도메인 객체와 저장하는 모양은 다르다. 파일에는 객체가 아니라 글자 한 줄이 들어간다
- 저장할 때 Book을 한 줄로 바꾸고, 읽을 때 한 줄을 Book으로 되살린다. 이것이 매핑이다
- 매핑은 어댑터 안에 둔다. 도메인은 자기가 파일에 어떻게 적히는지 모른다
- 대출 중이던 책은 borrow를 불러 대출 중인 책으로 되살린다. 상태를 바꾸는 길을 새로 만들지 않는다
- 이 예제에서는 toLine과 toBook이 매핑이다. 10장에서 반납 기한 칸이 더해진다

#### 코드
- toLine과 toBook은 따라 친다. readAll과 writeAll은 파일 읽기 쓰기라 복사 버튼으로 붙여넣는다
- Path.of("data/books.txt")는 실행 폴더 기준이다. 이클립스에서는 hexa/data/books.txt가 된다
- 생성자에서 파일이 없을 때만 처음 책 3권을 저장한다
- split(",", -1)의 -1은 끝의 빈 칸도 버리지 않게 한다. 빌리지 않은 책의 빈 회원 번호 칸을 지킨다
- LinkedHashMap은 넣은 순서를 지켜 파일 줄 순서가 섞이지 않는다
- 파일 오류 IOException은 포트에 없는 예외라 UncheckedIOException으로 감싼다

### src/hexa/domain/Book.java
- getBorrowerId는 매핑이 회원 번호를 파일에 적으려고 더했다

## file-store T2

### src/hexa/Main.java

#### 개념 어댑터 갈아 끼우기
- 같은 포트를 구현한 어댑터는 서로 바꿔 끼울 수 있다
- Main에서 new MemoryBookAdapter()를 new FileBookAdapter()로 한 줄 바꿨다. 서비스는 고치지 않았다
- 4장에서 나가는 포트를 연 덕분이다. 그 전이었다면 서비스를 열어 고쳐야 했다
- LoanCheck는 메모리 저장 그대로다. 입구마다 다른 어댑터를 끼워도 된다
- 이제 프로그램을 껐다 켜도 대출 기록이 남는다

## book-list T1

### src/hexa/application/port/in/BookView.java

#### 개념 응답 모델
- 포트가 바깥에 돌려주는 값만 담은 객체다. 상태를 바꾸는 메서드가 없다
- 앞 단계 포트는 도메인 Book을 그대로 돌려줬다. 콘솔이 받은 Book으로 returnBook을 부를 수 있었다
- 그렇게 바꾼 상태는 포트를 거치지 않아 저장되지도 않는다
- 응답 모델은 port.in에 둔다. 들어오는 포트와 함께 바깥에 보이는 약속이다
- 이 예제에서는 BookView가 책 번호, 제목, 대출 여부만 담는다

#### 코드
- record는 값을 담는 클래스를 한 줄로 만든다. 생성자, 읽기 메서드 id(), title(), borrowed()를 자바가 만들어 준다
- record의 필드는 바꿀 수 없다

### src/hexa/application/LoanService.java
- private toView가 Book을 BookView로 바꾼다. 대출과 반납이 같이 쓴다
- 대출 여부는 borrowerId가 null이 아닌지로 정한다

### src/hexa/adapter/in/ConsoleMenu.java
- import hexa.domain.Book이 사라졌다. 콘솔 메뉴는 이제 도메인을 모른다
- record는 getTitle() 대신 title()로 읽는다

## book-list T2

### src/hexa/application/port/in/LoanUseCase.java

#### 개념 조회 유스케이스
- 상태를 바꾸지 않고 읽기만 하는 유스케이스다. 대출, 반납과 같은 들어오는 포트에 둔다
- 조회도 응답 모델을 돌려준다. 그래서 목록을 받은 쪽도 책 상태를 바꿀 수 없다
- 나가는 포트에 전체 찾기를 더하면 저장 어댑터 둘 다 구현해야 한다
- 이 예제에서는 listBooks가 조회 유스케이스다

### src/hexa/application/port/out/BookPort.java
- findAll이 더해져 MemoryBookAdapter와 FileBookAdapter가 모두 구현한다. 하나라도 빠지면 컴파일 오류다

### src/hexa/adapter/out/MemoryBookAdapter.java
- HashMap은 꺼내는 순서가 넣은 순서와 다르다. LinkedHashMap으로 바꿔 목록이 B1, B2, B3 순서로 나온다
- new ArrayList<>(books.values())는 저장된 책을 새 리스트에 담아 돌려준다. 받은 쪽이 리스트를 바꿔도 저장은 그대로다

### src/hexa/application/LoanService.java
- 찾은 책마다 toView로 바꿔 리스트에 담는다

### src/hexa/adapter/in/ConsoleMenu.java
- 조건 ? 참일 때 : 거짓일 때는 둘 중 하나를 고르는 짧은 if다

## borrow-command T1

### src/hexa/application/port/in/BorrowCommand.java

#### 개념 입력 모델
- 들어오는 포트가 받는 값을 하나로 묶은 객체다. 응답 모델이 나가는 값이라면 입력 모델은 들어오는 값이다
- 앞 단계 포트는 글자 두 개를 받았다. 무엇이 회원 번호이고 무엇이 책 번호인지 순서로만 알았다
- 아무도 값을 검사하지 않아 빈 회원 번호로도 대출되었다
- 입력 모델이 있으면 검사할 자리가 생긴다. 다음 단계에서 이 자리에 검사를 넣는다
- 이 예제에서는 BorrowCommand가 대출 요청이다. 반납은 책 번호 하나라 묶지 않는다

### src/hexa/application/LoanService.java
- command.bookId()와 command.memberId()로 요청 안의 값을 꺼낸다

### src/hexa/adapter/in/ConsoleMenu.java
- 콘솔이 읽은 두 값으로 대출 요청을 만들어 포트에 넘긴다

## borrow-command T2

### src/hexa/application/port/in/BorrowCommand.java

#### 개념 입력 검사 위치
- 형식 검사는 저장된 책을 보지 않아도 할 수 있는 검사다. 회원 번호가 비었는가 같은 것이다
- 형식 검사는 입력 모델이 만들어질 때 한다. 잘못된 요청은 포트 안으로 들어오지 못한다
- 업무 규칙은 책의 지금 상태를 알아야 하는 검사다. 이미 대출 중인가 같은 것이다
- 업무 규칙은 도메인 Book에 그대로 둔다
- 콘솔, 두 번째 입구처럼 입구가 늘어도 검사는 한곳에서 한다

#### 코드
- public BorrowCommand { }는 record의 compact 생성자다. 괄호 없이 쓰고 값이 필드에 들어가기 전에 돈다
- isBlank는 빈 글자와 빈칸뿐인 글자를 모두 참으로 본다
- 잘못된 값에는 IllegalArgumentException을 던진다. 아직 잡는 곳이 없어 프로그램이 멈춘다

## loan-errors T1

### src/hexa/adapter/in/ConsoleMenu.java

#### 개념 도메인 예외를 어댑터가 메시지로 바꾸기
- 안쪽(도메인, 서비스, 입력 모델)은 실패를 예외로만 알린다. 무엇이 틀렸는지는 메시지에 담는다
- 바깥 어댑터가 예외를 잡아 자기 방식으로 보여 준다. 콘솔은 한 줄로 찍고 메뉴로 돌아간다
- 앞 단계에서는 아무도 잡지 않아 대출 중인 책을 빌리면 프로그램이 멈췄다
- 표준 예외를 쓰면 콘솔 메뉴가 도메인을 import하지 않고도 잡을 수 있다
- 잘못된 값은 IllegalArgumentException, 지금 상태로 안 되는 일은 IllegalStateException이다
- 예외를 메시지로 바꾸는 곳은 콘솔 메뉴 하나다. 서비스와 도메인은 콘솔 글을 모른다

#### 코드
- 메뉴 처리 전체를 try 하나로 감쌌다. 코드는 그대로고 들여쓰기만 한 칸 늘었다
- catch (A | B e)는 두 예외를 한 번에 잡는다
- e.getMessage()는 던질 때 넣은 글이다
- 없는 책 번호는 아직 NullPointerException이라 잡히지 않는다. 다음 단계에서 고친다

## loan-errors T2

### src/hexa/application/LoanService.java
- private findBook이 찾지 못한 책을 IllegalArgumentException 책을 찾을 수 없습니다로 바꾼다
- null을 그대로 넘기면 다음 줄에서 NullPointerException이 나고 메시지로는 이유를 알 수 없다
- 대출과 반납이 같은 findBook을 쓴다

## loan-errors T3

### src/hexa/domain/Book.java
- 대출 중이 아닌 책의 반납을 도메인이 거절한다. 대출 중 거절과 같은 모양이다
- 콘솔 메뉴는 이미 IllegalStateException을 잡으므로 고치지 않는다

## due-date T1

### src/hexa/domain/Book.java
- LocalDate는 시각 없이 날짜만 담는 자바 표준 클래스다
- borrow가 기한을 받는다. 14일은 서비스가 정하고 도메인은 받은 날짜만 기억한다
- isOverdue는 오늘을 받아 기한이 지났는지 본다. isAfter라서 기한 날 당일은 연체가 아니다
- 도메인은 오늘이 며칠인지 스스로 읽지 않는다

### src/hexa/application/LoanService.java
- LocalDate.now()는 컴퓨터의 오늘 날짜다. plusDays(14)는 14일 뒤다
- 서비스가 시스템 날짜를 직접 읽는다. 그래서 15일 뒤를 실행해 볼 방법이 없다. 다음 단계에서 고친다

### src/hexa/application/port/in/BookView.java
- 응답 모델에 반납 기한과 연체 여부를 더했다. 연체 계산은 서비스가 하고 콘솔은 찍기만 한다

### src/hexa/adapter/out/FileBookAdapter.java
- 저장 줄 끝에 기한 칸을 더했다. 빌리지 않은 책은 빈 칸이다
- LocalDate.parse는 2026-11-30 같은 글자를 날짜로 되살린다. toString은 그 반대다
- 예전 세 칸짜리 저장 파일은 읽지 못한다. hexa/data/books.txt를 지우고 시작한다

### src/hexa/adapter/in/ConsoleMenu.java
- 상태 글을 대출 가능에서 시작해 대출 중이면 기한을, 연체면 끝에 연체를 붙인다

## due-date T2

### src/hexa/application/port/out/ClockPort.java

#### 개념 시계 포트
- 오늘 날짜도 바깥에서 받아 오는 값이다. 그래서 나가는 포트로 연다
- 앞 단계 서비스는 LocalDate.now()를 직접 불러 오늘이 아닌 날을 실행해 볼 수 없었다
- 서비스는 clockPort.today()에 묻기만 하고 그 날짜가 어디서 오는지 모른다
- 저장 포트와 같은 모양이다. 데이터만이 아니라 시간, 알림 같은 바깥 일도 포트가 된다
- 이 예제에서는 ClockPort가 시계 포트다

### src/hexa/adapter/out/FixedClockAdapter.java

#### 개념 시간 갈아 끼우기
- 시계 어댑터는 둘이다. SystemClockAdapter는 진짜 오늘, FixedClockAdapter는 정해 둔 날짜를 준다
- Main은 시스템 시계를, LoanCheck는 고정 시계를 끼운다
- 고정 시계 두 개(3월 2일, 3월 17일)로 15일 뒤 연체를 기다리지 않고 바로 본다
- 서비스와 도메인은 어떤 시계가 끼워졌는지 모른다

#### 코드
- 생성자로 받은 날짜를 today()가 늘 그대로 돌려준다

### src/hexa/application/LoanService.java
- import java.time.LocalDate가 사라졌다. 서비스는 이제 시스템 날짜를 직접 읽지 않는다
- 생성자가 BookPort와 ClockPort 둘을 받는다

### src/hexa/LoanCheck.java
- LocalDate.of(2026, 3, 2)는 날짜를 직접 정한다
- 두 서비스가 같은 bookPort를 나눠 써서 3월 2일에 빌린 책을 3월 17일 서비스가 본다
- listBooks().get(0)은 목록의 첫 책 B1이다

### src/hexa/Main.java
- new SystemClockAdapter()를 서비스에 끼운다. 콘솔에서는 진짜 오늘로 기한이 정해진다

## overdue-notice T1

### src/hexa/application/port/out/NotifyPort.java

#### 개념 알림 포트
- 회원에게 알림을 보내는 일도 바깥 일이다. 나가는 포트로 연다
- 서비스는 send(회원, 글)만 부르고 알림이 콘솔로 가는지 파일로 가는지 모른다
- 알림 글을 만드는 일은 서비스가 하고 보내는 방법은 어댑터가 맡는다
- 이름을 notify로 하지 않은 것은 자바 Object에 이미 notify 메서드가 있어서다
- 이 예제에서는 NotifyPort가 알림 포트고 ConsoleNotifyAdapter가 첫 구현이다

### src/hexa/application/LoanService.java
- notifyOverdue는 모든 책에서 연체된 책을 골라 빌린 회원에게 보내고 보낸 수를 돌려준다
- 빌리지 않은 책은 기한이 없어 isOverdue가 거짓이라 보내지 않는다
- 생성자가 포트 셋을 받는다

### src/hexa/application/port/in/LoanUseCase.java
- 새 포트를 만들지 않고 들어오는 포트에 notifyOverdue를 더했다. 콘솔 메뉴가 받는 포트는 그대로 하나다

### src/hexa/adapter/out/ConsoleNotifyAdapter.java
- 받는 회원과 글을 콘솔에 한 줄로 찍는다

### src/hexa/LoanCheck.java
- 알림 어댑터 하나를 두 서비스에 같이 끼운다

## overdue-notice T2

### src/hexa/adapter/out/FileNotifyAdapter.java

#### 개념 어댑터 하나 더 꽂기
- 포트 하나에 구현을 여러 개 두고 입구마다 골라 끼운다
- Main은 import와 생성 한 줄만 바꿔 파일 알림을 끼운다. 서비스, 포트, 다른 어댑터는 그대로다
- LoanCheck는 콘솔 알림 그대로다. 같은 서비스가 입구에 따라 다른 곳으로 알린다
- 이메일, 문자 알림도 같은 포트를 구현한 어댑터를 하나 더 만들면 된다

#### 코드
- StandardOpenOption.CREATE와 APPEND는 파일이 없으면 만들고 있으면 끝에 더한다. 앞 알림이 지워지지 않는다
- List.of(한 줄)로 한 줄만 쓴다
- 저장 파일과 같은 data 폴더에 notices.txt로 남는다

## dependency-check T1

### .classpath
- kind="src" path="test"는 test 폴더를 두 번째 소스 폴더로 더한다. 학생 테스트가 여기 있다
- kind="lib"는 lib 폴더의 JUnit jar를 프로젝트에 붙인다. 내려받지 않고 복사한 jar를 쓴다
- jar는 코드가 아니라 칠 수 없다. 따라치기 폴더의 파일/lib에 있는 jar를 hexa/lib로 복사한다

### test/hexa/DependencyRuleTest.java

#### 개념 의존 규칙 테스트
- 5장에서 정한 의존 방향은 지금까지 사람이 눈으로 지켰다
- Book에 어댑터 import를 한 줄 넣어도 컴파일되고 Main이 돈다. 아무도 모른다
- 테스트가 소스 파일의 import 줄을 읽어 규칙에 어긋나면 빨간불을 켠다
- 규칙은 셋이다. domain은 안쪽만, application은 adapter를 모른다, 어댑터끼리 모른다
- 단위 테스트가 동작을 지킨다면 이 테스트는 구조를 지킨다
- 이클립스에서 이 파일을 열고 Run As, JUnit Test를 누른다

#### 코드
- @Test가 붙은 메서드 하나가 테스트 하나다. JUnit이 찾아서 돌린다
- assertFalse(조건, 글)은 조건이 참이면 실패하고 그 글을 보여 준다. 어긴 import 줄이 빨간불에 보인다
- import static은 Assertions.assertFalse를 assertFalse로 줄여 쓰게 한다
- 폴더 src/hexa/domain은 이클립스가 테스트를 도는 폴더 hexa 기준이다
- Files.walk는 폴더 아래 파일을 모두 돈다. try ( )로 열면 다 쓴 뒤 저절로 닫힌다
- path -> path.toString().endsWith(".java")는 자바 파일만 고르는 람다다
- 클래스와 메서드에 public이 없어도 JUnit 5는 테스트를 돌린다

## dependency-check T2

### test/hexa/DependencyRuleTest.java
- application은 adapter를 import하지 않는다
- adapter.in은 adapter.out을, adapter.out은 adapter.in을 import하지 않는다. 폴더를 하나씩 따로 읽는다
- 새 규칙은 메서드를 하나 더하고 importsIn을 다시 쓰면 된다
