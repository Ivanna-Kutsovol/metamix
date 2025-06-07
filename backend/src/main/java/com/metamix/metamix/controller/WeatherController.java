package com.metamix.metamix.controller;

import com.metamix.metamix.dto.WeatherCardDTO;
import com.metamix.metamix.dto.HourlyWeatherCardDTO;
import com.metamix.metamix.service.MetNoService;
import com.metamix.metamix.service.WttrInService;
import com.metamix.metamix.service.WeatherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WttrInService wttrInService;
    private final MetNoService metNoService;

    public WeatherController(WttrInService wttrInService, MetNoService metNoService) {
        this.wttrInService = wttrInService;
        this.metNoService = metNoService;
    }

    @GetMapping("/wttr/current")
    public WeatherCardDTO getWttrCurrent(
        @RequestParam(defaultValue = "Kyiv") String location,
        @RequestParam(defaultValue = "today") String day) {
        return wttrInService.getCurrentWeather(location, day);
    }

    @GetMapping("/wttr/hourly")
    public List<HourlyWeatherCardDTO> getWttrHourly(
        @RequestParam(defaultValue = "Kyiv") String location,
        @RequestParam(defaultValue = "today") String day) {
        return wttrInService.getHourlyWeather(location, day);
    }

    @GetMapping("/metno/current")
    public WeatherCardDTO getMetNoCurrent(
        @RequestParam(defaultValue = "Kyiv") String location,
        @RequestParam(defaultValue = "today") String day) {
        return metNoService.getCurrentWeather(location, day);
    }

    @GetMapping("/metno/hourly")
    public List<HourlyWeatherCardDTO> getMetNoHourly(
        @RequestParam(defaultValue = "Kyiv") String location,
        @RequestParam(defaultValue = "today") String day) {
        return metNoService.getHourlyWeather(location, day);
    }
}
