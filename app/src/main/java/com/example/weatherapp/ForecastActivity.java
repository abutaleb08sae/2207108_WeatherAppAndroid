package com.example.weatherapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ForecastAdapter adapter;
    private final String API_KEY = "5828bd5b646348de10e5a6be2b917c31";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        recyclerView = findViewById(R.id.forecastRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lon = getIntent().getDoubleExtra("lon", 0);

        fetchWeeklyForecast(lat, lon);
    }

    private void fetchWeeklyForecast(double lat, double lon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi api = retrofit.create(WeatherApi.class);
        api.getForecast(lat, lon, API_KEY, "metric").enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ForecastResponse.HourlyModel> dailyList = processDailyForecasts(response.body().list);
                    adapter = new ForecastAdapter(dailyList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
            }
        });
    }

    private List<ForecastResponse.HourlyModel> processDailyForecasts(List<ForecastResponse.HourlyModel> allForecasts) {
        Map<String, ForecastResponse.HourlyModel> dailyMap = new LinkedHashMap<>();

        for (ForecastResponse.HourlyModel item : allForecasts) {
            String date = item.dt_txt.split(" ")[0];

            if (!dailyMap.containsKey(date)) {
                dailyMap.put(date, item);
            } else {
                ForecastResponse.HourlyModel day = dailyMap.get(date);

                if (item.main.temp_max > day.main.temp_max) {
                    day.main.temp_max = item.main.temp_max;
                }

                if (item.main.temp_min < day.main.temp_min) {
                    day.main.temp_min = item.main.temp_min;
                }

                if (item.dt_txt.contains("12:00:00")) {
                    day.weather = item.weather;
                    day.main.temp = item.main.temp;
                }
            }
        }
        return new ArrayList<>(dailyMap.values());
    }
}