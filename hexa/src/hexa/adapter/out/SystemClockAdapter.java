package hexa.adapter.out;

import java.time.LocalDate;

import hexa.application.port.out.ClockPort;

public class SystemClockAdapter implements ClockPort {

    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}
