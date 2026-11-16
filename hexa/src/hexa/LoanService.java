package hexa;

public class LoanService {
    private final BookRepository repository = new BookRepository();

    public Book borrow(String memberId, String bookId) {
        Book book = repository.findById(bookId);
        book.borrow(memberId);
        repository.save(book);
        return book;
    }

    public Book returnBook(String bookId) {
        Book book = repository.findById(bookId);
        book.returnBook();
        repository.save(book);
        return book;
    }
}
