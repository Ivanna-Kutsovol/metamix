import React from 'react';
import stl from './card.module.scss';
import { getIconByDescription } from '@/utils/weatherUtils';
import Image from 'next/image';
import iconTemperature from '../../../public/icon _temperature_.svg';

export interface WeatherData {
  temperature: number;
  date: string;
  humidity: string;
  pressure: string;
  visibility: string;
  wind: string;
  weatherDescription: string;
}

export default function WeatherCard({ data }: { data: WeatherData }) {
  const date = data.date ? new Date(data.date) : null;
  const dateString = date && !isNaN(date.getTime()) ? date.toLocaleString() : 'Date not available';

  const iconSrc = data.weatherDescription ? getIconByDescription(data.weatherDescription) : "/icons/default.svg";

  return (
    <section className={stl.card}>
      <section className={stl.card__main_content}>
        <h2 className={stl.card__date}>{dateString}</h2>
        <section className={stl.card__weather}>
          <Image src={iconTemperature} alt="Weather Icon Temperature" className={stl.card__iconTemperature} width={19} height={63}/>
          <p className={stl.card__temperature}>{data.temperature}°C</p>
          <Image src={iconSrc} alt="Weather Icon" className={stl.card__icon} width={90} height={90} />
        </section>
      </section>
      <section className={stl.card__secondary_content}>

        <div className={stl.card__description}>
          <p>Humidity</p>
          <p>{data.humidity}</p>
        </div>
        
        <div className={stl.card__description}>
          <p>Visiblity</p>
          <p>{data.visibility}</p>
        </div>

        <div className={stl.card__description}>
          <p>Air Pressure</p>
          <p>{data.pressure}</p>
        </div>

        <div className={stl.card__description}>
          <p>Wind</p> 
          <p>{data.wind}</p>
        </div>

      </section>
    </section>
  );
}
