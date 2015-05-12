package com.example.shards;

import akka.contrib.pattern.ShardRegion;
import com.example.protocols.Commands;

public class PlayerCommandExtractor implements ShardRegion.MessageExtractor {
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
}