'use client';

import React, { useState } from 'react';
import stl from './header.module.scss';
import Image from 'next/image';
import Logo from '../../../public/logo.png';
import { useWeatherApi } from '@/context/WeatherApiContext';

export default function Header() {
    const [open, setOpen] = useState(false);
    const [activeIndex, setActiveIndex] = useState<number | null>(null);
    const { selectedApi, setSelectedApi, day, setDay } = useWeatherApi();

    const apiOptions = ['Wttr.in', 'Met.no'];

    const handleApiSelect = (api: string) => {
        setSelectedApi(api);
        setOpen(false);
    };

    const toggleDropdown = () => setOpen(!open);

    
    const handleMenuClick = (index: number) => {
        setActiveIndex(index);
        setDay(index === 0 ? 'today' : 'tomorrow');
    };

    return (
        <header className={stl.header}>
            <Image src={Logo} alt="logo" width={67} height={48}/>
            <h1 className={stl.header__title}>Metamix</h1>
            <ol className={stl.header__menu}>
                <li onClick={() => { handleMenuClick(0); }}
                    className={day === 'today' ? stl.header__active : stl.header__item}>
                    <a href="#">Today</a>
                </li>
                <li onClick={() => { handleMenuClick(1); }}
                    className={day === 'tomorrow' ? stl.header__active : stl.header__item}>
                    <a href="#">Tomorrow</a>
                </li>
                <li className={stl.header__item}>
                    <button 
                        className={open ? stl.header__close : stl.header__open } 
                        onClick={toggleDropdown}>{selectedApi}</button>
                {open && (
                    <ul className={stl.header__dropdown}>
                        {apiOptions
                        .filter(api => api !== selectedApi)
                        .map((api, index) => (
                            <li
                                key={index}
                                className={stl.header__itemDropdown}
                                onClick={() => handleApiSelect(api)}
                            >
                                <a href="#">{api}</a>
                           </li>
                        ))}
                    </ul>
                )}</li>
            </ol>
        </header>
    );
}