package hexa;

import java.util.Scanner;

public class LoanService {
    private final Scanner scanner;
    private final BookRepository repository = new BookRepository();

    public LoanService(Scanner scanner) {
        this.scanner = scanner;
    }

    public void borrow() {
        System.out.print("회원 번호: ");
        String memberId = scanner.nextLine();
        System.out.print("책 번호: ");
        Book book = repository.findById(scanner.nextLine());
        book.borrow(memberId);
        repository.save(book);
        System.out.println(book.getTitle() + " 대출했습니다");
    }

    public void returnBook() {
        System.out.print("책 번호: ");
        Book book = repository.findById(scanner.nextLine());
        book.returnBook();
        repository.save(book);
        System.out.println(book.getTitle() + " 반납했습니다");
    }
}
