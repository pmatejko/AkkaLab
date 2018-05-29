package main;

import actors.client.ClientActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import util.request.order.BookOrderRequest;
import util.request.search.BookSearchRequest;
import util.request.stream.BookStreamRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ClientMain {

    public static void main(String[] args) throws URISyntaxException, IOException {
        Path configPath = Paths.get(ServerMain.class.getResource("/client_remote.conf").toURI());
        Config config = ConfigFactory.parseFile(configPath.toFile());
        ActorSystem system = ActorSystem.create("client_system", config);
        ActorRef actor = system.actorOf(Props.create(ClientActor.class), "client_actor");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<String> commands = Arrays.asList("search", "order", "stream");
        boolean run = true;
        while (run) {
            System.out.println("Insert command: [search, order, stream, quit]");
            String command = br.readLine();
            String bookTitle = "";
            if (commands.stream().anyMatch(command::equals)) {
                System.out.println("Insert book title");
                bookTitle = br.readLine();
            }

            switch (command) {
                case "quit":
                    run = false;
                    break;
                case "search":
                    actor.tell(new BookSearchRequest(bookTitle), null);
                    break;
                case "order":
                    actor.tell(new BookOrderRequest(bookTitle), null);
                    break;
                case "stream":
                    actor.tell(new BookStreamRequest(bookTitle), null);
                    break;
                default:
                    System.out.println("Wrong command");
                    break;
            }
        }
    }

}
