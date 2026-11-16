package hexa;

import hexa.adapter.in.ConsoleMenu;
import hexa.adapter.out.ConsoleNotifyAdapter;
import hexa.adapter.out.FileBookAdapter;
import hexa.adapter.out.SystemClockAdapter;
import hexa.application.LoanService;
import hexa.application.port.in.LoanUseCase;
import hexa.application.port.out.BookPort;

public class Main {

    public static void main(String[] args) {
        System.out.println("도서 대출 프로그램을 시작합니다");
        BookPort bookPort = new FileBookAdapter();
        LoanUseCase loanUseCase = new LoanService(bookPort, new SystemClockAdapter(), new ConsoleNotifyAdapter());
        new ConsoleMenu(loanUseCase).run();
    }
}
