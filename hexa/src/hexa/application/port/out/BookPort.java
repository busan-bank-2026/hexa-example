package hexa.application.port.out;

import java.util.List;

import hexa.domain.Book;

public interface BookPort {
    Book findById(String id);

    List<Book> findAll();

    void save(Book book);
}
