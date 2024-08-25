package com.example.demo.Obj;

public class StatisticObj {
    private Integer moneyChangeDC;
    private Integer moneyChangeTK;
    private Integer moneyTK;
    private Integer moneyDC;
    private Integer countWinDC;
    private Integer countWinTK;
    private Integer countLoseDC;
    private Integer countLoseTK;
    private String resultDC;
    private String resultTK;

    public Integer getCountWinDC() {
        return countWinDC;
    }

    public void setCountWinDC(Integer countWinDC) {
        this.countWinDC = countWinDC;
    }

    public Integer getCountWinTK() {
        return countWinTK;
    }

    public void setCountWinTK(Integer countWinTK) {
        this.countWinTK = countWinTK;
    }

    public Integer getCountLoseDC() {
        return countLoseDC;
    }

    public void setCountLoseDC(Integer countLoseDC) {
        this.countLoseDC = countLoseDC;
    }

    public Integer getCountLoseTK() {
        return countLoseTK;
    }

    public void setCountLoseTK(Integer countLoseTK) {
        this.countLoseTK = countLoseTK;
    }

    public StatisticObj() {
    }

    public StatisticObj(Integer moneyChangeDC, Integer moneyChangeTK, Integer moneyTK, Integer moneyDC, String resultDC, String resultTK) {
        this.moneyChangeDC = moneyChangeDC;
        this.moneyChangeTK = moneyChangeTK;
        this.moneyTK = moneyTK;
        this.moneyDC = moneyDC;
        this.resultDC = resultDC;
        this.resultTK = resultTK;
    }

    public Integer getMoneyChangeDC() {
        return moneyChangeDC;
    }

    public void setMoneyChangeDC(Integer moneyChangeDC) {
        this.moneyChangeDC = moneyChangeDC;
    }

    public Integer getMoneyChangeTK() {
        return moneyChangeTK;
    }

    public void setMoneyChangeTK(Integer moneyChangeTK) {
        this.moneyChangeTK = moneyChangeTK;
    }

    public Integer getMoneyTK() {
        return moneyTK;
    }

    public void setMoneyTK(Integer moneyTK) {
        this.moneyTK = moneyTK;
    }

    public Integer getMoneyDC() {
        return moneyDC;
    }

    public void setMoneyDC(Integer moneyDC) {
        this.moneyDC = moneyDC;
    }

    public String getResultDC() {
        return resultDC;
    }

    public void setResultDC(String resultDC) {
        this.resultDC = resultDC;
    }

    public String getResultTK() {
        return resultTK;
    }

    public void setResultTK(String resultTK) {
        this.resultTK = resultTK;
    }
}
