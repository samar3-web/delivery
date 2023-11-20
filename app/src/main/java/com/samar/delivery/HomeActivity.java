package com.samar.delivery;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.samar.delivery.Adapter.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    RecyclerView my_rcv;

    private RecyclerView recyclerViewToDo, recyclerViewInProgress, recyclerViewDelivered;
    private TaskAdapter toDoAdapter, inProgressAdapter, deliveredAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    List<com.samar.delivery.models.Task> tasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerViewToDo = findViewById(R.id.recyclerViewToDo);
        //recyclerViewInProgress = findViewById(R.id.recyclerViewInProgress);
        //recyclerViewDelivered = findViewById(R.id.recyclerViewDelivered);

        my_rcv = findViewById(R.id.recyclerViewToDo);

        // Initialiser FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Vérifier l'authentification de l'utilisateur lors de la création de l'activité
        checkUserAuthentication();

        databaseReference = FirebaseDatabase.getInstance().getReference("tasksCollection");


        //setupRecyclerView();
        loadData();
    }
    private void setupRecyclerView() {
        recyclerViewToDo.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewInProgress.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDelivered.setLayoutManager(new LinearLayoutManager(this));

        toDoAdapter = new TaskAdapter(new ArrayList<>()); // Initialisation avec une liste vide
        inProgressAdapter = new TaskAdapter(new ArrayList<>());
        deliveredAdapter = new TaskAdapter(new ArrayList<>());


        recyclerViewToDo.setAdapter(toDoAdapter);
        recyclerViewInProgress.setAdapter(inProgressAdapter);
        recyclerViewDelivered.setAdapter(deliveredAdapter);
    }

    private void loadData() {
        // Vérifier l'authentification de l'utilisateur avant de charger les données
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // L'utilisateur est authentifié, vous pouvez accéder à la base de données
            // Récupérer les tâches à faire depuis Firebase
           /* databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("Firebase", "succées");

                    List<Task> allTasks = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Task task = snapshot.getValue(Task.class);
                        allTasks.add(task);
                    }

                    // Vous pouvez maintenant utiliser la liste de toutes les tâches comme vous le souhaitez
                    // Par exemple, vous pouvez filtrer les tâches par statut ici si nécessaire

                    // Exemple de filtrage pour les tâches "à faire"
                    //List<Task> toDoList = filterTasksByStatus(allTasks, "à faire");
                    //toDoAdapter.setTaskList(toDoList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Gestion des erreurs
                }
            });

           /* databaseReference.orderByChild("status").equalTo("à faire").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Task> toDoList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Task task = snapshot.getValue(Task.class);
                        toDoList.add(task);
                    }
                    toDoAdapter.setTaskList(toDoList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Gestion des erreurs
                }
            });*/

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("tasksCollection").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            Log.d("xxxxxdocs", "onComplete: of task data fetching " + task.getResult().getDocuments());
                            tasks = new ArrayList<com.samar.delivery.models.Task>();
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    com.samar.delivery.models.Task task1 = new com.samar.delivery.models.Task();
                                    task1.setId(doc.getId());
                                    task1.setLibelle(doc.get("name").toString());
                                    task1.setDuree(doc.get("duree").toString());
                                    task1.setStatus((doc.get("status")).toString());
                                    //task1.setHeureDateDebutReelle(doc.get("HeureDateDebutReelle").toString());
                                    //task1.setHeureDateFinReelle(doc.get("setHeureDateFinReelle").toString());


                                    tasks.add(task1);

                                toDoAdapter = new TaskAdapter(HomeActivity.this, tasks, R.layout.tache_cardview);

                                // Ajuster l'adaptateur et le gestionnaire de disposition de la recyclerViewToDo existante
                                recyclerViewToDo.setAdapter(toDoAdapter);
                                recyclerViewToDo.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.d("xxxxx", "onFailure: of HouseData fectching " + e.getLocalizedMessage());
                        }
                    });


        } else {

            Log.d("Firebase", "no user logged in .");
        }

    }

    private void checkUserAuthentication() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {

        }
    }
}