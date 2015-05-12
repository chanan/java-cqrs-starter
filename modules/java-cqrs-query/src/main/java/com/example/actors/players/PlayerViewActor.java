package com.example.actors.players;

import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentView;
import com.example.models.Player;
import com.example.protocols.Events;
import com.example.protocols.Queries;

import java.util.Optional;

public class PlayerViewActor extends AbstractPersistentView {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private Optional<Player> player = Optional.empty();

    public static Props props() {
        return Props.create(PlayerViewActor.class);
    }

    public PlayerViewActor() {
        receive(
                ReceiveBuilder.match(Object.class, p -> isPersistent() && p instanceof Events.NewPlayerCreated, persistent -> {
                    log.info("Player recreated!");
                    Events.NewPlayerCreated e = (Events.NewPlayerCreated) persistent;
                    player = Optional.of(new Player(e.teamName, e.jerseyNumber, e.name));
                }).match(Object.class, p -> isPersistent() && p instanceof Events.PlayerScored, persistent -> {
                    log.info("Player Scored");
                    Events.PlayerScored e = (Events.PlayerScored) persistent;
                    player.ifPresent(thePlayer ->
                                    thePlayer.score(e.score)
                    );
                }).match(Queries.PlayerRowRequest.class, request -> {
                    Player thePlayer = player.orElse(new Player(null, -1, null));
                    sender().tell(new Queries.PlayerRowResponse(request, thePlayer.getName(), thePlayer.getPoints()), self());
                }).build()
        );
    }

    @Override
    public String viewId() {
        return "Player-" + self().path().name() + "-view";
    }

    @Override
    public String persistenceId() {
        return "Player-" + self().path().name();
    }
}
