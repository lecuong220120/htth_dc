package com.example.demo.NsoObj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerNSO {
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("server")
    private int server;
    @JsonProperty("game")
    private int game;
    @JsonProperty("name")
    private String name;
    @JsonProperty("selection")
    private int selection;
    @JsonProperty("status")
    private int status;
    @JsonProperty("xu")
    private long xu;
    @JsonProperty("xu_win")
    private long xuWin;
    @JsonProperty("server_name")
    private String serverName;
    @JsonProperty("selection_str")
    private String selectionStr;
    @JsonProperty("time")
    private String time;

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    public int getGame() {
        return game;
    }

    public void setGame(int game) {
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getXu() {
        return xu;
    }

    public void setXu(long xu) {
        this.xu = xu;
    }

    public long getXuWin() {
        return xuWin;
    }

    public void setXuWin(long xuWin) {
        this.xuWin = xuWin;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getSelectionStr() {
        return selectionStr;
    }

    public void setSelectionStr(String selectionStr) {
        this.selectionStr = selectionStr;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
