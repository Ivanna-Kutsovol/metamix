'use client';

import React, { createContext, useContext, useState, ReactNode } from 'react';

type DayType = 'today' | 'tomorrow';
type WeatherApiContextType = {
  selectedApi: string;
  setSelectedApi: (api: string) => void;
  day: DayType;
  setDay: (day: DayType) => void;
};

const WeatherApiContext = createContext<WeatherApiContextType | undefined>(undefined);

export const WeatherApiProvider = ({ children }: { children: ReactNode }) => {
  const [selectedApi, setSelectedApi] = useState('Wttr.in');
  const [day, setDay] = useState<DayType>('today');

  return (
    <WeatherApiContext.Provider value={{ selectedApi, setSelectedApi, day, setDay }}>
      {children}
    </WeatherApiContext.Provider>
  );
};

export const useWeatherApi = () => {
  const context = useContext(WeatherApiContext);
  if (!context) {
    throw new Error('useWeatherApi must be used within a WeatherApiProvider');
  }
  return context;
};