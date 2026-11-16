package hexa.domain;

import java.time.LocalDate;

public class Book {
    private final String id;
    private final String title;
    private String borrowerId;
    private LocalDate dueDate;

    public Book(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public void borrow(String memberId, LocalDate dueDate) {
        if (borrowerId != null) {
            throw new IllegalStateException("이미 대출 중인 책입니다");
        }
        this.borrowerId = memberId;
        this.dueDate = dueDate;
    }

    public void returnBook() {
        if (borrowerId == null) {
            throw new IllegalStateException("대출 중이 아닌 책입니다");
        }
        this.borrowerId = null;
        this.dueDate = null;
    }

    public boolean isOverdue(LocalDate today) {
        return dueDate != null && today.isAfter(dueDate);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
