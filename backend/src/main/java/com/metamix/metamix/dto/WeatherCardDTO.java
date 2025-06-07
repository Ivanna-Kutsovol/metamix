package com.metamix.metamix.dto;

public class WeatherCardDTO {
    private String location;
    private Double temperature;
    private String description;
    private String humidity;
    private String pressure;
    private String visibility;
    private String wind;
    private String date;

    public WeatherCardDTO() {}

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public Double getTemperature() {
        return temperature;
    }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getWeatherDescription() {
        return description;
    }
    public void setWeatherDescription(String description) {
        this.description = description;
    }

    public String getHumidity() {
        return humidity;
    }
    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }
    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getVisibility() {
        return visibility;
    }
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getWind() {
        return wind;
    }
    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}