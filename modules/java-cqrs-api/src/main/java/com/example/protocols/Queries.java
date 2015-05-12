package com.example.protocols;

import java.io.Serializable;

public class Queries {

    public static class Request implements Serializable { }

    public static class TeamRequest extends Request {
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

    public static final class PlayerRowRequest extends PlayerRequest {
        public PlayerRowRequest(String teamName, int jerseyNumber) {
            super(teamName, jerseyNumber);
        }
    }

    public static final class PlayerRowResponse extends PlayerResponse {
        public final String name;
        public final int score;

        public PlayerRowResponse(PlayerRequest request, String name, int score) {
            super(request);
            this.name = name;
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