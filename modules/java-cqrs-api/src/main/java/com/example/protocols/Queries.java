package com.example.protocols;

import java.io.Serializable;

public class Queries {

    public static class TeamRequest implements Serializable{
        public final String teamName;

        public TeamRequest(String teamName) {
            this.teamName = teamName;
        }
    }

    public static class PlayerRequest extends TeamRequest {
        public final int jerseyNumber;

        public PlayerRequest(String teamName, int jerseyNumber) {
            super(teamName);
            this.jerseyNumber = jerseyNumber;
        }
    }

    public static class TeamResponse implements Serializable {
        public final TeamRequest request;

        public TeamResponse(TeamRequest request) {
            this.request = request;
        }
    }

    public static class PlayerResponse implements Serializable {
        public final PlayerRequest request;

        public PlayerResponse(PlayerRequest request) {
            this.request = request;
        }
    }

    public static final class PlayerNameRequest extends PlayerRequest {
        public PlayerNameRequest(String teamName, int jerseyNumber) {
            super(teamName, jerseyNumber);
        }
    }

    public static final class PlayerNameResponse extends PlayerResponse {
        public final String name;

        public PlayerNameResponse(PlayerRequest request, String name) {
            super(request);
            this.name = name;
        }
    }

    public static final class PlayerScoreRequest extends PlayerRequest {
        public PlayerScoreRequest(String teamName, int jerseyNumber) {
            super(teamName, jerseyNumber);
        }
    }

    public static final class PlayerScoreResponse extends PlayerResponse {
        public final int score;

        public PlayerScoreResponse(PlayerRequest request, int score) {
            super(request);
            this.score = score;
        }
    }

    public static final class TeamScoreRequest extends TeamRequest {
        public TeamScoreRequest(String teamName) {
            super(teamName);
        }
    }

    public static final class TeamScoreResponse extends TeamResponse {
        public final int score;

        public TeamScoreResponse(TeamRequest request, int score) {
            super(request);
            this.score = score;
        }
    }
}