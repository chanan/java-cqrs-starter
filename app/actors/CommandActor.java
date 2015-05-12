package actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSharding;
import akka.japi.pf.ReceiveBuilder;
import com.example.protocols.Commands;
import com.example.protocols.Queries;
import play.Logger;

public class CommandActor extends AbstractLoggingActor {
    private final ActorRef teamsCommand = ClusterSharding.get(ClusterSystem.getCommandSystem()).shardRegion("TeamsRegion");
    private final ActorRef playersCommand = ClusterSharding.get(ClusterSystem.getCommandSystem()).shardRegion("PlayerRegion");

    public static Props props() {
        return Props.create(CommandActor.class);
    }

    public CommandActor() {
        receive(
                ReceiveBuilder.match(Commands.PlayerCommand.class, command ->
                    playersCommand.forward(command, context())
                ).match(Commands.TeamCommand.class, command -> {
                    log().info("Sending team command to cluster");
                    teamsCommand.forward(command, context());
                }).build()
        );
    }
}