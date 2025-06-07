package com.metamix.metamix.service;

import com.metamix.metamix.dto.HourlyWeatherCardDTO;
import com.metamix.metamix.dto.WeatherCardDTO;
import com.metamix.metamix.service.GeoCodingService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetNoService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final GeoCodingService geocodingService;

    public MetNoService(GeoCodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    public WeatherCardDTO getCurrentWeather(String location, String day) {
    JSONObject json = fetchMetNoJson(location);
    JSONArray timeseries = json.getJSONObject("properties").getJSONArray("timeseries");

    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime targetTime;

    if ("tomorrow".equalsIgnoreCase(day)) {
        targetTime = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    } else {
        targetTime = now;
    }

    JSONObject target = timeseries.getJSONObject(0);
    ZonedDateTime closestTime = ZonedDateTime.parse(target.getString("time"));
    long minDiff = Math.abs(closestTime.toEpochSecond() - targetTime.toEpochSecond());

    for (int i = 1; i < timeseries.length(); i++) {
        JSONObject ts = timeseries.getJSONObject(i);
        ZonedDateTime time = ZonedDateTime.parse(ts.getString("time"));
        long diff = Math.abs(time.toEpochSecond() - targetTime.toEpochSecond());

        if (diff < minDiff) {
            minDiff = diff;
            target = ts;
        }
    }

    JSONObject details = target.getJSONObject("data").getJSONObject("instant").getJSONObject("details");

    WeatherCardDTO dto = new WeatherCardDTO();
    dto.setLocation(location);
    dto.setDate(target.getString("time"));
    dto.setTemperature(details.getDouble("air_temperature"));
    dto.setHumidity(details.optDouble("relative_humidity", 0) + "%");
    dto.setVisibility("N/A");
    dto.setPressure(details.optDouble("air_pressure_at_sea_level", 0) + " hPa");
    dto.setWind(details.optDouble("wind_speed", 0) + " m/s");

    if (target.getJSONObject("data").has("next_1_hours")) {
        String symbol = target.getJSONObject("data")
                              .getJSONObject("next_1_hours")
                              .getJSONObject("summary")
                              .getString("symbol_code");
        dto.setWeatherDescription(symbol);
    } else {
        dto.setWeatherDescription("unknown");
    }

    return dto;
}

    public List<HourlyWeatherCardDTO> getHourlyWeather(String location, String day) {
        JSONObject json = fetchMetNoJson(location);
        JSONArray timeseries = json.getJSONObject("properties").getJSONArray("timeseries");

        List<HourlyWeatherCardDTO> list = new ArrayList<>();
        for (int i = 0; i < timeseries.length() && list.size() < 12; i += 2) {
            JSONObject ts = timeseries.getJSONObject(i);
            JSONObject details = ts.getJSONObject("data").getJSONObject("instant").getJSONObject("details");

            HourlyWeatherCardDTO dto = new HourlyWeatherCardDTO();
            dto.setTime(ts.getString("time"));
            dto.setTemperature(details.getDouble("air_temperature"));

            if (ts.getJSONObject("data").has("next_1_hours")) {
                String symbol = ts.getJSONObject("data").getJSONObject("next_1_hours")
                        .getJSONObject("summary").getString("symbol_code");
                dto.setWeatherDescription(symbol);
            } else {
                dto.setWeatherDescription("unknown");
            }

            list.add(dto);
        }

        return list;
    }

    private JSONObject fetchMetNoJson(String location) {
    try {
        double[] coords = geocodingService.geocode(location);
        String lat = String.valueOf(coords[0]);
        String lon = String.valueOf(coords[1]);

        String url = String.format(
                "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=%s&lon=%s", lat, lon);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "metamix-weather-app")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    } catch (IOException | InterruptedException e) {
        throw new RuntimeException("Failed to fetch data from met.no", e);
    }
}

}