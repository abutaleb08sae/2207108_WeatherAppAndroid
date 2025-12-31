package com.example.weatherapp;

import java.util.List;

public class WeatherResponse {

    public Coord coord;
    public Main main;
    public Wind wind;
    public Sys sys;
    public List<Weather> weather;
    public String name;

    public class Coord {
        public float lat;
        public float lon;
    }

    public class Main {
        public float temp;
        public int humidity;
    }

    public class Wind {
        public float speed;
    }

    public class Sys {
        public String country;
        public long sunrise;
        public long sunset;
    }

    public class Weather {
        public String main;
        public String description;
        public String icon;
    }
}