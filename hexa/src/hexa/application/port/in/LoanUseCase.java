package hexa.application.port.in;

import java.util.List;

public interface LoanUseCase {
    BookView borrow(String memberId, String bookId);

    BookView returnBook(String bookId);

    List<BookView> listBooks();
}
