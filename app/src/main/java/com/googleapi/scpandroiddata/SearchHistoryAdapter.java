package com.googleapi.scpandroiddata;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder> {
    private List<SearchHistoryItem> historyItems;
    private OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onEditClick(SearchHistoryItem item);
        void onDeleteClick(SearchHistoryItem item);
    }

    public SearchHistoryAdapter(List<SearchHistoryItem> historyItems, OnHistoryItemClickListener listener) {
        this.historyItems = historyItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_history, parent, false);
        return new SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryViewHolder holder, int position) {
        SearchHistoryItem item = historyItems.get(position);
        holder.value.setText(item.getValue());

        holder.editButton.setOnClickListener(v -> listener.onEditClick(item));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public void updateData(List<SearchHistoryItem> newData) {
        this.historyItems = newData;
        notifyDataSetChanged();
    }

    static class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView value;
        View editButton;
        View deleteButton;

        public SearchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            value = itemView.findViewById(R.id.history_value);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}