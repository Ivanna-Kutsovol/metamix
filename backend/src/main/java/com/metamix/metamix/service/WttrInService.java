package com.metamix.metamix.service;

import com.metamix.metamix.dto.WeatherCardDTO;
import com.metamix.metamix.dto.HourlyWeatherCardDTO;
import com.metamix.metamix.service.GeoCodingService;
import com.metamix.metamix.util.TimeZoneUtil;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class WttrInService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final GeoCodingService geocodingService;

    public WttrInService(GeoCodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    public WeatherCardDTO getCurrentWeather(String location, String day) {
    try {
        double[] coords = geocodingService.geocode(location);
        String lat = String.valueOf(coords[0]);
        String lon = String.valueOf(coords[1]);

        String url = String.format("https://wttr.in/%s,%s?format=j1", lat, lon);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        JSONArray weatherArray = json.getJSONArray("weather");

        WeatherCardDTO dto = new WeatherCardDTO();

        if ("tomorrow".equalsIgnoreCase(day) && weatherArray.length() > 1) {
                JSONObject targetDayWeather = weatherArray.getJSONObject(1);
                JSONObject midday = targetDayWeather.getJSONArray("hourly").getJSONObject(4);

                dto.setTemperature(targetDayWeather.getDouble("avgtempC"));
                dto.setHumidity(midday.getString("humidity") + "%");
                dto.setVisibility(midday.getString("visibility") + " km");
                dto.setPressure(midday.getString("pressure") + " hPa");
                dto.setWind(midday.getString("windspeedKmph") + " km/h");
                dto.setWeatherDescription(midday.getJSONArray("weatherDesc").getJSONObject(0).getString("value"));
            } else {
                JSONObject current = json.getJSONArray("current_condition").getJSONObject(0);
                dto.setTemperature(current.getDouble("temp_C"));
                dto.setHumidity(current.getString("humidity") + "%");
                dto.setVisibility(current.getString("visibility") + " km");
                dto.setPressure(current.getString("pressure") + " hPa");
                dto.setWind(current.getString("windspeedKmph") + " km/h");
                dto.setWeatherDescription(current.getJSONArray("weatherDesc").getJSONObject(0).getString("value"));
            }

        ZoneId zone = TimeZoneUtil.getZoneId(coords[0], coords[1]);

        ZonedDateTime localDate = ZonedDateTime.now(zone);

        dto.setLocation(location);
        DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        dto.setDate(localDate.format(isoFormatter));
        

        return dto;
    } catch (IOException | InterruptedException e) {
        throw new RuntimeException("Failed to fetch weather from wttr.in", e);
    }
}

    public List<HourlyWeatherCardDTO> getHourlyWeather(String location, String day) {
    List<HourlyWeatherCardDTO> result = new ArrayList<>();
    try {
        double[] coords = geocodingService.geocode(location);
        String lat = String.valueOf(coords[0]);
        String lon = String.valueOf(coords[1]);
        String url = String.format("https://wttr.in/%s,%s?format=j1", lat, lon);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        JSONArray weatherArray  = json.getJSONArray("weather");

        int dayIndex = "tomorrow".equalsIgnoreCase(day) && weatherArray.length() > 1 ? 1 : 0;

        JSONArray hourlyArray = weatherArray.getJSONObject(dayIndex).getJSONArray("hourly");

        ZoneId zone = TimeZoneUtil.getZoneId(coords[0], coords[1]);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < hourlyArray.length(); i += 2) {
            JSONObject hour = hourlyArray.getJSONObject(i);
            HourlyWeatherCardDTO dto = new HourlyWeatherCardDTO();

            String rawTime = hour.getString("time");
            String formattedRawTime = String.format("%04d", Integer.parseInt(rawTime));

            ZonedDateTime dateTime = ZonedDateTime.now(zone)
                    .withHour(Integer.parseInt(formattedRawTime.substring(0, 2)))
                    .withMinute(Integer.parseInt(formattedRawTime.substring(2, 4)))
                    .withSecond(0)
                    .withNano(0);

            dto.setTime(outputFormatter.format(dateTime));
            dto.setTemperature(hour.getDouble("tempC"));
            dto.setWeatherDescription(hour.getJSONArray("weatherDesc").getJSONObject(0).getString("value"));
            
            result.add(dto);
        }
        return result;
    } catch (IOException | InterruptedException e) {
        throw new RuntimeException("Failed to fetch hourly from wttr.in", e);
    }
}

}
