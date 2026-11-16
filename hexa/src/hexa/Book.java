package hexa;

public class Book {
    private final String id;
    private final String title;
    private String borrowerId;

    public Book(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public void borrow(String memberId) {
        this.borrowerId = memberId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
