package com.googleapi.scpandroiddata;


import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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

public class PressureGraphFragment extends Fragment {
    private static final String ARG_DATA_ITEMS = "data_items";
    private List<DataItem> dataItems;

    public static PressureGraphFragment newInstance(List<DataItem> dataItems) {
        PressureGraphFragment fragment = new PressureGraphFragment();
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

        if (dataItems != null && !dataItems.isEmpty()) {
            setupPressureChart(chart);
        }

        return view;
    }

    private void setupPressureChart(LineChart chart) {
        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        for (DataItem item : dataItems) {
            try {
                float pressure = Float.parseFloat(item.getAir_pressure_value());
                Date date = sdf.parse(item.getDate_time());
                entries.add(new Entry(date.getTime(), pressure));
            } catch (NumberFormatException | ParseException e) {
                // Skip invalid data
            }
        }

        if (entries.isEmpty()) return;

        // Sort entries by timestamp
        Collections.sort(entries, Comparator.comparing(Entry::getX));

        LineDataSet dataSet = new LineDataSet(entries, "Pressure (hPa)");
        dataSet.setColor(Color.GREEN);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.GREEN);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Set up marker view
        ChartMarkerView mv = new ChartMarkerView(chart.getContext(), R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);

        // Configure X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd HH:mm");

            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(new Date((long) value));
            }
        });

        // Set axis bounds
        xAxis.setAxisMinimum(entries.get(0).getX());
        xAxis.setAxisMaximum(entries.get(entries.size() - 1).getX());

        chart.getDescription().setText("Pressure over Time");

        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate(); // Refresh the chart
    }

}