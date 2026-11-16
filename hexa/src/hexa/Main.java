package hexa;

public class Main {

    public static void main(String[] args) {
        System.out.println("도서 대출 프로그램을 시작합니다");
        BookPort bookPort = new MemoryBookAdapter();
        LoanUseCase loanUseCase = new LoanService(bookPort);
        new ConsoleMenu(loanUseCase).run();
    }
}
