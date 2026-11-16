package hexa;

import java.util.HashMap;
import java.util.Map;

public class BookRepository {
    private final Map<String, Book> books = new HashMap<>();

    public BookRepository() {
        save(new Book("B1", "객체지향의 사실과 오해"));
        save(new Book("B2", "클린 아키텍처"));
        save(new Book("B3", "도메인 주도 설계 첫걸음"));
    }

    public Book findById(String id) {
        return books.get(id);
    }

    public void save(Book book) {
        books.put(book.getId(), book);
    }
}
