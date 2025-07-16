package com.googleapi.scpandroiddata;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SearchHistoryHelper {
    private static final String PREF_NAME = "DeviceIdHistoryPrefs";
    private static final String HISTORY_KEY = "device_id_history";
    private static final int MAX_HISTORY_ITEMS = 10;

    private final SharedPreferences sharedPreferences;

    public SearchHistoryHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveDeviceIdHistory(Set<String> deviceIds) {
        // Convert to list and keep only the last 10 items
        List<String> list = new ArrayList<>(deviceIds);
        if (list.size() > MAX_HISTORY_ITEMS) {
            list = list.subList(0, MAX_HISTORY_ITEMS);
        }

        sharedPreferences.edit()
                .putStringSet(HISTORY_KEY, new LinkedHashSet<>(list))
                .apply();
    }

    public Set<String> loadDeviceIdHistory() {
        return sharedPreferences.getStringSet(HISTORY_KEY, new LinkedHashSet<>());
    }
}