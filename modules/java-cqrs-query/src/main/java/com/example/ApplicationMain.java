package com.example;

import akka.actor.ActorSystem;
import akka.contrib.pattern.ClusterSharding;
import akka.contrib.pattern.ShardRegion;
import com.example.actors.players.PlayerViewActor;
import com.example.actors.teams.TeamViewActor;
import com.example.protocols.Commands;
import com.example.protocols.Queries;
import com.example.shards.PlayerQueryExtractor;
import com.example.shards.TeamQueryExtractor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ApplicationMain {

    public static void main(String[] args) {
        final String port = args.length > 0 ? args[0] : "0";
        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                withFallback(ConfigFactory.load());

        ActorSystem system = ActorSystem.create("GameClusterSystemQuery", config);
        ClusterSharding.get(system).start("TeamsRegion", TeamViewActor.props(), new TeamQueryExtractor());
        ClusterSharding.get(system).start("PlayerRegion", PlayerViewActor.props(), new PlayerQueryExtractor());

        system.awaitTermination();
    }
}