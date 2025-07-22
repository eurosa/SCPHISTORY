package com.googleapi.scpandroiddata;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.DataItemViewHolder> {
    private List<DataItem> dataItems;

    public DataItemAdapter(List<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @NonNull
    @Override
    public DataItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data, parent, false);
        return new DataItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataItemViewHolder holder, int position) {
        DataItem item = dataItems.get(position);
        Context context = holder.itemView.getContext();

        // Set values with null checks and text colors
        setTextWithColor(holder.tempValue, item.getTemp_value(),
                ContextCompat.getColor(context, R.color.temperature_color));
        setTextWithColor(holder.humValue, item.getHum_value(),
                ContextCompat.getColor(context, R.color.humidity_color));
        setTextWithColor(holder.pressureValue, item.getAir_pressure_value(),
                ContextCompat.getColor(context, R.color.pressure_color));
        setTextWithColor(holder.dateTime, item.getDate_time(),
                ContextCompat.getColor(context, R.color.datetime_color));

        // Alternate row colors for better readability
        int bgColor = position % 2 == 0
                ? ContextCompat.getColor(context, R.color.row_even)
                : ContextCompat.getColor(context, R.color.row_odd);
        holder.itemView.setBackgroundColor(bgColor);
    }

    private void setTextWithColor(TextView textView, String value, int color) {
        textView.setText(value != null ? value : "N/A");
        textView.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public void updateData(List<DataItem> newData) {
        this.dataItems = newData;
        notifyDataSetChanged();
    }

    static class DataItemViewHolder extends RecyclerView.ViewHolder {
        TextView tempValue;
        TextView humValue;
        TextView pressureValue;
        TextView dateTime;

        public DataItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tempValue = itemView.findViewById(R.id.temp_value);
            humValue = itemView.findViewById(R.id.hum_value);
            pressureValue = itemView.findViewById(R.id.pressure_value);
            dateTime = itemView.findViewById(R.id.date_time);
        }
    }
}