package hexa;

public interface LoanUseCase {
    Book borrow(String memberId, String bookId);

    Book returnBook(String bookId);
}
