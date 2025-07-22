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
import java.util.ArrayList;
import java.util.List;

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

        for (int i = 0; i < dataItems.size(); i++) {
            DataItem item = dataItems.get(i);
            try {
                float pressure = Float.parseFloat(item.getAir_pressure_value());
                entries.add(new Entry(i, pressure));
            } catch (NumberFormatException e) {
                // Skip invalid data
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Pressure (hPa)");
        dataSet.setColor(Color.GREEN);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.GREEN);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Configure X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new DateAxisValueFormatter(dataItems));

        chart.getDescription().setText("Pressure over Time");
        chart.invalidate(); // refresh
    }
}