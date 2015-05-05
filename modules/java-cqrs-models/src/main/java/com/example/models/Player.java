package com.example.models;

import java.io.Serializable;

public class Player implements Serializable {
    private String teamName;
    private int jerseyNumber;
    private String name;
    private int points;

    private Player() {
    }

    public Player(String teamName, int jerseryNumber, String name) {
        this.jerseyNumber = jerseryNumber;
        this.name = name;
        this.points = 0;
    }

    public String getId() {
        return teamName + "-" + jerseyNumber;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void score(int addPoints) {
        points += addPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (jerseyNumber != player.jerseyNumber) return false;
        if (points != player.points) return false;
        if (!teamName.equals(player.teamName)) return false;
        return name.equals(player.name);

    }

    @Override
    public int hashCode() {
        int result = teamName.hashCode();
        result = 31 * result + jerseyNumber;
        result = 31 * result + name.hashCode();
        result = 31 * result + points;
        return result;
    }
}
