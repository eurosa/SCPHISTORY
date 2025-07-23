package com.googleapi.scpandroiddata;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final List<DataItem> dataItems;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<DataItem> dataItems) {
        super(fragmentActivity);
        this.dataItems = dataItems;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return TemperatureGraphFragment.newInstance(dataItems);
            case 1:
                return HumidityGraphFragment.newInstance(dataItems);
            case 2:
                return PressureGraphFragment.newInstance(dataItems);
            case 3:
                return DataTableFragment.newInstance(dataItems);
            default:
                return DataTableFragment.newInstance(dataItems);
        }
    }

    @Override
    public int getItemCount() {
        return 4; // 3 graphs + 1 table
    }
}
