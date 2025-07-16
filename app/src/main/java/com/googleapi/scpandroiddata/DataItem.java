package com.googleapi.scpandroiddata;



public class DataItem {
    private String temp_value;
    private String hum_value;
    private String air_pressure_value;
    private String date_time;

    public String getTemp_value() {
        return temp_value;
    }

    public void setTemp_value(String temp_value) {
        this.temp_value = temp_value;
    }

    public String getHum_value() {
        return hum_value;
    }

    public void setHum_value(String hum_value) {
        this.hum_value = hum_value;
    }

    public String getAir_pressure_value() {
        return air_pressure_value;
    }

    public void setAir_pressure_value(String air_pressure_value) {
        this.air_pressure_value = air_pressure_value;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}