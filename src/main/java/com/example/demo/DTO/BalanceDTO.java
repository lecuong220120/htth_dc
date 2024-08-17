package com.example.demo.DTO;

import com.example.demo.Obj.Balance;
import com.example.demo.Obj.History;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BalanceDTO {
    @JsonProperty("total_page")
    private Integer total_page;
    @JsonProperty("page")
    private Integer page;
    @JsonProperty("total")
    private Integer total;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("balance_histories")
    private List<Balance> balances;

    public BalanceDTO() {
    }

    public BalanceDTO(Integer total_page, Integer page, Integer total, Integer size, List<Balance> balances) {
        this.total_page = total_page;
        this.page = page;
        this.total = total;
        this.size = size;
        this.balances = balances;
    }

    public Integer getTotal_page() {
        return total_page;
    }

    public void setTotal_page(Integer total_page) {
        this.total_page = total_page;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }
}
