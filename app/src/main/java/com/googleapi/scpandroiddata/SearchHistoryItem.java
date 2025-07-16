package com.googleapi.scpandroiddata;



import java.util.Date;
import java.util.UUID;

public class SearchHistoryItem {
    private String id;
    private String value;
    private Date createdDate;

    public SearchHistoryItem() {
        this.id = UUID.randomUUID().toString();
        this.createdDate = new Date();
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}