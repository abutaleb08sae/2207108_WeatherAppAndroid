package com.example.weatherapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText searchCityEt;
    private Button searchBtn;
    private TextView countryTv, cityTv, tempTv, latTv, lonTv, sunriseTv, sunsetTv, windTv, humidityTv, airAqiTv;
    private final String API_KEY = "5828bd5b646348de10e5a6be2b917c31";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchCityEt = findViewById(R.id.editTextText);
        searchBtn = findViewById(R.id.button);
        countryTv = findViewById(R.id.country);
        cityTv = findViewById(R.id.city);
        tempTv = findViewById(R.id.textView4);
        latTv = findViewById(R.id.latitude);
        lonTv = findViewById(R.id.longitude);
        sunriseTv = findViewById(R.id.sunrise);
        sunsetTv = findViewById(R.id.sunset);
        windTv = findViewById(R.id.wind_speed);
        humidityTv = findViewById(R.id.humidity);
        airAqiTv = findViewById(R.id.air_aqi);

        searchBtn.setOnClickListener(v -> {
            String city = searchCityEt.getText().toString().trim();
            if (!city.isEmpty()) fetchWeather(city);
        });
    }

    private void fetchWeather(String cityName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi api = retrofit.create(WeatherApi.class);
        api.getWeather(cityName, API_KEY, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse data = response.body();


                    Locale l = new Locale("", data.sys.country);
                    countryTv.setText("Country: " + l.getDisplayCountry());


                    String status = data.weather.get(0).main;
                    String desc = data.weather.get(0).description;

                    tempTv.setText(Math.round(data.main.temp) + "°C | " + status);


                    cityTv.setText("City: " + data.name);
                    latTv.setText(": " + data.coord.lat);
                    lonTv.setText(": " + data.coord.lon);
                    windTv.setText(": " + data.wind.speed + " m/s");
                    humidityTv.setText(": " + data.main.humidity + "%");
                    sunriseTv.setText(": " + formatTime(data.sys.sunrise));
                    sunsetTv.setText(": " + formatTime(data.sys.sunset));

                    fetchAirQuality(data.coord.lat, data.coord.lon, retrofit);

                } else {
                    Toast.makeText(MainActivity.this, "City not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAirQuality(double lat, double lon, Retrofit retrofit) {
        WeatherApi api = retrofit.create(WeatherApi.class);
        api.getAirQuality(lat, lon, API_KEY).enqueue(new Callback<AirResponse>() {
            @Override
            public void onResponse(Call<AirResponse> call, Response<AirResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int aqi = response.body().list.get(0).main.aqi;
                    String[] aqiLevels = {"", "Good", "Fair", "Moderate", "Poor", "Very Poor"};
                    airAqiTv.setText(": " + aqiLevels[aqi]);
                }
            }
            @Override
            public void onFailure(Call<AirResponse> call, Throwable t) {}
        });
    }

    private String formatTime(long time) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(time * 1000));
    }
}