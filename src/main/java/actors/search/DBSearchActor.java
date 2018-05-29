package actors.search;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;
import util.SearchStatus;
import util.request.search.BookSearchRequest;
import util.request.search.BookSearchSlaveRequest;
import util.request.Request;
import util.response.*;
import util.response.search.BookFoundResponse;
import util.response.search.BookFoundSlaveResponse;
import util.response.search.BookNotFoundResponse;
import util.response.search.BookNotFoundSlaveResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static akka.actor.SupervisorStrategy.restart;


public class DBSearchActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef[] dbSearchSlaves;
    private final Map<Long, SearchStatus> searchStatusMap;
    private long currentSearchNumber;

    public DBSearchActor() {
        this.dbSearchSlaves = new ActorRef[] {
                context().actorOf(Props.create(DBSearchSlaveActor.class, "/book_database_1.csv"), "book_db_1_slave"),
                context().actorOf(Props.create(DBSearchSlaveActor.class, "/book_database_2.csv"), "book_db_2_slave")
        };
        this.searchStatusMap = new HashMap<>();
        this.currentSearchNumber = 0;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BookSearchRequest.class, request -> {
                    log.info(request.toString());
                    long searchNumber = currentSearchNumber++;
                    searchStatusMap.put(searchNumber, new SearchStatus(sender()));

                    Request slaveRequest = new BookSearchSlaveRequest(searchNumber, request.getBookTitle());
                    Arrays.stream(dbSearchSlaves)
                            .forEach(slave -> slave.tell(slaveRequest, self()));
                })
                .match(BookFoundSlaveResponse.class, slaveResponse -> {
                    SearchStatus searchStatus = searchStatusMap.get(slaveResponse.getSearchNumber());

                    if (!searchStatus.isFound()) {
                        searchStatus.setFound();

                        Response response = new BookFoundResponse(slaveResponse.getBookTitle(), slaveResponse.getBookPrice());
                        log.info(response.toString());
                        searchStatus.getCustomer()
                                .tell(response, self());
                    }
                })
                .match(BookNotFoundSlaveResponse.class, slaveResponse -> {
                    SearchStatus searchStatus = searchStatusMap.get(slaveResponse.getSearchNumber());
                    searchStatus.incrementCompleteSearchesAmount();

                    if (!searchStatus.isFound() && searchStatus.getCompleteSearchesAmount() >= dbSearchSlaves.length) {
                        searchStatusMap.remove(slaveResponse.getSearchNumber());

                        Response response = new BookNotFoundResponse();
                        log.info(response.toString());
                        searchStatus.getCustomer()
                                .tell(response, self());
                    }
                })
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(5, Duration.create("1 minute"),
                DeciderBuilder.matchAny(throwable -> restart()).build());
    }
}
