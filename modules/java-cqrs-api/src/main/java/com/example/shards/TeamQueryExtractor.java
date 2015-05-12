package com.example.shards;

import akka.contrib.pattern.ShardRegion;
import com.example.protocols.Queries;

public class TeamQueryExtractor implements ShardRegion.MessageExtractor {
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
}