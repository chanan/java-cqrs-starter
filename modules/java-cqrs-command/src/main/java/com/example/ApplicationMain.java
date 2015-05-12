package com.example;

import akka.actor.ActorSystem;
import akka.contrib.pattern.ClusterSharding;
import akka.contrib.pattern.ShardRegion;
import com.example.actors.players.PlayerActor;
import com.example.actors.teams.TeamActor;
import com.example.protocols.Commands;
import com.example.shards.PlayerCommandExtractor;
import com.example.shards.TeamCommandExtractor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ApplicationMain {

    public static void main(String[] args) {
        final String port = args.length > 0 ? args[0] : "0";
        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                withFallback(ConfigFactory.load());

        ActorSystem system = ActorSystem.create("GameClusterSystemCommand", config);
        ClusterSharding.get(system).start("TeamsRegion", TeamActor.props(), new TeamCommandExtractor());
        ClusterSharding.get(system).start("PlayerRegion", PlayerActor.props(), new PlayerCommandExtractor());

        system.awaitTermination();
    }
}