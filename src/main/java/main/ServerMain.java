package main;

import actors.server.ServerActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerMain {

    public static void main(String[] args) throws URISyntaxException {
        Path configPath = Paths.get(ServerMain.class.getResource("/server_remote.conf").toURI());
        Config config = ConfigFactory.parseFile(configPath.toFile());
        ActorSystem system = ActorSystem.create("server_remote_system", config);
        ActorRef actor = system.actorOf(Props.create(ServerActor.class), "server_remote_actor");
    }

}
