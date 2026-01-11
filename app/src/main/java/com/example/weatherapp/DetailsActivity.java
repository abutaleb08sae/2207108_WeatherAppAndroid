package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    private Button btnHourly, btnWeekly, btnMonthly, btnWeatherDetails;
    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);

        btnHourly = findViewById(R.id.btn_hourly_forecast);
        btnWeekly = findViewById(R.id.btn_weekly_forecast);
        btnMonthly = findViewById(R.id.btn_monthly_forecast);
        btnWeatherDetails = findViewById(R.id.btn_weather_details);

        btnHourly.setOnClickListener(v -> {
            Intent intent = new Intent(DetailsActivity.this, HourlyActivity.class);
            intent.putExtra("lat", lat);
            intent.putExtra("lon", lon);
            startActivity(intent);
        });

        btnWeekly.setOnClickListener(v -> {
            Intent intent = new Intent(DetailsActivity.this, ForecastActivity.class);
            intent.putExtra("lat", lat);
            intent.putExtra("lon", lon);
            startActivity(intent);
        });

        btnMonthly.setOnClickListener(v -> {
            Intent intent = new Intent(DetailsActivity.this, MonthlyActivity.class);
            intent.putExtra("lat", lat);
            intent.putExtra("lon", lon);
            startActivity(intent);
        });

        btnWeatherDetails.setOnClickListener(v -> {
            Intent intent = new Intent(DetailsActivity.this, WeatherInfoActivity.class);
            intent.putExtra("lat", lat);
            intent.putExtra("lon", lon);
            startActivity(intent);
        });
    }
}