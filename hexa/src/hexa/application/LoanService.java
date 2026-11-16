package hexa.application;

import java.util.ArrayList;
import java.util.List;

import hexa.application.port.in.BookView;
import hexa.application.port.in.BorrowCommand;
import hexa.application.port.in.LoanUseCase;
import hexa.application.port.out.BookPort;
import hexa.application.port.out.ClockPort;
import hexa.domain.Book;

public class LoanService implements LoanUseCase {
    private final BookPort bookPort;
    private final ClockPort clockPort;

    public LoanService(BookPort bookPort, ClockPort clockPort) {
        this.bookPort = bookPort;
        this.clockPort = clockPort;
    }

    @Override
    public BookView borrow(BorrowCommand command) {
        Book book = findBook(command.bookId());
        book.borrow(command.memberId(), clockPort.today().plusDays(14));
        bookPort.save(book);
        return toView(book);
    }

    @Override
    public BookView returnBook(String bookId) {
        Book book = findBook(bookId);
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

    private Book findBook(String bookId) {
        Book book = bookPort.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("책을 찾을 수 없습니다");
        }
        return book;
    }

    private BookView toView(Book book) {
        return new BookView(book.getId(), book.getTitle(), book.getBorrowerId() != null, book.getDueDate(),
                book.isOverdue(clockPort.today()));
    }
}
