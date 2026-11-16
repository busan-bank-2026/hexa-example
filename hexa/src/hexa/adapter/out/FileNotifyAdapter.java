package hexa.adapter.out;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import hexa.application.port.out.NotifyPort;

public class FileNotifyAdapter implements NotifyPort {
    private final Path file = Path.of("data/notices.txt");

    @Override
    public void send(String memberId, String message) {
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, List.of(memberId + "," + message), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
