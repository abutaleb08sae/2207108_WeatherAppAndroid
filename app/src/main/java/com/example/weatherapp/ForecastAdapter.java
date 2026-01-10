package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private List<ForecastResponse.ForecastItem> forecastList;

    public ForecastAdapter(List<ForecastResponse.ForecastItem> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastResponse.ForecastItem item = forecastList.get(position);

        if (item != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
            String dayName = sdf.format(new Date(item.dt * 1000));
            holder.dayTv.setText(dayName);

            if (item.main != null) {
                holder.tempTv.setText(Math.round(item.main.temp) + "°");
            }

            if (item.weather != null && !item.weather.isEmpty()) {
                String iconUrl = "https://openweathermap.org/img/wn/" + item.weather.get(0).icon + "@2x.png";
                Glide.with(holder.itemView.getContext()).load(iconUrl).into(holder.iconIv);
            }
        }
    }

    @Override
    public int getItemCount() {
        return forecastList != null ? forecastList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTv, tempTv;
        ImageView iconIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTv = itemView.findViewById(R.id.dayText);
            tempTv = itemView.findViewById(R.id.tempText);
            iconIv = itemView.findViewById(R.id.forecastIcon);
        }
    }
}