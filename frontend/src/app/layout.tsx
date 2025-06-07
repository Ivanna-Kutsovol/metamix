import type { Metadata } from "next";
import "./styles/App.scss";
import Header from "@/components/header/header";
import { WeatherApiProvider } from "@/context/WeatherApiContext";

export const metadata: Metadata = {
  title: "Metamix",
  description: "Weather widget that gathers data from multiple sources",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <WeatherApiProvider>
          <Header/>
          {children}
        </WeatherApiProvider>
      </body>
    </html>
  );
}
