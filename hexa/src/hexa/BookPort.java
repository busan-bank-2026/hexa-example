package hexa;

import hexa.domain.Book;

public interface BookPort {
    Book findById(String id);

    void save(Book book);
}
