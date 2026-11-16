package hexa;

import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final LoanService loanService;

    public ConsoleMenu(LoanService loanService) {
        this.loanService = loanService;
    }

    public void run() {
        while (true) {
            System.out.println("1 대출  2 반납  0 종료");
            System.out.print("> ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("회원 번호: ");
                String memberId = scanner.nextLine();
                System.out.print("책 번호: ");
                String bookId = scanner.nextLine();
                Book book = loanService.borrow(memberId, bookId);
                System.out.println(book.getTitle() + " 대출했습니다");
            } else if (choice.equals("2")) {
                System.out.print("책 번호: ");
                String bookId = scanner.nextLine();
                Book book = loanService.returnBook(bookId);
                System.out.println(book.getTitle() + " 반납했습니다");
            } else if (choice.equals("0")) {
                System.out.println("프로그램을 끝냅니다");
                break;
            }
        }
    }
}
