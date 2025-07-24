package com.googleapi.scpandroiddata;



import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TemperatureGraphFragment extends Fragment {
    private static final String ARG_DATA_ITEMS = "data_items";
    private List<DataItem> dataItems;

    public static TemperatureGraphFragment newInstance(List<DataItem> dataItems) {
        TemperatureGraphFragment fragment = new TemperatureGraphFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA_ITEMS, new ArrayList<>(dataItems));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dataItems = (List<DataItem>) getArguments().getSerializable(ARG_DATA_ITEMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        LineChart chart = view.findViewById(R.id.chart);
        Log.d("Date&Time ","Empty");
        if (dataItems != null && !dataItems.isEmpty()) {
            Log.d("Date&Time ","not Empty");
            setupTemperatureChart(chart);
        }

        return view;
    }

    private void setupTemperatureChart(LineChart chart) {
        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        for (DataItem item : dataItems) {
            try {
                float temp = Float.parseFloat(item.getTemp_value());
                Date date = sdf.parse(item.getDate_time());
                Log.d("Date&Time ",item.getTemp_value());
                entries.add(new Entry(date.getTime(), temp));
            } catch (NumberFormatException | ParseException e) {
                // Skip invalid data
            }
        }

        if (entries.isEmpty()) return;

        // Sort entries by timestamp
        Collections.sort(entries, Comparator.comparing(Entry::getX));

        LineDataSet dataSet = new LineDataSet(entries, "Temperature (°C)");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Marker view for chart
        ChartMarkerView mv = new ChartMarkerView(chart.getContext(), R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);

        // Configure X-axis with date formatting
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        chart.getAxisLeft().setTextColor(Color.RED);
        chart.getAxisRight().setEnabled(true); // Optional
        xAxis.setTextColor(Color.RED); // Replace RED with any color you want
        xAxis.setLabelRotationAngle(-45);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(new Date((long) value));
            }
        });

        // Set X-axis bounds
        xAxis.setAxisMinimum(entries.get(0).getX());
        xAxis.setAxisMaximum(entries.get(entries.size() - 1).getX());

        chart.getDescription().setText("Temperature over Time");
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate(); // refresh
    }

    /*private void setupTemperatureChart(LineChart chart) {
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < dataItems.size(); i++) {
            DataItem item = dataItems.get(i);
            try {
                float temp = Float.parseFloat(item.getTemp_value());
                entries.add(new Entry(i, temp));
            } catch (NumberFormatException e) {
                // Skip invalid data
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Temperature (°C)");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Configure X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new DateAxisValueFormatter(dataItems));

        chart.getDescription().setText("Temperature over Time");
        chart.notifyDataSetChanged();
        chart.invalidate(); // refresh
    }*/
}
