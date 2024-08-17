package com.example.demo.DTO;

import com.example.demo.Obj.History;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class HistoryDTO {
    @JsonProperty("total_page")
    private Integer total_page;
    @JsonProperty("page")
    private Integer page;
    @JsonProperty("total")
    private Integer total;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("histories")
    private List<History> histories;

    public HistoryDTO() {
    }

    public HistoryDTO(Integer total_page, Integer page, Integer total, Integer size, List<History> histories) {
        this.total_page = total_page;
        this.page = page;
        this.total = total;
        this.size = size;
        this.histories = histories;
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

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }
}
