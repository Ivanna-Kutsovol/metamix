import Sunny from "../../public/iconsWeather/sunny.webp";
import PartlyCloudy from "../../public/iconsWeather/partlyCloude.webp";
import Cloudy from "../../public/iconsWeather/cloudy.webp";
import Rain from "../../public/iconsWeather/rain.webp";
import Snow from "../../public/iconsWeather/snow.webp";
import Storm from "../../public/iconsWeather/storm.webp";
import ClearHight from "../../public/iconsWeather/clearHight.webp";
import Night from "../../public/iconsWeather/night.webp";
import NightCloude from "../../public/iconsWeather/nightCloudy.webp";
import Thunder from '../../public/iconsWeather/thunder.webp';
import NightRain from '../../public/iconsWeather/nightRain.webp';

export const weatherIconMap: Record<string, string[]> = {
  [Sunny.src]: ["clearsky_day", "fair_day", "sunny"],
  [PartlyCloudy.src]: ["partlycloudy", "partlycloudy_day"],
  [Cloudy.src]: ["cloudy"],
  [Rain.src]: ["rain", "lightrain", "heavyrain", "rainshowers_day"],
  [NightRain.src]: ["rainshowers_night", "lightrain_night", "heavyrain_night"], 
  [Snow.src]: ["snow", "snowshowers_day", "snowshowers_night"],
  [Storm.src]: ["storm"],
  [Thunder.src]: ["thunderstorm"],
  [ClearHight.src]: ["clearsky_night", "fair_night"],
  [Night.src]: ["night"],
  [NightCloude.src]: ["cloudy_night", "partlycloudy_night"],
};

const defaultIcon = PartlyCloudy.src;

export function getIconByDescription(desc: string): string {
  const lowerDesc = desc.toLowerCase();

  for (const [iconPath, keywords] of Object.entries(weatherIconMap)) {
    if (keywords.includes(lowerDesc)) {
      return iconPath;
    }
  }

  return defaultIcon;
}