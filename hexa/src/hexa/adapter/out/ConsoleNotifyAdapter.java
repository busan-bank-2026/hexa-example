package hexa.adapter.out;

import hexa.application.port.out.NotifyPort;

public class ConsoleNotifyAdapter implements NotifyPort {

    @Override
    public void send(String memberId, String message) {
        System.out.println(memberId + "에게 알림: " + message);
    }
}
