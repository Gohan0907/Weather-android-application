package com.example.my_weather_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RVWeatherAdapter extends RecyclerView.Adapter<RVWeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<RVWeatherModal> rvWeatherModalArrayList;

    public RVWeatherAdapter(Context context, ArrayList<RVWeatherModal> rvWeatherModalArrayList) {
        this.context = context;
        this.rvWeatherModalArrayList = rvWeatherModalArrayList;
    }

    @NonNull
    @Override
    public RVWeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVWeatherAdapter.ViewHolder holder, int position) {

        RVWeatherModal modal = rvWeatherModalArrayList.get(position);
        holder.temperaturetv.setText(modal.getTemperature() + "°c");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditiontv);
        holder.windtv.setText(modal.getWindspeed() + "Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(modal.getTime());
        }catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return rvWeatherModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windtv,temperaturetv,timetv;
        private ImageView conditiontv;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            windtv = itemView.findViewById(R.id.idtvwindspeed);
            temperaturetv = itemView.findViewById(R.id.idtvtime);
            timetv = itemView.findViewById(R.id.idtvwindspeed);
            conditiontv = itemView.findViewById(R.id.idtvcondition);
        }
    }
}
