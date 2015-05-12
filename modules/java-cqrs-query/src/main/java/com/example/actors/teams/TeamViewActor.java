package com.example.actors.teams;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSharding;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentView;
import com.example.models.Team;
import com.example.protocols.Events;
import com.example.protocols.Queries;
import scala.concurrent.Future;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static akka.pattern.Patterns.pipe;
import static akka.pattern.Patterns.ask;

public class TeamViewActor extends AbstractPersistentView {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final ActorRef playersQuery = ClusterSharding.get(context().system()).shardRegion("PlayerRegion");
    private Optional<Team> team = Optional.empty();

    public static Props props() {
        return Props.create(TeamViewActor.class);
    }

    public TeamViewActor() {
        receive(
                ReceiveBuilder.match(Object.class, p -> isPersistent() && p instanceof Events.NewTeamCreated, persistent -> {
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
                }).match(Queries.TeamScoreRequest.class, teamScoreRequest -> {
                    team.ifPresent(team -> {
                        final List<Future<Object>> futures = new ArrayList<>();
                        team.getPlayers().stream().forEach(player -> {
                            final Queries.PlayerRowRequest playerRowRequest = new Queries.PlayerRowRequest(team.getTeamName(), player);
                            final Future future = ask(playersQuery, playerRowRequest, 500);
                            futures.add(future);
                        });
                        final Future<Iterable<Object>> aggregate = Futures.sequence(futures, context().dispatcher());
                        final Future<Queries.TeamScoreResponse> responseFuture = aggregate.map(new Mapper<Iterable<Object>, Queries.TeamScoreResponse>() {
                            public Queries.TeamScoreResponse apply(Iterable<Object> list) {
                                final int totalScore = StreamSupport.stream(list.spliterator(), false).mapToInt(response -> {
                                    Queries.PlayerRowResponse playerRowResponse = (Queries.PlayerRowResponse) response;
                                    return playerRowResponse.score;
                                }).sum();
                                return new Queries.TeamScoreResponse(teamScoreRequest, totalScore);
                            }
                        }, context().dispatcher());
                        pipe(responseFuture, context().dispatcher()).to(sender());
                    });
                    if(!team.isPresent())
                        sender().tell(new Queries.TeamScoreResponse(teamScoreRequest, -1), self());
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