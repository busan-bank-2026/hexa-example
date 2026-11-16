package hexa;

import hexa.adapter.out.MemoryBookAdapter;
import hexa.application.LoanService;
import hexa.application.port.in.BookView;
import hexa.application.port.in.BorrowCommand;
import hexa.application.port.in.LoanUseCase;
import hexa.application.port.out.BookPort;

public class LoanCheck {

    public static void main(String[] args) {
        BookPort bookPort = new MemoryBookAdapter();
        LoanUseCase loanUseCase = new LoanService(bookPort);

        BookView borrowed = loanUseCase.borrow(new BorrowCommand("M1", "B1"));
        System.out.println("B1을 M1에게 대출: " + borrowed.title());

        BookView returned = loanUseCase.returnBook("B1");
        System.out.println("B1 반납: " + returned.title());
    }
}
