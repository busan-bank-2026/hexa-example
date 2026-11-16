package hexa.adapter.out;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hexa.application.port.out.BookPort;
import hexa.domain.Book;

public class FileBookAdapter implements BookPort {
    private final Path file = Path.of("data/books.txt");

    public FileBookAdapter() {
        if (Files.notExists(file)) {
            save(new Book("B1", "객체지향의 사실과 오해"));
            save(new Book("B2", "클린 아키텍처"));
            save(new Book("B3", "도메인 주도 설계 첫걸음"));
        }
    }

    @Override
    public Book findById(String id) {
        return readAll().get(id);
    }

    @Override
    public void save(Book book) {
        Map<String, Book> books = readAll();
        books.put(book.getId(), book);
        writeAll(books);
    }

    private String toLine(Book book) {
        String borrowerId = book.getBorrowerId() == null ? "" : book.getBorrowerId();
        return book.getId() + "," + book.getTitle() + "," + borrowerId;
    }

    private Book toBook(String line) {
        String[] parts = line.split(",", -1);
        Book book = new Book(parts[0], parts[1]);
        if (!parts[2].isEmpty()) {
            book.borrow(parts[2]);
        }
        return book;
    }

    private Map<String, Book> readAll() {
        Map<String, Book> books = new LinkedHashMap<>();
        if (Files.notExists(file)) {
            return books;
        }
        try {
            for (String line : Files.readAllLines(file)) {
                Book book = toBook(line);
                books.put(book.getId(), book);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return books;
    }

    private void writeAll(Map<String, Book> books) {
        List<String> lines = new ArrayList<>();
        for (Book book : books.values()) {
            lines.add(toLine(book));
        }
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, lines);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
