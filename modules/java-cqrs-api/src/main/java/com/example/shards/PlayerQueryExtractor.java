package com.example.shards;

import akka.contrib.pattern.ShardRegion;
import com.example.protocols.Queries;

public class PlayerQueryExtractor implements ShardRegion.MessageExtractor {
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
}