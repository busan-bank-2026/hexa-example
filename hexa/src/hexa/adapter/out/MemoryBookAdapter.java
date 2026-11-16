package hexa.adapter.out;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hexa.application.port.out.BookPort;
import hexa.domain.Book;

public class MemoryBookAdapter implements BookPort {
    private final Map<String, Book> books = new LinkedHashMap<>();

    public MemoryBookAdapter() {
        save(new Book("B1", "객체지향의 사실과 오해"));
        save(new Book("B2", "클린 아키텍처"));
        save(new Book("B3", "도메인 주도 설계 첫걸음"));
    }

    @Override
    public Book findById(String id) {
        return books.get(id);
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    @Override
    public void save(Book book) {
        books.put(book.getId(), book);
    }
}
