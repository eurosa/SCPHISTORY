package com.googleapi.scpandroiddata;

import java.util.List;

public class ApiResponse {
    private List<DataItem> data;
    private int count;

    public List<DataItem> getData() {
        return data;
    }

    public void setData(List<DataItem> data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}