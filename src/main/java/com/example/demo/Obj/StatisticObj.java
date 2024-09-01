package com.example.demo.Obj;

public class StatisticObj {
    private Integer moneyChange;
    private Integer money;
    private Integer countWin;
    private Integer countLose;
    private String result;
    private boolean type; //true = dc, false = tk

    public Integer getMoneyChange() {
        return moneyChange;
    }

    public void setMoneyChange(Integer moneyChange) {
        this.moneyChange = moneyChange;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getCountWin() {
        return countWin;
    }

    public void setCountWin(Integer countWin) {
        this.countWin = countWin;
    }

    public Integer getCountLose() {
        return countLose;
    }

    public void setCountLose(Integer countLose) {
        this.countLose = countLose;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
