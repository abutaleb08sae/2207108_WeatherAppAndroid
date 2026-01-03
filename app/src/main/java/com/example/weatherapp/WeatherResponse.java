package com.example.weatherapp;

import java.util.List;

public class WeatherResponse {

    public Coord coord;
    public Main main;
    public Wind wind;
    public Sys sys;
    public List<Weather> weather;
    public String name;

    public static class Coord {
        public double lat;
        public double lon;
    }

    public static class Main {
        public double temp;
        public double feels_like;
        public int humidity;
    }

    public static class Wind {
        public double speed;
    }

    public static class Sys {
        public String country;
        public long sunrise;
        public long sunset;
    }

    public static class Weather {
        public String main;
        public String description;
        public String icon;
    }
}