package actors.stream;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.dispatch.OnComplete;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import util.request.search.BookSearchRequest;
import util.request.stream.BookStreamRequest;
import util.response.Response;
import util.response.search.BookFoundResponse;
import util.response.search.BookNotFoundResponse;
import util.response.stream.BookStreamResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.stream.Collectors;

public class StreamActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef dbSearchActor;

    public StreamActor(ActorRef dbSearchActor) {
        this.dbSearchActor = dbSearchActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookStreamRequest.class, request -> {
                    log.info(request.toString());
                    ActorRef customer = sender();
                    ActorMaterializer materializer = ActorMaterializer.create(context().system());

                    Patterns.ask(dbSearchActor, new BookSearchRequest(request.getBookTitle()), 5_000)
                            .onComplete(new OnComplete<Object>() {
                                @Override
                                public void onComplete(Throwable failure, Object success) throws Throwable {
                                    if (failure != null || success instanceof BookNotFoundResponse) {
                                        Response response = new BookNotFoundResponse();
                                        log.info(response.toString());
                                        customer.tell(new BookNotFoundResponse(), self());
                                    } else if (success instanceof BookFoundResponse) {
                                        BookFoundResponse response = (BookFoundResponse) success;
                                        Path bookPath = Paths.get(getClass().getResource("/" + response.getBookTitle() + ".txt").toURI());

                                        log.info(response.toString() + " - STARTING STREAM");
                                        Source.from(Files.lines(bookPath).collect(Collectors.toList()))
                                                .map(BookStreamResponse::new)
                                                .throttle(1, Duration.ofSeconds(1))
                                                .runWith(Sink.actorRef(customer, new BookStreamResponse("END OF BOOK")), materializer);
                                    }
                                }
                            }, context().system().dispatcher());
                })
                .build();
    }
}
