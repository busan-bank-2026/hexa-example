package hexa;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("도서 대출 프로그램을 시작합니다");
        Scanner scanner = new Scanner(System.in);
        LoanService loanService = new LoanService(scanner);

        while (true) {
            System.out.println("1 대출  2 반납  0 종료");
            System.out.print("> ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                loanService.borrow();
            } else if (choice.equals("2")) {
                loanService.returnBook();
            } else if (choice.equals("0")) {
                System.out.println("프로그램을 끝냅니다");
                break;
            }
        }
    }
}
