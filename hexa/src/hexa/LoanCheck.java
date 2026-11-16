package hexa;

import java.time.LocalDate;

import hexa.adapter.out.ConsoleNotifyAdapter;
import hexa.adapter.out.FixedClockAdapter;
import hexa.adapter.out.MemoryBookAdapter;
import hexa.application.LoanService;
import hexa.application.port.in.BookView;
import hexa.application.port.in.BorrowCommand;
import hexa.application.port.in.LoanUseCase;
import hexa.application.port.out.BookPort;
import hexa.application.port.out.NotifyPort;

public class LoanCheck {

    public static void main(String[] args) {
        BookPort bookPort = new MemoryBookAdapter();
        NotifyPort notifyPort = new ConsoleNotifyAdapter();
        LoanUseCase loanUseCase = new LoanService(bookPort, new FixedClockAdapter(LocalDate.of(2026, 3, 2)), notifyPort);

        BookView borrowed = loanUseCase.borrow(new BorrowCommand("M1", "B1"));
        System.out.println("B1을 M1에게 대출: " + borrowed.title());
        System.out.println("반납 기한: " + borrowed.dueDate());

        LoanUseCase later = new LoanService(bookPort, new FixedClockAdapter(LocalDate.of(2026, 3, 17)), notifyPort);
        BookView late = later.listBooks().get(0);
        System.out.println("15일 뒤 B1 연체: " + late.overdue());

        int count = later.notifyOverdue();
        System.out.println("연체 알림: " + count + "건");

        BookView returned = later.returnBook("B1");
        System.out.println("B1 반납: " + returned.title());
    }
}
