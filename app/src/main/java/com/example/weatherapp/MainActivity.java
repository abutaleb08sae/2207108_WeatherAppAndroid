package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText searchCityEt;
    private Button searchBtn, btnSeeMore, btnClearHistory;
    private TextView countryTv, cityTv, tempTv, latTv, lonTv, sunriseTv, sunsetTv, windTv, humidityTv, airAqiTv;
    private TextView statusTv, feelsLikeTv, descriptionTv;
    private ImageView mainWeatherIcon;
    private ListView historyListView;
    private View historyContainer;

    private final String API_KEY = "5828bd5b646348de10e5a6be2b917c31";
    private Retrofit retrofit;
    private DatabaseReference dbRef;

    private double currentLat, currentLon;
    private List<String> historyNames = new ArrayList<>();
    private List<String> historyKeys = new ArrayList<>();
    private ArrayAdapter<String> historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchCityEt = findViewById(R.id.editTextText);
        searchBtn = findViewById(R.id.button);
        btnSeeMore = findViewById(R.id.btn_see_more);
        btnClearHistory = findViewById(R.id.btn_clear_history);
        historyListView = findViewById(R.id.historyListView);
        historyContainer = findViewById(R.id.historyContainer);

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

        statusTv = findViewById(R.id.weather_status);
        feelsLikeTv = findViewById(R.id.feels_like);
        descriptionTv = findViewById(R.id.weather_description);
        mainWeatherIcon = findViewById(R.id.weather_icon_main);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dbRef = FirebaseDatabase.getInstance("https://weather-app-58898-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("search_history");

        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyNames);
        historyListView.setAdapter(historyAdapter);

        loadSearchHistory();

        boolean fromGPS = getIntent().getBooleanExtra("fromGPS", false);
        if (fromGPS) {
            searchCityEt.setVisibility(View.GONE);
            searchBtn.setVisibility(View.GONE);
            currentLat = getIntent().getDoubleExtra("lat", 0);
            currentLon = getIntent().getDoubleExtra("lon", 0);
            fetchWeatherByCoords(currentLat, currentLon);
        }

        searchBtn.setOnClickListener(v -> {
            String city = searchCityEt.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeather(city);
                saveToHistory(city);
                hideHistory();
            }
        });

        searchCityEt.setOnClickListener(v -> {
            if (!historyNames.isEmpty()) {
                showHistory();
            }
        });

        searchCityEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (!historyNames.isEmpty()) {
                    showHistory();
                }
            } else {
                historyListView.postDelayed(this::hideHistory, 300);
            }
        });

        historyListView.setOnItemClickListener((parent, view, position, id) -> {
            String city = historyNames.get(position);
            searchCityEt.setText(city);
            fetchWeather(city);
            hideHistory();
            searchCityEt.clearFocus();
        });

        historyListView.setOnItemLongClickListener((parent, view, position, id) -> {
            dbRef.child(historyKeys.get(position)).removeValue();
            Toast.makeText(this, "Removed from history", Toast.LENGTH_SHORT).show();
            return true;
        });

        btnClearHistory.setOnClickListener(v -> {
            dbRef.removeValue();
            hideHistory();
        });

        btnSeeMore.setOnClickListener(v -> {
            if (currentLat != 0 || currentLon != 0) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("lat", currentLat);
                intent.putExtra("lon", currentLon);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please search for a city first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showHistory() {
        historyContainer.setVisibility(View.VISIBLE);
        historyListView.setVisibility(View.VISIBLE);
        btnClearHistory.setVisibility(View.VISIBLE);
        historyContainer.bringToFront();
    }

    private void hideHistory() {
        historyContainer.setVisibility(View.GONE);
        historyListView.setVisibility(View.GONE);
        btnClearHistory.setVisibility(View.GONE);
    }

    private void saveToHistory(String city) {
        if (!historyNames.contains(city)) {
            String id = dbRef.push().getKey();
            if (id != null) {
                dbRef.child(id).child("cityName").setValue(city);
            }
        }
    }

    private void loadSearchHistory() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyNames.clear();
                historyKeys.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String name = data.child("cityName").getValue(String.class);
                    if (name != null) {
                        historyNames.add(0, name);
                        historyKeys.add(0, data.getKey());
                    }
                }
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchWeather(String cityName) {
        WeatherApi api = retrofit.create(WeatherApi.class);
        api.getWeather(cityName, API_KEY, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
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

    private void fetchWeatherByCoords(double lat, double lon) {
        WeatherApi api = retrofit.create(WeatherApi.class);
        api.getWeatherByCoords(lat, lon, API_KEY, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error fetching location weather", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(WeatherResponse data) {
        currentLat = data.coord.lat;
        currentLon = data.coord.lon;

        Locale l = new Locale("", data.sys.country);
        countryTv.setText("Country: " + l.getDisplayCountry());
        cityTv.setText("City: " + data.name);

        tempTv.setText(Math.round(data.main.temp) + "°");
        feelsLikeTv.setText("Feels like: " + Math.round(data.main.feels_like) + "°");
        statusTv.setText(data.weather.get(0).main);

        String desc = data.weather.get(0).description;
        descriptionTv.setText("The skies will be " + desc + ".");

        String iconUrl = "https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@4x.png";
        Glide.with(this)
                .load(iconUrl)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(mainWeatherIcon);

        latTv.setText(": " + data.coord.lat);
        lonTv.setText(": " + data.coord.lon);
        windTv.setText(": " + data.wind.speed + " m/s");
        humidityTv.setText(": " + data.main.humidity + "%");
        sunriseTv.setText(": " + formatTime(data.sys.sunrise));
        sunsetTv.setText(": " + formatTime(data.sys.sunset));

        btnSeeMore.setVisibility(View.VISIBLE);
        fetchAirQuality(currentLat, currentLon);
    }

    private void fetchAirQuality(double lat, double lon) {
        WeatherApi api = retrofit.create(WeatherApi.class);
        api.getAirQuality(lat, lon, API_KEY).enqueue(new Callback<AirResponse>() {
            @Override
            public void onResponse(Call<AirResponse> call, Response<AirResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().list.isEmpty()) {
                    int aqi = response.body().list.get(0).main.aqi;
                    String[] aqiLevels = {"", "Good", "Fair", "Moderate", "Poor", "Very Poor"};
                    if (aqi >= 1 && aqi <= 5) {
                        airAqiTv.setText(": " + aqiLevels[aqi]);
                    }
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