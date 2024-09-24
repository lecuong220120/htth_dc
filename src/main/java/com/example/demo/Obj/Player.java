package com.example.demo.Obj;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
@Entity
@Table(name = "players")
@Data
public class Player {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer idGame;
    private String name_server;
    private String name;
    private Integer coin;

    public Integer getIdGame() {
        return idGame;
    }

    public void setIdGame(Integer idGame) {
        this.idGame = idGame;
    }

    private Integer type;
    private Integer selection;
    private Integer status;
    private Integer win_coin;
    private Integer coinDaThang = 0;
    private String time;


    public Player(Integer id, Integer id_game, String name_server, String name, Integer coin, Integer type, Integer selection, Integer status, Integer win_coin, Integer coinThamGia, Integer coinDaThang, String time) {
        this.id = id;
        this.name_server = name_server;
        this.name = name;
        this.coin = coin;
        this.type = type;
        this.selection = selection;
        this.status = status;
        this.win_coin = win_coin;
        this.coinDaThang = coinDaThang;
        this.time = time;
    }


    public Player() {
    }


    public Integer getCoinDaThang() {
        return coinDaThang;
    }

    public void setCoinDaThang(Integer coinDaThang) {
        this.coinDaThang = coinDaThang;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName_server() {
        return name_server;
    }

    public void setName_server(String name_server) {
        this.name_server = name_server;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getWin_coin() {
        return win_coin;
    }

    public void setWin_coin(Integer win_coin) {
        this.win_coin = win_coin;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Player(Integer id, String name_server, String name, Integer coin, Integer type, Integer selection, Integer status, Integer win_coin, String time) {
        this.id = id;
        this.name_server = name_server;
        this.name = name;
        this.coin = coin;
        this.type = type;
        this.selection = selection;
        this.status = status;
        this.win_coin = win_coin;
        this.time = time;
    }

    public static String formatNumber(int number) {
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
}
