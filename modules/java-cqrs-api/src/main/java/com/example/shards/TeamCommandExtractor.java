package com.example.shards;

import akka.contrib.pattern.ShardRegion;
import com.example.protocols.Commands;

public class TeamCommandExtractor implements ShardRegion.MessageExtractor {
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
}