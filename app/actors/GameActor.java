package actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSharding;
import akka.japi.pf.ReceiveBuilder;
import com.example.protocols.Commands;
import com.example.protocols.Queries;
import play.Logger;

public class GameActor extends AbstractLoggingActor {
    //Commands
    private ActorRef teamsCommand;
    private ActorRef playersCommand;

    public static Props props() {
        return Props.create(GameActor.class);
    }

    public GameActor() {
        receive(
                ReceiveBuilder.match(Commands.NewTeam.class, newTeam ->
                                teamsCommand.tell(newTeam, self())
                ).match(Commands.AddPlayer.class, addPlayer ->
                                playersCommand.tell(addPlayer, self())
                ).match(Commands.PlayerScore.class, playerScore ->
                                playersCommand.tell(playerScore, self())
                ).match(Queries.PlayerNameRequest.class, request -> {
                    //playersQuery.tell(request, self())
                    //playersQuery.forward(request, context())
                    final ActorRef worker = context().actorOf(Worker.props());
                    worker.forward(request, context());
                }).build()
        );
    }

    @Override
    public void preStart() throws Exception {
        teamsCommand = ClusterSharding.get(ClusterSystem.getCommandSystem()).shardRegion("TeamsRegionCommand");
        playersCommand = ClusterSharding.get(ClusterSystem.getCommandSystem()).shardRegion("PlayerRegionCommand");
    }

    public static class Worker extends AbstractLoggingActor {
        //Queries
        private ActorRef teamsQuery;
        private ActorRef playersQuery;

        public static Props props() {
            return Props.create(Worker.class);
        }

        public Worker() {
            receive(
                    ReceiveBuilder.matchAny(request -> {
                        final ActorRef sender = sender();
                        playersQuery.tell(request, self());
                        context().become(
                                ReceiveBuilder.matchAny(response -> {
                                    Logger.debug("Here!");
                                    sender.tell(response, self());
                                    context().stop(self());
                                }).build()
                        );
                    }).build()
            );
        }

        @Override
        public void preStart() throws Exception {
            teamsQuery = ClusterSharding.get(ClusterSystem.getQuerySystem()).shardRegion("TeamsRegionQuery");
            playersQuery = ClusterSharding.get(ClusterSystem.getQuerySystem()).shardRegion("PlayerRegionQuery");
        }
    }
}