package com.googleapi.scpandroiddata;



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

        // Set values with null checks
        holder.tempValue.setText(item.getTemp_value() != null ? item.getTemp_value() : "N/A");
        holder.humValue.setText(item.getHum_value() != null ? item.getHum_value() : "N/A");
        holder.pressureValue.setText(item.getAir_pressure_value() != null ? item.getAir_pressure_value() : "N/A");
        holder.dateTime.setText(item.getDate_time() != null ? item.getDate_time() : "N/A");

        // Alternate row colors for better readability
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.row_even));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.row_odd));
        }
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