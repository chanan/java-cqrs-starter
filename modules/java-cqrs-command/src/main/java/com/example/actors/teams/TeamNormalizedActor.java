package com.example.actors.teams;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.example.protocols.Events;

/*
    This class can write to a normalized database for purposes of searching
    Can also be used to write events to a Spark Streaming or Storm for analysis
    Acks can be implemented to ensure delivery or using AbstractPersistentActorWithAtLeastOnceDelivery
 */
public class TeamNormalizedActor extends AbstractLoggingActor {
    public static Props props() {
        return Props.create(TeamNormalizedActor.class);
    }

    public TeamNormalizedActor() {
        receive(
                ReceiveBuilder.match(Events.NewTeamCreated.class, newTeamCreated ->
                    log().info("Write to database new team {} created",
                            newTeamCreated.teamName)
                ).match(Events.NewPlayerCreated.class, newPlayerCreated ->
                    log().info("Write to database new player {} with jersery {} added to {} team",
                            newPlayerCreated.name, newPlayerCreated.jerseyNumber, newPlayerCreated.teamName)
                ).match(Events.PlayerScored.class, playerScored ->
                    log().info("{} point(s) scored for {} team by player {}",
                            playerScored.score, playerScored.teamName, playerScored.jerseyNumber)
                ).build()
        );
    }
}