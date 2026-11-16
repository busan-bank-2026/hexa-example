package hexa;

import hexa.application.LoanService;
import hexa.application.port.in.LoanUseCase;
import hexa.application.port.out.BookPort;
import hexa.domain.Book;

public class LoanCheck {

    public static void main(String[] args) {
        BookPort bookPort = new MemoryBookAdapter();
        LoanUseCase loanUseCase = new LoanService(bookPort);

        Book borrowed = loanUseCase.borrow("M1", "B1");
        System.out.println("B1을 M1에게 대출: " + borrowed.getTitle());

        Book returned = loanUseCase.returnBook("B1");
        System.out.println("B1 반납: " + returned.getTitle());
    }
}
