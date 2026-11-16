package hexa.application.port.in;

import java.time.LocalDate;

public record BookView(String id, String title, boolean borrowed, LocalDate dueDate, boolean overdue) {
}
