package actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSharding;
import akka.japi.pf.ReceiveBuilder;
import com.example.protocols.Queries;

public class QueryActor extends AbstractLoggingActor {
    private final ActorRef teamsQuery = ClusterSharding.get(ClusterSystem.getQuerySystem()).shardRegion("TeamsRegion");
    private final ActorRef playersQuery = ClusterSharding.get(ClusterSystem.getQuerySystem()).shardRegion("PlayerRegion");

    public static Props props() {
        return Props.create(QueryActor.class);
    }

    public QueryActor() {
        receive(
                ReceiveBuilder.match(Queries.PlayerRequest.class, query -> {
                    log().info("Sending query to cluster");
                    playersQuery.forward(query, context());
                    //sender().tell("{ \"json\": 1 }", self());
                }).match(Queries.TeamRequest.class, query ->
                    teamsQuery.forward(query, context())
                ).build()
        );
    }
}