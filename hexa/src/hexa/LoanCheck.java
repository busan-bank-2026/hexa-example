package hexa;

public class LoanCheck {

    public static void main(String[] args) {
        LoanUseCase loanUseCase = new LoanService();

        Book borrowed = loanUseCase.borrow("M1", "B1");
        System.out.println("B1을 M1에게 대출: " + borrowed.getTitle());

        Book returned = loanUseCase.returnBook("B1");
        System.out.println("B1 반납: " + returned.getTitle());
    }
}
