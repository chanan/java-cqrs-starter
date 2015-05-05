package com.example.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Team implements Serializable {
    private String teamName;
    private Set<Integer> players;
    private int score;

    public Team(String teamName) {
        this.teamName = teamName;
        players = new HashSet<>();
    }

    private Team() {
    }

    public String getTeamName() {
        return teamName;
    }

    public Set<Integer> getPlayers() {
        return players;
    }

    public void addPlayer(int playerJerseyNumber) {
        if(!players.contains(playerJerseyNumber)) players.add(playerJerseyNumber);
    }

    public int getScore() {
        return score;
    }

    public void score(int score) {
        this.score =+ score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        if (score != team.score) return false;
        if (!teamName.equals(team.teamName)) return false;
        return players.equals(team.players);

    }

    @Override
    public int hashCode() {
        int result = teamName.hashCode();
        result = 31 * result + players.hashCode();
        result = 31 * result + score;
        return result;
    }
}