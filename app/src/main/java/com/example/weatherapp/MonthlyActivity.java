package com.example.weatherapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MonthlyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView monthTitle;
    private final String API_KEY = "5828bd5b646348de10e5a6be2b917c31";
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);

        recyclerView = findViewById(R.id.monthlyRecyclerView);
        monthTitle = findViewById(R.id.monthTitle);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lon = getIntent().getDoubleExtra("lon", 0);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        setupCalendar(lat, lon);
    }

    private void setupCalendar(double lat, double lon) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthTitle.setText(sdf.format(cal.getTime()));

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

        List<String> daysList = new ArrayList<>();
        for (int i = 0; i < 42; i++) {
            if (i < firstDayOfWeek || i >= daysInMonth + firstDayOfWeek) {
                daysList.add("");
            } else {
                daysList.add(String.valueOf(i - firstDayOfWeek + 1));
            }
        }

        fetchMonthlyData(lat, lon, daysList);
    }

    private void fetchMonthlyData(double lat, double lon, List<String> daysList) {
        WeatherApi api = retrofit.create(WeatherApi.class);
        api.getForecast(lat, lon, API_KEY, "metric").enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<Integer, ForecastResponse.HourlyModel> forecastMap = new HashMap<>();
                    Calendar tempCal = Calendar.getInstance();

                    for (ForecastResponse.HourlyModel item : response.body().list) {
                        tempCal.setTimeInMillis(item.dt * 1000L);
                        int day = tempCal.get(Calendar.DAY_OF_MONTH);

                        if (!forecastMap.containsKey(day)) {
                            forecastMap.put(day, item);
                        } else {
                            ForecastResponse.HourlyModel existing = forecastMap.get(day);
                            if (item.main.temp_max > existing.main.temp_max) {
                                existing.main.temp_max = item.main.temp_max;
                            }
                            if (item.main.temp_min < existing.main.temp_min) {
                                existing.main.temp_min = item.main.temp_min;
                            }
                        }
                    }

                    recyclerView.setLayoutManager(new GridLayoutManager(MonthlyActivity.this, 7));
                    recyclerView.setAdapter(new MonthlyAdapter(daysList, forecastMap));
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Toast.makeText(MonthlyActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}