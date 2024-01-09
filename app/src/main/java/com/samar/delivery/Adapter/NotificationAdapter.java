package com.samar.delivery.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samar.delivery.R;
import com.samar.delivery.models.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

// NotificationAdapter.java
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> notifications = new ArrayList<>();

    public void addNotification(Notification notification) {
        notifications.add(notification);
        sortNotificationsByDate();
        notifyItemInserted(notifications.indexOf(notification));
       // notifyItemInserted(notifications.size() - 1);
    }
    public void clearNotifications() {
        notifications.clear();
        notifyDataSetChanged();
    }
    private void sortNotificationsByDate() {
        Collections.sort(notifications, new Comparator<Notification>() {
            @Override
            public int compare(Notification notification1, Notification notification2) {
                return notification2.getNotificationDate().compareTo(notification1.getNotificationDate());
            }
        });
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notificationTitle);
            dateTextView = itemView.findViewById(R.id.notificationDate);
        }

        public void bind(Notification notification) {
            titleTextView.setText(notification.getTaskTitle());
            // Format de date à adapter selon vos besoins
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(notification.getNotificationDate());
            dateTextView.setText(formattedDate);
        }
    }
}

