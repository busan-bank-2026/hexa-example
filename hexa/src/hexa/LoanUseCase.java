package hexa;

import hexa.domain.Book;

public interface LoanUseCase {
    Book borrow(String memberId, String bookId);

    Book returnBook(String bookId);
}
