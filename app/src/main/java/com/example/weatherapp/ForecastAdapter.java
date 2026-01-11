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

    private List<ForecastResponse.HourlyModel> forecastList;

    public ForecastAdapter(List<ForecastResponse.HourlyModel> forecastList) {
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
        ForecastResponse.HourlyModel item = forecastList.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
        holder.dayTv.setText(sdf.format(new Date(item.dt * 1000)));

        holder.tempTv.setText(Math.round(item.main.temp) + "°C");

        if (item.weather != null && !item.weather.isEmpty()) {
            String description = item.weather.get(0).description;
            holder.descTv.setText(description.substring(0, 1).toUpperCase() + description.substring(1));

            String iconUrl = "https://openweathermap.org/img/wn/" + item.weather.get(0).icon + "@4x.png";
            Glide.with(holder.itemView.getContext()).load(iconUrl).into(holder.iconIv);
        }

        String highLow = "H: " + Math.round(item.main.temp_max) + "° L: " + Math.round(item.main.temp_min) + "°";
        holder.highLowTv.setText(highLow);
    }

    @Override
    public int getItemCount() {
        return forecastList == null ? 0 : forecastList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTv, tempTv, descTv, highLowTv;
        ImageView iconIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTv = itemView.findViewById(R.id.dayText);
            tempTv = itemView.findViewById(R.id.tempText);
            descTv = itemView.findViewById(R.id.descriptionText);
            highLowTv = itemView.findViewById(R.id.highLowText);
            iconIv = itemView.findViewById(R.id.forecastIcon);
        }
    }
}