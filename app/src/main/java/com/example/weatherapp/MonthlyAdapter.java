package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Map;

public class MonthlyAdapter extends RecyclerView.Adapter<MonthlyAdapter.ViewHolder> {

    private final List<String> daysOfMonth;
    private final Map<Integer, ForecastResponse.HourlyModel> forecastData;

    public MonthlyAdapter(List<String> daysOfMonth, Map<Integer, ForecastResponse.HourlyModel> forecastData) {
        this.daysOfMonth = daysOfMonth;
        this.forecastData = forecastData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String dayText = daysOfMonth.get(position);
        holder.dayNumberTv.setText(dayText);

        if (dayText != null && !dayText.isEmpty()) {
            try {
                int dayInt = Integer.parseInt(dayText);
                if (forecastData != null && forecastData.containsKey(dayInt)) {
                    ForecastResponse.HourlyModel data = forecastData.get(dayInt);

                    holder.tempTv.setVisibility(View.VISIBLE);
                    holder.weatherIconIv.setVisibility(View.VISIBLE);

                    String highLow = Math.round(data.main.temp_max) + "°/" + Math.round(data.main.temp_min) + "°";
                    holder.tempTv.setText(highLow);

                    String iconUrl = "https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png";
                    Glide.with(holder.itemView.getContext())
                            .load(iconUrl)
                            .into(holder.weatherIconIv);
                } else {
                    holder.tempTv.setVisibility(View.INVISIBLE);
                    holder.weatherIconIv.setVisibility(View.INVISIBLE);
                }
            } catch (NumberFormatException e) {
                holder.tempTv.setVisibility(View.INVISIBLE);
                holder.weatherIconIv.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.tempTv.setVisibility(View.INVISIBLE);
            holder.weatherIconIv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth != null ? daysOfMonth.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayNumberTv, tempTv;
        ImageView weatherIconIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayNumberTv = itemView.findViewById(R.id.dayNumberTv);
            tempTv = itemView.findViewById(R.id.tempTv);
            weatherIconIv = itemView.findViewById(R.id.weatherIconIv);
        }
    }
}