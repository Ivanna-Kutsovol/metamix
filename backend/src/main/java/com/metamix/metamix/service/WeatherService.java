package com.metamix.metamix.service;

import com.metamix.metamix.dto.WeatherCardDTO;
import com.metamix.metamix.dto.HourlyWeatherCardDTO;
import com.metamix.metamix.service.GeoCodingService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final GeoCodingService geocodingService;

    public WeatherService(GeoCodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    public WeatherCardDTO getWeather(String location, String source) {
        double[] coords = geocodingService.geocode(location);

        if ("wttr".equalsIgnoreCase(source)) {
            return fetchFromWttr(location);
        } else if ("met".equalsIgnoreCase(source)) {
            return fetchFromMet(location);
        } else {
            throw new IllegalArgumentException("Unknown source: " + source);
        }
    }

    public List<HourlyWeatherCardDTO> getHourlyWeather(String location, String source, String day) {
        double[] coords = geocodingService.geocode(location);

        if ("wttr".equalsIgnoreCase(source)) {
            return fetchHourlyFromWttr(location, day);
        } else if ("met".equalsIgnoreCase(source)) {
            return fetchHourlyFromMet(location, day);
        } else {
            throw new IllegalArgumentException("Unknown source: " + source);
        }
    }

    // ======== WTTR =========

    private WeatherCardDTO fetchFromWttr(String location) {
        String url = "https://wttr.in/" + location + "?format=j1";
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);

        JSONObject current = json.getJSONArray("current_condition").getJSONObject(0);

        WeatherCardDTO dto = new WeatherCardDTO();
        dto.setLocation(location);
        dto.setTemperature(current.getDouble("air_temperature"));
        dto.setWeatherDescription(current.getJSONArray("weatherDesc").getJSONObject(0).getString("value"));
        dto.setHumidity(current.getString("humidity") + "%");
        dto.setPressure(current.getString("pressure") + " hPa");
        dto.setVisibility(current.getString("visibility") + " km");
        dto.setWind(current.getString("windspeedKmph") + " km/h");
        dto.setDate(json.getJSONArray("weather").getJSONObject(0).getString("date"));

        return dto;
    }

    private List<HourlyWeatherCardDTO> fetchHourlyFromWttr(String location, String day) {
        String url = "https://wttr.in/" + location + "?format=j1";
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);
        JSONArray hourly = json.getJSONArray("weather").getJSONObject(0).getJSONArray("hourly");

        List<HourlyWeatherCardDTO> result = new ArrayList<>();

        for (int i = 0; i < hourly.length(); i += 2) {
            JSONObject hourObj = hourly.getJSONObject(i);
            HourlyWeatherCardDTO dto = new HourlyWeatherCardDTO();
            dto.setTime(hourObj.getString("time"));
            dto.setTemperature(hourObj.getDouble("air_temperature"));
            dto.setIcon(hourObj.getJSONArray("weatherIconUrl").getJSONObject(0).getString("value"));
            result.add(dto);
        }

        return result;
    }

    // ======== MET.NO =========

    private WeatherCardDTO fetchFromMet(String location) {
        double[] coords = geocodingService.geocode(location);
        String lat = String.valueOf(coords[0]);
        String lon = String.valueOf(coords[1]);

        String url = "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=" + lat + "&lon=" + lon;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mymetamix");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JSONObject json = new JSONObject(response.getBody());
        JSONObject timeseries = json.getJSONObject("properties").getJSONArray("timeseries").getJSONObject(0);
        JSONObject details = timeseries.getJSONObject("data").getJSONObject("instant").getJSONObject("details");

        WeatherCardDTO dto = new WeatherCardDTO();
        dto.setLocation(location);
        dto.setDate(timeseries.getString("time"));
        dto.setTemperature(details.getDouble("air_temperature"));
        dto.setHumidity(details.getDouble("relative_humidity") + "%");
        dto.setPressure(details.getDouble("air_pressure_at_sea_level") + " hPa");
        dto.setWind(details.getDouble("wind_speed") + " m/s");
        dto.setVisibility("N/A");
        dto.setWeatherDescription("From met.no");

        return dto;
    }

    private List<HourlyWeatherCardDTO> fetchHourlyFromMet(String location, String day) {
        double[] coords = geocodingService.geocode(location);
        String lat = String.valueOf(coords[0]);
        String lon = String.valueOf(coords[1]);

        String url = "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=" + lat + "&lon=" + lon;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mymetamix");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JSONObject json = new JSONObject(response.getBody());
        JSONArray timeseries = json.getJSONObject("properties").getJSONArray("timeseries");

        List<HourlyWeatherCardDTO> result = new ArrayList<>();

        for (int i = 0; i < timeseries.length() && result.size() < 12; i += 2) {
            JSONObject hourObj = timeseries.getJSONObject(i);
            JSONObject details = hourObj.getJSONObject("data").getJSONObject("instant").getJSONObject("details");

            HourlyWeatherCardDTO dto = new HourlyWeatherCardDTO();
            dto.setTime(hourObj.getString("time"));
            dto.setTemperature(details.getDouble("air_temperature"));
            dto.setIcon("custom-met-icon");

            result.add(dto);
        }

        return result;
    }
}