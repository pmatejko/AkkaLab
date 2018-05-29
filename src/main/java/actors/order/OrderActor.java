package actors.order;

import akka.actor.*;
import akka.dispatch.OnComplete;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import akka.pattern.Patterns;
import scala.concurrent.duration.Duration;
import util.request.order.BookOrderFileWriteRequest;
import util.request.order.BookOrderRequest;
import util.request.search.BookSearchRequest;
import util.response.Response;
import util.response.order.BookOrderSuccessResponse;
import util.response.search.BookFoundResponse;
import util.response.search.BookNotFoundResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;

public class OrderActor extends AbstractActor {
    private static final String ORDERS_FILE_NAME = "/orders.txt";
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef dbSearchActor;
    private final ActorRef fileWriteActor;

    public OrderActor(ActorRef dbSearchActor) throws URISyntaxException {
        Path ordersPath = Paths.get(getClass()
                .getResource(ORDERS_FILE_NAME)
                .toURI()
        );
        this.dbSearchActor = dbSearchActor;
        this.fileWriteActor = context().actorOf(Props.create(FileWriteActor.class, ordersPath), "fileWriteActor");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookOrderRequest.class, request -> {
                    ActorRef customer = sender();

                    Patterns.ask(dbSearchActor, new BookSearchRequest(request.getBookTitle()), 5_000)
                            .onComplete(new OnComplete<Object>() {
                                @Override
                                public void onComplete(Throwable failure, Object success) {
                                    if (failure != null || success instanceof BookNotFoundResponse) {
                                        Response response = new BookNotFoundResponse();
                                        log.info(response.toString());
                                        customer.tell(response, self());
                                    } else if (success instanceof BookFoundResponse){
                                        BookFoundResponse searchResponse = (BookFoundResponse) success;
                                        fileWriteActor.tell(new BookOrderFileWriteRequest(searchResponse.getBookTitle()), self());

                                        Response response = new BookOrderSuccessResponse();
                                        log.info(response.toString());
                                        customer.tell(response, self());
                                    }
                                }
                            }, context().system().dispatcher());
                })
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(5, Duration.create("1 minute"),
                DeciderBuilder
                        .matchAny(e -> restart())
                        .build()
        );
    }
}
