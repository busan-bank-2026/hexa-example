package hexa.application.port.in;

import java.util.List;

public interface LoanUseCase {
    BookView borrow(BorrowCommand command);

    BookView returnBook(String bookId);

    List<BookView> listBooks();

    int notifyOverdue();
}
