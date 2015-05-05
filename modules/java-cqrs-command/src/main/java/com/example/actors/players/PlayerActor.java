package com.example.actors.players;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSharding;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import com.example.models.Player;
import com.example.protocols.Commands;
import com.example.protocols.Events;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.Optional;

public class PlayerActor extends AbstractPersistentActor {
    private final ActorRef playerNormalizer = context().actorOf(PlayerNormalizedActor.props());
    private Optional<Player> player = Optional.empty();
    private ActorRef teamsRegion;

    public static Props props() {
        return Props.create(PlayerActor.class);
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receiveCommand() {
        return ReceiveBuilder.match(Commands.AddPlayer.class, addPlayer -> {
            persist(new Events.NewPlayerCreated(addPlayer.teamName, addPlayer.jerseyNumber, addPlayer.name), e -> {
                player = Optional.of(new Player(e.teamName, e.jerseyNumber, e.name));
                playerNormalizer.tell(e, self());
                teamsRegion.tell(addPlayer, self()); //Let the team know the player has been added
                context().become(started());
            });
        }).build();
    }

    private PartialFunction<Object, BoxedUnit> started() {
        return ReceiveBuilder.match(Commands.PlayerScore.class, playerScore -> {
            persist(new Events.PlayerScored(playerScore.teamName, playerScore.jerseyNumber, playerScore.score), e -> {
                player.get().score(e.score);
                playerNormalizer.tell(e, self()); //NOTE: Can also send "final" player score instead of event
                teamsRegion.tell(e, self());
            });
        }).build();
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receiveRecover() {
        return ReceiveBuilder.match(Events.NewPlayerCreated.class, e ->
                player = Optional.of(new Player(e.teamName, e.jerseyNumber, e.name))
        ).match(Events.PlayerScored.class, e ->
                        player.get().score(e.score)
        ).build();
    }

    @Override
    public boolean recoveryFinished() {
        context().become(started());
        return super.recoveryFinished();
    }

    @Override
    public String persistenceId() {
        return "Player-" + self().path().name();
    }

    @Override
    public void preStart() throws Exception {
        teamsRegion = ClusterSharding.get(context().system()).shardRegion("TeamsRegionCommand");
        super.preStart();
    }
}