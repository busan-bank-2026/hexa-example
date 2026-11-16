package hexa.application;

import java.util.ArrayList;
import java.util.List;

import hexa.application.port.in.BookView;
import hexa.application.port.in.BorrowCommand;
import hexa.application.port.in.LoanUseCase;
import hexa.application.port.out.BookPort;
import hexa.domain.Book;

public class LoanService implements LoanUseCase {
    private final BookPort bookPort;

    public LoanService(BookPort bookPort) {
        this.bookPort = bookPort;
    }

    @Override
    public BookView borrow(BorrowCommand command) {
        Book book = bookPort.findById(command.bookId());
        book.borrow(command.memberId());
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

    @Override
    public List<BookView> listBooks() {
        List<BookView> views = new ArrayList<>();
        for (Book book : bookPort.findAll()) {
            views.add(toView(book));
        }
        return views;
    }

    private BookView toView(Book book) {
        return new BookView(book.getId(), book.getTitle(), book.getBorrowerId() != null);
    }
}
