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
            System.out.println("1 대출  2 반납  3 목록  4 연체 알림  0 종료");
            System.out.print("> ");
            String choice = scanner.nextLine();

            try {
                if (choice.equals("1")) {
                    System.out.print("회원 번호: ");
                    String memberId = scanner.nextLine();
                    System.out.print("책 번호: ");
                    String bookId = scanner.nextLine();
                    BookView book = loanUseCase.borrow(new BorrowCommand(memberId, bookId));
                    System.out.println(book.title() + " 대출했습니다");
                    System.out.println("반납 기한: " + book.dueDate());
                } else if (choice.equals("2")) {
                    System.out.print("책 번호: ");
                    String bookId = scanner.nextLine();
                    BookView book = loanUseCase.returnBook(bookId);
                    System.out.println(book.title() + " 반납했습니다");
                } else if (choice.equals("3")) {
                    for (BookView book : loanUseCase.listBooks()) {
                        String status = "대출 가능";
                        if (book.borrowed()) {
                            status = "대출 중 반납 기한 " + book.dueDate();
                        }
                        if (book.overdue()) {
                            status = status + " 연체";
                        }
                        System.out.println(book.id() + " " + book.title() + " " + status);
                    }
                } else if (choice.equals("4")) {
                    int count = loanUseCase.notifyOverdue();
                    System.out.println("연체 알림 " + count + "건을 보냈습니다");
                } else if (choice.equals("0")) {
                    System.out.println("프로그램을 끝냅니다");
                    break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
