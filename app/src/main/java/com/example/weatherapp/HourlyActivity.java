package com.example.weatherapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HourlyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HourlyAdapter adapter;
    private List<HourlyModel> hourlyList;
    private final String API_KEY = "5828bd5b646348de10e5a6be2b917c31";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly);

        recyclerView = findViewById(R.id.hourly_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        hourlyList = new ArrayList<>();
        adapter = new HourlyAdapter(this, hourlyList);
        recyclerView.setAdapter(adapter);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lon = getIntent().getDoubleExtra("lon", 0);

        if (lat != 0 || lon != 0) {
            fetchHourlyForecast(lat, lon);
        } else {
            Toast.makeText(this, "Coordinates not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchHourlyForecast(double lat, double lon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi api = retrofit.create(WeatherApi.class);
        api.getForecast(lat, lon, API_KEY, "metric").enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hourlyList.clear();
                    hourlyList.addAll(response.body().list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Toast.makeText(HourlyActivity.this, "Failed to load forecast", Toast.LENGTH_SHORT).show();
            }
        });
    }
}