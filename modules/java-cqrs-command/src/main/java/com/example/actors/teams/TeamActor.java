package com.example.actors.teams;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import com.example.models.Team;
import com.example.protocols.Commands;
import com.example.protocols.Events;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.Optional;

public class TeamActor extends AbstractPersistentActor {
    private final ActorRef teamNormalizer = context().actorOf(TeamNormalizedActor.props());
    private Optional<Team> team = Optional.empty();

    public static Props props() {
        return Props.create(TeamActor.class);
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receiveCommand() {
        return ReceiveBuilder.match(Commands.NewTeam.class, newTeam -> {
            persist(new Events.NewTeamCreated(newTeam.teamName), e -> {
                team = Optional.of(new Team(e.teamName));
                teamNormalizer.tell(e, self());
                context().become(started());
            });
        }).build();
    }

    private PartialFunction<Object, BoxedUnit> started() {
        return ReceiveBuilder.match(Commands.AddPlayer.class, addPlayer ->
                        persist(new Events.NewPlayerCreated(addPlayer.teamName, addPlayer.jerseyNumber, addPlayer.name), e -> {
                            team.ifPresent(theTeam -> {
                                if (!theTeam.getPlayers().contains(addPlayer.jerseyNumber)) {
                                    theTeam.addPlayer(addPlayer.jerseyNumber);
                                    teamNormalizer.tell(e, self());
                                }
                            });
                        })
        ).match(Commands.PlayerScore.class, playerScore ->
                        persist(new Events.PlayerScored(playerScore.teamName, playerScore.jerseyNumber, playerScore.score), e -> {
                            team.ifPresent(theTeam -> theTeam.score(e.score));
                            teamNormalizer.tell(e, self());
                        })
        ).build();
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receiveRecover() {
        return ReceiveBuilder.match(Events.NewTeamCreated.class, e -> {
            team = Optional.of(new Team(e.teamName));
        }).match(Events.NewPlayerCreated.class, e -> {
            team.ifPresent(theTeam -> {
                if (theTeam.getPlayers().contains(e.jerseyNumber)) theTeam.addPlayer(e.jerseyNumber);
            });
        }).match(Events.PlayerScored.class, e -> {
            team.ifPresent(theTeam ->
                theTeam.score(e.score)
            );
        }).build();
    }

    @Override
    public boolean recoveryFinished() {
        context().become(started());
        return super.recoveryFinished();
    }

    @Override
    public String persistenceId() {
        return "Team-" + self().path().name();
    }
}