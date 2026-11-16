package hexa;

public interface BookPort {
    Book findById(String id);

    void save(Book book);
}
