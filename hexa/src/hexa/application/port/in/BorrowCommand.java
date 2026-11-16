package hexa.application.port.in;

public record BorrowCommand(String memberId, String bookId) {
    public BorrowCommand {
        if (memberId.isBlank()) {
            throw new IllegalArgumentException("회원 번호가 비었습니다");
        }
        if (bookId.isBlank()) {
            throw new IllegalArgumentException("책 번호가 비었습니다");
        }
    }
}
