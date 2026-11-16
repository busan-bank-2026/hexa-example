package hexa;

public class Main {

    public static void main(String[] args) {
        System.out.println("도서 대출 프로그램을 시작합니다");
        LoanService loanService = new LoanService();
        new ConsoleMenu(loanService).run();
    }
}
