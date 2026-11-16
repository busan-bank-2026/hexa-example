package hexa.domain;

public class Book {
    private final String id;
    private final String title;
    private String borrowerId;

    public Book(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public void borrow(String memberId) {
        if (borrowerId != null) {
            throw new IllegalStateException("이미 대출 중인 책입니다");
        }
        this.borrowerId = memberId;
    }

    public void returnBook() {
        this.borrowerId = null;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
