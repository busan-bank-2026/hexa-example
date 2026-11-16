package hexa.application.port.in;

public interface LoanUseCase {
    BookView borrow(String memberId, String bookId);

    BookView returnBook(String bookId);
}
