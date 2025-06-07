'use client';

import React, { useEffect, useState } from 'react';
import HourlyCard, { HourlyWeather } from './hourlyCards';
import stl from './hourlyCards.module.scss';

export default function DisplayCards({ source = "metno", location = "Kyiv", day = "today" }) {
  const [hours, setHours] = useState<HourlyWeather[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);

    fetch(`http://localhost:8080/weather/${source}/hourly?location=${location}&day=${day}`)
      .then(res => {
        if (!res.ok) throw new Error('Network response was not ok');
        return res.json();
      })
      .then((data: HourlyWeather[]) => {
        const now = new Date();
        const filtered = data
          .filter(item => {
            const itemDate = new Date(item.time);
            return itemDate >= now;
          })
          .slice(0, 7);

        setHours(filtered);
      })
      .catch(err => {
        console.error(err);
        setError('Не удалось загрузить почасовые данные');
      })
      .finally(() => setLoading(false));
  }, [source, location, day]);

  if (loading) return <p>Loading hourly data...</p>;
  if (error) return <p style={{ color: 'red' }}>{error}</p>;

  return (
    <div className={stl.container}>
      {hours.map((h, i) => (
        <HourlyCard key={i} data={h} />
      ))}
    </div>
  );
}
