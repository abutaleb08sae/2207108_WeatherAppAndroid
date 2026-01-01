package com.example.weatherapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HourlyModel {
    @SerializedName("dt")
    public long dt;

    @SerializedName("main")
    public Main main;

    @SerializedName("weather")
    public List<Weather> weather;

    public static class Main {
        @SerializedName("temp")
        public double temp;
    }

    public static class Weather {
        @SerializedName("icon")
        public String icon;
    }
}

class ForecastResponse {
    @SerializedName("list")
    public List<HourlyModel> list;
}