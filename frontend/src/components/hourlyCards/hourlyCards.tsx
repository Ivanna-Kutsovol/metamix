import React from 'react';
import stl from './hourlyCards.module.scss';
import Image from 'next/image';
import { getIconByDescription } from '@/utils/weatherUtils';

export interface HourlyWeather {
  time: string;
  temperature: number;
  weatherDescription: string;
}

export default function HourlyCard({ data }: { data: HourlyWeather }) {
    console.log("Hourly description:", data.weatherDescription);
    const iconSrc = getIconByDescription(data.weatherDescription) ?? "/icons/default.svg";

  const formattedTime = new Date(data.time).toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit',
  });

  return (
    <div className={stl.container__card}>
      <div className={stl.container__time}><strong>{formattedTime}</strong></div>
        <div className={stl.container__weather}/>
        <Image
          src={iconSrc}
          alt={data.weatherDescription}
          width={51}
          height={56}
          className={stl.container__icon}
        />
        <div className={stl.container__temperature}>{data.temperature}°C</div>
      </div>
  );
}
