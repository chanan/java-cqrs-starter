package actors;

import akka.actor.ActorSystem;
import akka.contrib.pattern.ClusterSharding;
import com.example.shards.PlayerCommandExtractor;
import com.example.shards.TeamCommandExtractor;
import com.example.shards.TeamQueryExtractor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClusterSystem {
    private static final ActorSystem commandSystem;
    private static final ActorSystem querySystem;

    static {
        final Config commandConfig = ConfigFactory
                .parseString("akka.cluster.seed-nodes = [\"akka.tcp://GameClusterSystemCommand@127.0.0.1:2551\"]")
                .withFallback(ConfigFactory.load());

        commandSystem = ActorSystem.create("GameClusterSystemCommand", commandConfig);
        ClusterSharding.get(commandSystem).start("TeamsRegion", null, new TeamCommandExtractor());
        ClusterSharding.get(commandSystem).start("PlayerRegion", null, new PlayerCommandExtractor());

        final Config queryConfig = ConfigFactory
                .parseString("akka.cluster.seed-nodes = [\"akka.tcp://GameClusterSystemQuery@127.0.0.1:2553\"]")
                .withFallback(ConfigFactory.load());

        querySystem = ActorSystem.create("GameClusterSystemQuery", queryConfig);
        ClusterSharding.get(querySystem).start("TeamsRegion", null, new TeamQueryExtractor());
        ClusterSharding.get(querySystem).start("PlayerRegion", null, new TeamQueryExtractor());
    }

    public static ActorSystem getCommandSystem() {
        return commandSystem;
    }

    public static ActorSystem getQuerySystem() {
        return querySystem;
    }
}