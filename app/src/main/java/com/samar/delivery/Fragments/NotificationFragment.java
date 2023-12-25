package com.samar.delivery.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samar.delivery.Adapter.NotificationAdapter;
import com.samar.delivery.R;
import com.samar.delivery.models.Notification;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
// NotificationFragment.java
public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.notificationRecyclerView);
        adapter = new NotificationAdapter();

        // Configurez le RecyclerView avec un LinearLayoutManager, un DividerItemDecoration, etc.
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Chargez et mettez à jour les notifications depuis la base de données ici
        updateNotifications();

        return view;
    }

    private void updateNotifications() {
        // Exemple : Ajoutez des notifications fictives pour les besoins de l'exemple
        Notification notification1 = new Notification("Tâche 1 modifiée", new Date());
        Notification notification2 = new Notification("Tâche 2 créée", new Date());

        adapter.addNotification(notification1);
        adapter.addNotification(notification2);
    }
}
