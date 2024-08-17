package com.example.demo.Obj;

public class DCTK {
    private Integer type;
    private Integer selection;
    private Integer coin;
    private Integer id_server;

    public DCTK(Integer type, Integer selection, Integer coin, Integer id_server) {
        this.type = type;
        this.selection = selection;
        this.coin = coin;
        this.id_server = id_server;
    }

    public DCTK() {
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSelection() {
        return selection;
    }

    public void setSelection(Integer selection) {
        this.selection = selection;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public Integer getId_server() {
        return id_server;
    }

    public void setId_server(Integer id_server) {
        this.id_server = id_server;
    }
}
