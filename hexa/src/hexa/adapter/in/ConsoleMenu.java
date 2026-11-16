package hexa.adapter.in;

import java.util.Scanner;

import hexa.application.port.in.BookView;
import hexa.application.port.in.BorrowCommand;
import hexa.application.port.in.LoanUseCase;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final LoanUseCase loanUseCase;

    public ConsoleMenu(LoanUseCase loanUseCase) {
        this.loanUseCase = loanUseCase;
    }

    public void run() {
        while (true) {
            System.out.println("1 대출  2 반납  3 목록  0 종료");
            System.out.print("> ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("회원 번호: ");
                String memberId = scanner.nextLine();
                System.out.print("책 번호: ");
                String bookId = scanner.nextLine();
                BookView book = loanUseCase.borrow(new BorrowCommand(memberId, bookId));
                System.out.println(book.title() + " 대출했습니다");
            } else if (choice.equals("2")) {
                System.out.print("책 번호: ");
                String bookId = scanner.nextLine();
                BookView book = loanUseCase.returnBook(bookId);
                System.out.println(book.title() + " 반납했습니다");
            } else if (choice.equals("3")) {
                for (BookView book : loanUseCase.listBooks()) {
                    String status = book.borrowed() ? "대출 중" : "대출 가능";
                    System.out.println(book.id() + " " + book.title() + " " + status);
                }
            } else if (choice.equals("0")) {
                System.out.println("프로그램을 끝냅니다");
                break;
            }
        }
    }
}
