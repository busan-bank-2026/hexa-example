package hexa;

public class LoanService implements LoanUseCase {
    private final BookRepository repository = new BookRepository();

    @Override
    public Book borrow(String memberId, String bookId) {
        Book book = repository.findById(bookId);
        book.borrow(memberId);
        repository.save(book);
        return book;
    }

    @Override
    public Book returnBook(String bookId) {
        Book book = repository.findById(bookId);
        book.returnBook();
        repository.save(book);
        return book;
    }
}
