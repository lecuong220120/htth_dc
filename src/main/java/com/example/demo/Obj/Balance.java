package com.example.demo.Obj;

import com.example.demo.Service.DctkService;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DecimalFormat;

public class Balance {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("type_name")
    private String type_name;
    @JsonProperty("id_user")
    private Integer id_user;
    @JsonProperty("username")
    private String username;
    @JsonProperty("id_server")
    private Integer id_server;
    @JsonProperty("before")
    private Integer before;
    @JsonProperty("after")
    private Integer after;
    @JsonProperty("change")
    private Integer change;
    @JsonProperty("note")
    private String note;
    @JsonProperty("time")
    private String time;

    public Balance() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public Integer getId_user() {
        return id_user;
    }

    public void setId_user(Integer id_user) {
        this.id_user = id_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId_server() {
        return id_server;
    }

    public void setId_server(Integer id_server) {
        this.id_server = id_server;
    }

    public Integer getBefore() {
        return before;
    }

    public void setBefore(Integer before) {
        this.before = before;
    }

    public Integer getAfter() {
        return after;
    }

    public void setAfter(Integer after) {
        this.after = after;
    }

    public Integer getChange() {
        return change;
    }

    public void setChange(Integer change) {
        this.change = change;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Balance(Integer id, Integer type, String type_name, Integer id_user, String username, Integer id_server, Integer before, Integer after, Integer change, String note, String time) {
        this.id = id;
        this.type = type;
        this.type_name = type_name;
        this.id_user = id_user;
        this.username = username;
        this.id_server = id_server;
        this.before = before;
        this.after = after;
        this.change = change;
        this.note = note;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Tong: " + formatNumber(after) + ", van truoc = "+ formatNumber(change);
    }
    public String formatNumber(int number) {
        DecimalFormat df;
        boolean isNegative = number < 0;
        int absNumber = Math.abs(number);

        if (absNumber < 1000) {
            // Không cần định dạng cho số nhỏ hơn 1000
            return (isNegative ? "-" : "") + absNumber;
        } else if (absNumber < 1_000_000) {
            // Định dạng với hàng nghìn
            df = new DecimalFormat("#,###");
        } else if (absNumber < 1_000_000_000) {
            // Định dạng với hàng triệu
            df = new DecimalFormat("#,###.##M");
            return (isNegative ? "-" : "") + df.format(absNumber / 1_000_000.0);
        } else {
            // Định dạng với hàng tỷ
            df = new DecimalFormat("#,###.##B");
            return (isNegative ? "-" : "") + df.format(absNumber / 1_000_000_000.0);
        }

        return (isNegative ? "-" : "") + df.format(absNumber);
    }

    public static void main(String[] args) throws InterruptedException {
        DctkService dctkService = new DctkService();
        dctkService.playVersion4();
    }
}
