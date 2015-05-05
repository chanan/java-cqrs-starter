package com.example.actors.teams;

import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentView;
import com.example.models.Team;
import com.example.protocols.Events;

import java.util.Optional;

public class TeamViewActor extends AbstractPersistentView {
    private Optional<Team> team = Optional.empty();

    public static Props props() {
        return Props.create(TeamViewActor.class);
    }

    public TeamViewActor() {
        receive(
                ReceiveBuilder.match(Object.class, p -> isPersistent() && p instanceof Events.NewTeamCreated,  persistent -> {
                    Events.NewTeamCreated e = (Events.NewTeamCreated) persistent;
                    team = Optional.of(new Team(e.teamName));
                }).match(Object.class, p -> isPersistent() && p instanceof Events.NewPlayerCreated, persistent -> {
                    Events.NewPlayerCreated e = (Events.NewPlayerCreated) persistent;
                    team.ifPresent(theTeam ->
                                    theTeam.addPlayer(e.jerseyNumber)
                    );
                }).match(Object.class, p -> isPersistent() && p instanceof Events.PlayerScored, persistent -> {
                    Events.PlayerScored e = (Events.PlayerScored) persistent;
                    team.ifPresent(theTeam ->
                                    theTeam.score(e.score)
                    );
                }).build()
        );
    }

    @Override
    public String viewId() {
        return "Team-" + self().path().name() + "-view";
    }

    @Override
    public String persistenceId() {
        return "Team-" + self().path().name();
    }
}
