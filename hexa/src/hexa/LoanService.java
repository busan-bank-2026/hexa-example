package hexa;

public class LoanService implements LoanUseCase {
    private final BookPort bookPort = new BookRepository();

    @Override
    public Book borrow(String memberId, String bookId) {
        Book book = bookPort.findById(bookId);
        book.borrow(memberId);
        bookPort.save(book);
        return book;
    }

    @Override
    public Book returnBook(String bookId) {
        Book book = bookPort.findById(bookId);
        book.returnBook();
        bookPort.save(book);
        return book;
    }
}
