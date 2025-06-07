package com.metamix.metamix.dto;

public class HourlyWeatherCardDTO {
    private String time;
    private Double temperature;
    private String icon;
    private String description;

    public HourlyWeatherCardDTO() {}

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public Double getTemperature() {
        return temperature;
    }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getWeatherDescription() {
        return description;
    }
    public void setWeatherDescription(String description) {
        this.description = description;
    }
}