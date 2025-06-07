'use client';

import React, { useEffect, useState } from 'react';
import { useWeatherApi } from '@/context/WeatherApiContext';
import WeatherCard from './card'; 
import type { WeatherData } from './card';
import stl from './card.module.scss';
import Image from 'next/image';
import iconSearch from "../../../public/icon _search_.svg";

export default function Display() {
  const [location, setLocation] = useState("Kyiv");
  const { selectedApi, day } = useWeatherApi();
  const [weatherData, setWeatherData] = useState<WeatherData | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!location.trim()) return;

    const fetchWeather = async () => {
      setLoading(true);
      setError(null);

      const apiMap: Record<string, string> = {
          'WeatherAPI': 'http://localhost:8080/weather/wttr/current',
          'Open-Meteo': 'http://localhost:8080/weather/metno/current',
      };
      const url = `${apiMap[selectedApi]}?location=${encodeURIComponent(location)}&day=${day}`;

      try {

        const res = await fetch(url);
        if (!res.ok) throw new Error('Network response was not ok');
        const rawData = await res.json();

        const formattedData: WeatherData = {
          temperature: rawData.temperature ?? rawData.temp ?? 0,
          date: rawData.date ?? rawData.time ?? new Date().toISOString(),
          humidity: rawData.humidity ?? 0,
          pressure: rawData.pressure ?? 0,
          visibility: rawData.visibility ?? 0,
          wind: typeof rawData.wind === 'string' ? rawData.wind
        : `${rawData.windSpeed ?? rawData.wind?.speed ?? 0} m/s${rawData.windDirection ?? rawData.wind?.direction ? ", " + (rawData.windDirection ?? rawData.wind?.direction) : ""}`,
          weatherDescription: rawData.weatherDescription ?? rawData.description ?? "clear"
        };

        setWeatherData(formattedData);
      } catch (error) {
        console.error('Error fetching weather:', error);
        setError('Не удалось загрузить данные о погоде');
        setWeatherData(null);
      } finally {
        setLoading(false);
      }
    };

    fetchWeather();
  }, [selectedApi, location, day]);

  return (
    <div className={stl.input_container}>
      <div className={stl.input_wrapper}>
          <Image src={iconSearch} alt="Search Icon" className={stl.icon} width={45} height={45}/>
          <input
            type="text"
            placeholder="Search location"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            className={stl.input}
          />
      </div>

      {loading && <p>Loading...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {!loading && !error && weatherData && <WeatherCard data={weatherData} />}
    </div>
  );
}
