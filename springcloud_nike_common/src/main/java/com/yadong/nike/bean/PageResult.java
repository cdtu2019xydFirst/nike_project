package com.yadong.nike.bean;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/19 | 19:20
 **/
public class PageResult <T> {

    private long total;
    private List<T> rows;

    public PageResult() {
    }

    public PageResult(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
