package actors;

import akka.actor.ActorSystem;
import akka.contrib.pattern.ClusterSharding;
import akka.contrib.pattern.ShardRegion;
import com.example.protocols.Commands;
import com.example.protocols.Queries;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClusterSystem {
    private static final ActorSystem actorSystem;
    private static final ActorSystem commandSystem;
    private static final ActorSystem querySystem;

    static {
        actorSystem = ActorSystem.create("Application");

        ShardRegion.MessageExtractor teamCommandExtractor = new ShardRegion.MessageExtractor() {

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

        ShardRegion.MessageExtractor playerCommandExtractor = new ShardRegion.MessageExtractor() {

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

        ShardRegion.MessageExtractor teamQueryExtractor = new ShardRegion.MessageExtractor() {

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

        ShardRegion.MessageExtractor playerQueryExtractor = new ShardRegion.MessageExtractor() {

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


        final Config commandConfig = ConfigFactory.parseString("akka.cluster.seed-nodes = [\"akka.tcp://GameClusterSystemCommand@127.0.0.1:2551\"]").
                withFallback(ConfigFactory.load());

        commandSystem = ActorSystem.create("GameClusterSystemCommand", commandConfig);
        ClusterSharding.get(commandSystem).start("TeamsRegionCommand", null, teamCommandExtractor);
        ClusterSharding.get(commandSystem).start("PlayerRegionCommand", null, playerCommandExtractor);

        final Config queryConfig = ConfigFactory.parseString("akka.cluster.seed-nodes = [\"akka.tcp://GameClusterSystemQuery@127.0.0.1:2553\"]").
                withFallback(ConfigFactory.load());

        querySystem = ActorSystem.create("GameClusterSystemQuery", queryConfig);
        ClusterSharding.get(querySystem).start("TeamsRegionQuery", null, teamQueryExtractor);
        ClusterSharding.get(querySystem).start("PlayerRegionQuery", null, playerQueryExtractor);
    }

    public static ActorSystem getActorSystem() {
        return actorSystem;
    }

    public static ActorSystem getCommandSystem() {
        return commandSystem;
    }

    public static ActorSystem getQuerySystem() {
        return querySystem;
    }
}
