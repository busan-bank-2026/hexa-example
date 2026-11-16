package hexa.adapter.out;

import java.time.LocalDate;

import hexa.application.port.out.ClockPort;

public class FixedClockAdapter implements ClockPort {
    private final LocalDate today;

    public FixedClockAdapter(LocalDate today) {
        this.today = today;
    }

    @Override
    public LocalDate today() {
        return today;
    }
}
