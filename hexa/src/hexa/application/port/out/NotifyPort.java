package hexa.application.port.out;

public interface NotifyPort {
    void send(String memberId, String message);
}
