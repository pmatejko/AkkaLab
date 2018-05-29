package actors.server;

import actors.order.OrderActor;
import actors.search.DBSearchActor;
import actors.stream.StreamActor;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;
import util.request.order.BookOrderRequest;
import util.request.search.BookSearchRequest;
import util.request.stream.BookStreamRequest;

import java.io.FileNotFoundException;
import java.io.IOException;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;

public class ServerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef dbSearchActor;
    private final ActorRef orderActor;
    private final ActorRef streamActor;

    public ServerActor() {
        this.dbSearchActor = context().actorOf(Props.create(DBSearchActor.class), "dbSearchActor");
        this.orderActor = context().actorOf(Props.create(OrderActor.class, dbSearchActor), "orderActor");
        this.streamActor = context().actorOf(Props.create(StreamActor.class, dbSearchActor), "streamActor");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookSearchRequest.class, request -> dbSearchActor.tell(request, sender()))
                .match(BookOrderRequest.class, request -> orderActor.tell(request, sender()))
                .match(BookStreamRequest.class, request -> streamActor.tell(request, sender()))
                .matchAny(o -> log.info(o.toString()))
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(5, Duration.create("1 minute"),
                DeciderBuilder
                        .match(IOException.class, e -> restart())
                        .match(FileNotFoundException.class, e -> restart())
                        .matchAny(e -> escalate())
                        .build()
        );
    }
}
