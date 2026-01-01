package com.example.weatherapp;

import android.content.Context;
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

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private Context context;
    private List<HourlyModel> hourlyList;

    public HourlyAdapter(Context context, List<HourlyModel> hourlyList) {
        this.context = context;
        this.hourlyList = hourlyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hourly, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyModel model = hourlyList.get(position);

        holder.temp.setText(Math.round(model.main.temp) + "°");

        if (position == 0) {
            holder.time.setText("Now");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("h a", Locale.getDefault());
            holder.time.setText(sdf.format(new Date(model.dt * 1000)));
        }

        String iconUrl = "https://openweathermap.org/img/wn/" + model.weather.get(0).icon + "@2x.png";
        Glide.with(context).load(iconUrl).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return Math.min(hourlyList.size(), 24);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView temp, time;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            temp = itemView.findViewById(R.id.hourly_temp);
            time = itemView.findViewById(R.id.hourly_time);
            icon = itemView.findViewById(R.id.hourly_icon);
        }
    }
}