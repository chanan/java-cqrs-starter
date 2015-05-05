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
                    Events.NewPlayerCreated e = (Events.NewPlayerCreated) persistent;
                    player = Optional.of(new Player(e.teamName, e.jerseyNumber, e.name));
                }).match(Object.class, p -> isPersistent() && p instanceof Events.PlayerScored, persistent -> {
                    Events.PlayerScored e = (Events.PlayerScored) persistent;
                    player.ifPresent( thePlayer ->
                        thePlayer.score(e.score)
                    );
                }).match(Queries.PlayerNameRequest.class, request -> {
                    log.info("Query!");
                    log.info("Sender: " + sender());
                    sender().tell(new Queries.PlayerNameResponse(request, player.get().getName()), self());
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
