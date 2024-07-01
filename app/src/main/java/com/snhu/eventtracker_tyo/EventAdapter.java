package com.snhu.eventtracker_tyo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;
    private OnDeleteClickListener onDeleteClickListener;

    public EventAdapter(Context context, List<Event> eventList, OnDeleteClickListener onDeleteClickListener) {
        this.context = context;
        this.eventList = eventList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventNameTextView.setText(event.getName());
        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(position));

        holder.notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, NotificationSettingsActivity.class);
            intent.putExtra("event_name", event.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        Button deleteButton, notificationButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            notificationButton = itemView.findViewById(R.id.notificationButton);
        }
    }
}
