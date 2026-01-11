package com.example.weatherapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherInfoActivity extends AppCompatActivity {

    private TextView tvWind, tvHumidity, tvPressure, tvVisibility;
    private final String API_KEY = "5828bd5b646348de10e5a6be2b917c31";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);

        tvWind = findViewById(R.id.tvWindSpeed);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvPressure = findViewById(R.id.tvPressure);
        tvVisibility = findViewById(R.id.tvVisibility);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lon = getIntent().getDoubleExtra("lon", 0);

        fetchCurrentDetails(lat, lon);
    }

    private void fetchCurrentDetails(double lat, double lon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi api = retrofit.create(WeatherApi.class);
        api.getCurrentWeather(lat, lon, API_KEY, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse data = response.body();

                    if (data.wind != null) {
                        tvWind.setText("Wind: " + data.wind.speed + " km/h");
                    }

                    if (data.main != null) {
                        tvHumidity.setText("Humidity: " + data.main.humidity + "%");
                        tvPressure.setText("Pressure: " + data.main.pressure + " hPa");
                    }

                    tvVisibility.setText("Visibility: " + (data.visibility / 1000) + " km");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(WeatherInfoActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}