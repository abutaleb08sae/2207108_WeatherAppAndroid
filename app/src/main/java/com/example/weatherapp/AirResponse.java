package com.example.weatherapp;
import java.util.List;

public class AirResponse {
    public List<AirItem> list;
    public class AirItem {
        public Main main;
    }
    public class Main {
        public int aqi;
    }
}