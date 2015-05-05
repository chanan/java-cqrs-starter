package com.example;

import akka.actor.ActorSystem;
import akka.contrib.pattern.ClusterSharding;
import akka.contrib.pattern.ShardRegion;
import com.example.actors.players.PlayerActor;
import com.example.actors.teams.TeamActor;
import com.example.protocols.Commands;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ApplicationMain {

    public static void main(String[] args) {
        final String port = args.length > 0 ? args[0] : "0";
        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                withFallback(ConfigFactory.load());
        //withFallback(ConfigFactory.parseString("akka.cluster.roles = [command]")).

        ActorSystem system = ActorSystem.create("GameClusterSystemCommand", config);

        ShardRegion.MessageExtractor teamMessageExtractor = new ShardRegion.MessageExtractor() {

            @Override
            public String entryId(Object message) {
                if (message instanceof Commands.TeamCommand)
                    return ((Commands.TeamCommand) message).teamName;
                else
                    return null;
            }

            @Override
            public Object entryMessage(Object message) {
                return message;
            }

            @Override
            public String shardId(Object message) {
                if (message instanceof Commands.TeamCommand) {
                    long id = Math.abs(((Commands.TeamCommand) message).teamName.hashCode());
                    return String.valueOf(id % 100);
                } else {
                    return null;
                }
            }

        };
        ClusterSharding.get(system).start("TeamsRegionCommand", TeamActor.props(), teamMessageExtractor);

        ShardRegion.MessageExtractor playerMessageExtractor = new ShardRegion.MessageExtractor() {

            @Override
            public String entryId(Object message) {
                if (message instanceof Commands.PlayerCommand)
                    return String.format("%s-%s", ((Commands.PlayerCommand) message).teamName, ((Commands.PlayerCommand) message).jerseyNumber);
                else
                    return null;
            }

            @Override
            public Object entryMessage(Object message) {
                return message;
            }

            @Override
            public String shardId(Object message) {
                if (message instanceof Commands.PlayerCommand) {
                    long id = Math.abs(((Commands.PlayerCommand) message).teamName.hashCode());
                    return String.valueOf(id % 100);
                } else {
                    return null;
                }
            }

        };
        ClusterSharding.get(system).start("PlayerRegionCommand", PlayerActor.props(), playerMessageExtractor);

        system.awaitTermination();
    }
}