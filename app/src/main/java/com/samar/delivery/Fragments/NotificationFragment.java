package com.samar.delivery.Fragments;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.samar.delivery.Adapter.NotificationAdapter;
import com.samar.delivery.R;
import com.samar.delivery.models.Notification;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
// NotificationFragment.java
public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.notificationRecyclerView);
        adapter = new NotificationAdapter();

        // Configurez le RecyclerView avec un LinearLayoutManager, un DividerItemDecoration, etc.
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Chargez et mettez à jour les notifications depuis la base de données ici
        //updateNotifications();
        loadNotifications();

        return view;
    }

    private void updateNotifications() {
        // Exemple : Ajoutez des notifications fictives pour les besoins de l'exemple
        Notification notification1 = new Notification("Tâche 1 modifiée", new Date());
        Notification notification2 = new Notification("Tâche 2 créée", new Date());

        adapter.addNotification(notification1);
        adapter.addNotification(notification2);
    }
    private void loadNotifications() {
        firebaseAuth = FirebaseAuth.getInstance();
        // Vérifier l'authentification de l'utilisateur avant de charger les données
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            // Référence à votre collection Firestore
            CollectionReference InAppNotificationCollection = firestore.collection("InAppNotificationCollection");

// Utiliser addSnapshotListener pour écouter les modifications en temps réel
            InAppNotificationCollection.whereEqualTo("assignedUser", user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    //adapter = new NotificationAdapter();
                    adapter.clearNotifications();
                    if (e != null) {
                        Log.w("FirestoreListener", "Listen failed.", e);
                        return;
                    }
                    if (!isAdded()) {
                        // Le fragment n'est pas attaché à une activité
                        return;
                    }
                    if (snapshot != null && !snapshot.isEmpty()) {
                          for (DocumentSnapshot doc : snapshot.getDocuments()) {
                              Log.d("aaaaaaaaaaaaaaaaaaaaaaaa",""+doc.get("taskName").toString());
                              if(Objects.requireNonNull(doc.get("type")).toString().equals("add")) {
                                  Notification notification1 = new Notification("The task titled \"" + doc.get("taskName").toString() + "\" has been added on", doc.getTimestamp("creationDate").toDate());
                                  adapter.addNotification(notification1);
                              }else{
                                  Notification notification1 = new Notification("The task titled \"" + doc.get("taskName").toString() + "\" has been edited on", doc.getTimestamp("creationDate").toDate());

                                  adapter.addNotification(notification1);
                              }


                        }
                    } else {
                        Log.d("FirestoreListener", "Current data: null");
                    }

                }
            });


        } else {

            Log.d("Firebase", "no user logged in .");
        }


    }
}
