package actors.search;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import util.request.search.BookSearchSlaveRequest;
import util.response.search.BookFoundSlaveResponse;
import util.response.search.BookNotFoundSlaveResponse;
import util.response.Response;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DBSearchSlaveActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final Path databasePath;

    public DBSearchSlaveActor(String databaseName) throws URISyntaxException {
        this.databasePath = Paths.get(getClass()
                .getResource(databaseName)
                .toURI()
        );
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookSearchSlaveRequest.class, request -> {
                    Response response = Files.lines(databasePath)
                            .map(line -> line.split(";"))
                            .filter(splittedLine -> request.getBookTitle().equals(splittedLine[0]))
                            .findAny()
                            .map(splittedLine -> (Response) new BookFoundSlaveResponse(request.getSearchNumber(), splittedLine[0], Integer.valueOf(splittedLine[1])))
                            .orElseGet(() -> new BookNotFoundSlaveResponse(request.getSearchNumber()));

                    log.info(response.toString());
                    sender().tell(response, self());
                })
                .build();
    }
}
