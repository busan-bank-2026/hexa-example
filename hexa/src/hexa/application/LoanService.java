package hexa.application;

import hexa.application.port.in.BookView;
import hexa.application.port.in.LoanUseCase;
import hexa.application.port.out.BookPort;
import hexa.domain.Book;

public class LoanService implements LoanUseCase {
    private final BookPort bookPort;

    public LoanService(BookPort bookPort) {
        this.bookPort = bookPort;
    }

    @Override
    public BookView borrow(String memberId, String bookId) {
        Book book = bookPort.findById(bookId);
        book.borrow(memberId);
        bookPort.save(book);
        return toView(book);
    }

    @Override
    public BookView returnBook(String bookId) {
        Book book = bookPort.findById(bookId);
        book.returnBook();
        bookPort.save(book);
        return toView(book);
    }

    private BookView toView(Book book) {
        return new BookView(book.getId(), book.getTitle(), book.getBorrowerId() != null);
    }
}
