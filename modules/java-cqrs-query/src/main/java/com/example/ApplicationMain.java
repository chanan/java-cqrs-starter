package com.example;

import akka.actor.ActorSystem;
import akka.contrib.pattern.ClusterSharding;
import akka.contrib.pattern.ShardRegion;
import com.example.actors.players.PlayerViewActor;
import com.example.actors.teams.TeamViewActor;
import com.example.protocols.Commands;
import com.example.protocols.Queries;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ApplicationMain {

    public static void main(String[] args) {
        final String port = args.length > 0 ? args[0] : "0";
        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                withFallback(ConfigFactory.load());
        //withFallback(ConfigFactory.parseString("akka.cluster.roles = [query]")).

        ActorSystem system = ActorSystem.create("GameClusterSystemQuery", config);

        ShardRegion.MessageExtractor teamMessageExtractor = new ShardRegion.MessageExtractor() {

            @Override
            public String entryId(Object message) {
                if (message instanceof Queries.TeamRequest)
                    return ((Queries.TeamRequest) message).teamName;
                else
                    return null;
            }

            @Override
            public Object entryMessage(Object message) {
                return message;
            }

            @Override
            public String shardId(Object message) {
                if (message instanceof Queries.TeamRequest) {
                    long id = Math.abs(((Queries.TeamRequest) message).teamName.hashCode());
                    return String.valueOf(id % 100);
                } else {
                    return null;
                }
            }

        };
        ClusterSharding.get(system).start("TeamsRegionQuery", TeamViewActor.props(), teamMessageExtractor);

        ShardRegion.MessageExtractor playerMessageExtractor = new ShardRegion.MessageExtractor() {

            @Override
            public String entryId(Object message) {
                if (message instanceof Queries.PlayerRequest)
                    return String.format("%s-%s", ((Queries.PlayerRequest) message).teamName, ((Queries.PlayerRequest) message).jerseyNumber);
                else
                    return null;
            }

            @Override
            public Object entryMessage(Object message) {
                return message;
            }

            @Override
            public String shardId(Object message) {
                if (message instanceof Queries.PlayerRequest) {
                    long id = Math.abs(((Queries.PlayerRequest) message).teamName.hashCode());
                    return String.valueOf(id % 100);
                } else {
                    return null;
                }
            }

        };
        ClusterSharding.get(system).start("PlayerRegionQuery", PlayerViewActor.props(), playerMessageExtractor);

        system.awaitTermination();
    }
}