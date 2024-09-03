package com.example.demo.Obj;

public class TimeSocket {
    private Integer server;
    private Integer time;

    public Integer getServer() {
        return server;
    }

    public void setServer(Integer server) {
        this.server = server;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TimeSocket{" +
                "server=" + server +
                ", time=" + time +
                '}';
    }
}
