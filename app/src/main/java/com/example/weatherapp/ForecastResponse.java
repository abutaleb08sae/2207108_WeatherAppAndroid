package com.example.weatherapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {
    @SerializedName("list")
    public List<HourlyModel> list;

    public static class HourlyModel {
        @SerializedName("dt")
        public long dt;

        @SerializedName("main")
        public Main main;

        @SerializedName("weather")
        public List<Weather> weather;

        @SerializedName("dt_txt")
        public String dt_txt;

        public static class Main {
            @SerializedName("temp")
            public double temp;

            @SerializedName("temp_min")
            public double temp_min;

            @SerializedName("temp_max")
            public double temp_max;

            @SerializedName("feels_like")
            public double feels_like;

            @SerializedName("humidity")
            public int humidity;
        }

        public static class Weather {
            @SerializedName("main")
            public String main;

            @SerializedName("description")
            public String description;

            @SerializedName("icon")
            public String icon;
        }
    }
}