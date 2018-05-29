package actors.order;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import util.request.order.BookOrderFileWriteRequest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileWriteActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final BufferedWriter writer;

    public FileWriteActor(Path filePath) throws IOException {
        this.writer = Files.newBufferedWriter(filePath);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookOrderFileWriteRequest.class, request -> {
                    log.info(request.toString());
                    writer.write(request.getLine());
                })
                .build();
    }
}
