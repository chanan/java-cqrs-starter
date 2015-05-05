package com.example.actors.players;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.example.protocols.Events;

/*
    This class can write to a normalized database for purposes of searching
    Can also be used to write events to a Spark Streaming or Storm for analysis
    Acks can be implemented to ensure delivery or using AbstractPersistentActorWithAtLeastOnceDelivery
 */
public class PlayerNormalizedActor extends AbstractLoggingActor {
    public static Props props() {
        return Props.create(PlayerNormalizedActor.class);
    }

    public PlayerNormalizedActor() {
        receive(
                ReceiveBuilder.match(Events.NewPlayerCreated.class, newPlayerCreated ->
                        log().info("Write to database new player {} with jersery {} created in {} team",
                                newPlayerCreated.name, newPlayerCreated.jerseyNumber, newPlayerCreated.teamName)
                ).match(Events.PlayerScored.class, playerScored ->
                    log().info("Write to database player {} in team {} scored: {} point(s)",
                            playerScored.jerseyNumber, playerScored.teamName, playerScored.score)
                ).build()
        );
    }
}