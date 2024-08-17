package com.example.demo.Obj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class History {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("id_server")
    private Integer id_server;
    @JsonProperty("name_server")
    private String name_server;
    @JsonProperty("rd_num")
    private Integer rd_num;
    @JsonProperty("rd_coin")
    private String rd_coin;
    @JsonProperty("percent_win")
    private String percent_win;
    @JsonProperty("sum")
    private String sum;
    @JsonProperty("plus_string")
    private String plus_string;
    @JsonProperty("result_tk")
    private Integer result_tk;
    @JsonProperty("result_cd")
    private Integer result_cd;
    @JsonProperty("result_kq")
    private Integer result_kq;
    @JsonProperty("last_num")
    private Integer last_num;
    @JsonProperty("start")
    private String start;
    @JsonProperty("stop")
    private String stop;
    @JsonProperty("view_key")
    private String view_key;

    public History() {
    }

    public History(Integer id, Integer id_server, String name_server, Integer rd_num, String rd_coin, String percent_win, String sum, String plus_string, Integer result_tk, Integer result_cd, Integer result_kq, Integer last_num, String start, String stop, String view_key) {
        this.id = id;
        this.id_server = id_server;
        this.name_server = name_server;
        this.rd_num = rd_num;
        this.rd_coin = rd_coin;
        this.percent_win = percent_win;
        this.sum = sum;
        this.plus_string = plus_string;
        this.result_tk = result_tk;
        this.result_cd = result_cd;
        this.result_kq = result_kq;
        this.last_num = last_num;
        this.start = start;
        this.stop = stop;
        this.view_key = view_key;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_server() {
        return id_server;
    }

    public void setId_server(Integer id_server) {
        this.id_server = id_server;
    }

    public String getName_server() {
        return name_server;
    }

    public void setName_server(String name_server) {
        this.name_server = name_server;
    }

    public Integer getRd_num() {
        return rd_num;
    }

    public void setRd_num(Integer rd_num) {
        this.rd_num = rd_num;
    }

    public String getRd_coin() {
        return rd_coin;
    }

    public void setRd_coin(String rd_coin) {
        this.rd_coin = rd_coin;
    }

    public String getPercent_win() {
        return percent_win;
    }

    public void setPercent_win(String percent_win) {
        this.percent_win = percent_win;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getPlus_string() {
        return plus_string;
    }

    public void setPlus_string(String plus_string) {
        this.plus_string = plus_string;
    }

    public Integer getResult_tk() {
        return result_tk;
    }

    public void setResult_tk(Integer result_tk) {
        this.result_tk = result_tk;
    }

    public Integer getResult_cd() {
        return result_cd;
    }

    public void setResult_cd(Integer result_cd) {
        this.result_cd = result_cd;
    }

    public Integer getResult_kq() {
        return result_kq;
    }

    public void setResult_kq(Integer result_kq) {
        this.result_kq = result_kq;
    }

    public Integer getLast_num() {
        return last_num;
    }

    public void setLast_num(Integer last_num) {
        this.last_num = last_num;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getView_key() {
        return view_key;
    }

    public void setView_key(String view_key) {
        this.view_key = view_key;
    }
}
