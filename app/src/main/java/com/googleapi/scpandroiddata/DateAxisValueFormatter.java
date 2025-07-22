package com.googleapi.scpandroiddata;



import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.List;

public class DateAxisValueFormatter extends ValueFormatter {
    private final List<DataItem> dataItems;

    public DateAxisValueFormatter(List<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @Override
    public String getFormattedValue(float value) {
        int index = (int) value;
        if (index >= 0 && index < dataItems.size()) {
            return dataItems.get(index).getDate_time();
        }
        return "";
    }
}
