package com.metamix.metamix.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoCodingService {
    private final RestTemplate restTemplate = new RestTemplate();

    public double[] geocode(String location) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?q=" + location + "&format=json&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "metamix-weather-app");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JSONArray array = new JSONArray(response.getBody());

            if (array.length() == 0) {
                throw new RuntimeException("Location not found: " + location);
            }

            JSONObject obj = array.getJSONObject(0);
            double lat = obj.getDouble("lat");
            double lon = obj.getDouble("lon");

            return new double[]{lat, lon};
        } catch (Exception e) {
            throw new RuntimeException("Geocoding failed for: " + location, e);
        }
    }
}
