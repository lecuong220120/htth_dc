package com.example.demo.NsoObj;

import com.example.demo.Obj.Player;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GameData {
    @JsonProperty("players")
    private List<PlayerNSO> players;
    @JsonProperty("hl_players")
    private List<Object> hlPlayers;
    @JsonProperty("user")
    private User user;

    // Getters and Setters
    public List<PlayerNSO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerNSO> players) {
        this.players = players;
    }

    public List<Object> getHlPlayers() {
        return hlPlayers;
    }

    public void setHlPlayers(List<Object> hlPlayers) {
        this.hlPlayers = hlPlayers;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
