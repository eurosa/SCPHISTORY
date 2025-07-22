package com.googleapi.scpandroiddata;



import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class DataTableFragment extends Fragment {
    private static final String ARG_DATA_ITEMS = "data_items";
    private List<DataItem> dataItems;

    public static DataTableFragment newInstance(List<DataItem> dataItems) {
        DataTableFragment fragment = new DataTableFragment();
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
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.data_recycler_view);

        DataItemAdapter adapter = new DataItemAdapter(dataItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}