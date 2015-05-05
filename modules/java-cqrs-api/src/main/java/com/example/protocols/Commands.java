package com.example.protocols;

import java.io.Serializable;

public class Commands {

    public static class TeamCommand implements Serializable {
        public final String teamName;

        public TeamCommand(String teamName) {
            this.teamName = teamName;
        }
    }

    public static class PlayerCommand extends TeamCommand {
        public final int jerseyNumber;

        public PlayerCommand(String teamName, int jerseyNumber) {
            super(teamName);
            this.jerseyNumber = jerseyNumber;
        }
    }


    public static final class NewTeam extends TeamCommand {
        public NewTeam(String teamName) {
            super(teamName);
        }
    }

    public static final class AddPlayer extends PlayerCommand {
        public final String name;

        public AddPlayer(String teamName, int jerseyNumber, String name) {
            super(teamName, jerseyNumber);
            this.name = name;
        }
    }

    public static final class PlayerScore extends PlayerCommand {
        public final int score;

        public PlayerScore(String teamName, int jerseyNumber, int score) {
            super(teamName, jerseyNumber);
            this.score = score;
        }
    }
}
