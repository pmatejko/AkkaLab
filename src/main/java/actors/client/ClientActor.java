package actors.client;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import util.request.order.BookOrderRequest;
import util.request.search.BookSearchRequest;
import util.request.stream.BookStreamRequest;
import util.response.order.BookOrderSuccessResponse;
import util.response.search.BookFoundResponse;
import util.response.search.BookNotFoundResponse;
import util.response.stream.BookStreamResponse;

public class ClientActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorSelection serverActor;

    public ClientActor() {
        this.serverActor = context()
                .actorSelection("akka.tcp://server_remote_system@127.0.0.1:3552/user/server_remote_actor");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookSearchRequest.class, request -> serverActor.tell(request, self()))
                .match(BookOrderRequest.class, request -> serverActor.tell(request, self()))
                .match(BookStreamRequest.class, request -> serverActor.tell(request, self()))
                .match(BookFoundResponse.class, response -> System.out.println("Book found: " + response.toString()))
                .match(BookNotFoundResponse.class, response -> System.out.println("Book not found."))
                .match(BookOrderSuccessResponse.class, response -> System.out.println("Book ordered: " + response.toString()))
                .match(BookStreamResponse.class, response -> System.out.println(response.getBookLine()))
                .matchAny(o -> log.info("Unknown message received"))
                .build();
    }
}
