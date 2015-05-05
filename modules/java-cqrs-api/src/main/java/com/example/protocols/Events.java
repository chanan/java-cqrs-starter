package com.example.protocols;

import java.io.Serializable;

public class Events {
    public static class TeamEvent implements Serializable {
        public final String teamName;

        public TeamEvent(String teamName) {
            this.teamName = teamName;
        }
    }

    public static class PlayerEvent extends TeamEvent {
        public final int jerseyNumber;

        public PlayerEvent(String teamName, int jerseyNumber) {
            super(teamName);
            this.jerseyNumber = jerseyNumber;
        }
    }

    public static final class NewTeamCreated extends TeamEvent {
        public NewTeamCreated(String teamName) {
            super(teamName);
        }
    }

    public static final class NewPlayerCreated extends PlayerEvent {
        public final String name;

        public NewPlayerCreated(String teamName, int jerseyNumber, String name) {
            super(teamName, jerseyNumber);
            this.name = name;
        }
    }

    public static final class PlayerScored extends PlayerEvent {
        public final int score;

        public PlayerScored(String teamName, int jerseyNumber, int score) {
            super(teamName, jerseyNumber);
            this.score = score;
        }
    }
}
