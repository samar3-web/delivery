package com.samar.delivery;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.samar.delivery.Adapter.TaskAdapter;
import com.squareup.picasso.Picasso;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    RecyclerView my_rcv;

    private RecyclerView recyclerViewToDo, recyclerViewInProgress, recyclerViewDelivered;
    private TaskAdapter toDoAdapter, inProgressAdapter, deliveredAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    ChipNavigationBar chipNavigationBar;
    ImageView profile_button;

    List<com.samar.delivery.models.Task> tasks;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
        chipNavigationBar.setItemSelected(R.id.nav_home,
                true);
        profile_button = findViewById(R.id.logout_btn);

       // bottomMenu();
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ProfileIntent = new Intent ( HomeActivity.this,ProfileActivity.class );
                startActivity ( ProfileIntent );

            }
        });

        RetriveUserImage();

       // recyclerViewToDo = findViewById(R.id.recyclerViewToDo);


        //recyclerViewInProgress = findViewById(R.id.recyclerViewInProgress);
        //recyclerViewDelivered = findViewById(R.id.recyclerViewDelivered);

      //  my_rcv = findViewById(R.id.recyclerViewToDo);

        // Initialiser FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Vérifier l'authentification de l'utilisateur lors de la création de l'activité
        checkUserAuthentication();

        databaseReference = FirebaseDatabase.getInstance().getReference("tasksCollection");


        //setupRecyclerView();

        //loadData();

        /*logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to logout from the application?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                getActivity().finish();
                                Toast.makeText(getActivity(), "Logging Out", Toast.LENGTH_SHORT).show();

                                // Start LoginActivity with transition animation
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });*/
        // Create Timeline rows List
        ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();

// Create new timeline row (Row Id)
        TimelineRow myRow = new TimelineRow(0);

// To set the row Date (optional)
        myRow.setDate(new Date());
// To set the row Title (optional)
        myRow.setTitle("Title");
// To set the row Description (optional)
        myRow.setDescription("Description");
// To set the row bitmap image (optional)
        myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.img));
// To set row Below Line Color (optional)
        myRow.setBellowLineColor(Color.argb(255, 0, 0, 100));
// To set row Below Line Size in dp (optional)
        myRow.setBellowLineSize(6);
// To set row Image Size in dp (optional)
        myRow.setImageSize(40);
// To set background color of the row image (optional)
        myRow.setBackgroundColor(Color.argb(255, 30, 100, 0));
// To set the Background Size of the row image in dp (optional)
        myRow.setBackgroundSize(60);
// To set row Date text color (optional)
        myRow.setDateColor(Color.argb(255, 0, 0, 0));
// To set row Title text color (optional)
        myRow.setTitleColor(Color.argb(255, 0, 0, 0));
// To set row Description text color (optional)
        myRow.setDescriptionColor(Color.argb(255, 0, 0, 0));

// Add the new row to the list
        timelineRowsList.add(myRow);
        timelineRowsList.add(myRow);
        timelineRowsList.add(myRow);

// Create the Timeline Adapter
        ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                //if true, list will be sorted by date
                false);

// Get the ListView and Bind it with the Timeline Adapter
        ListView myListView = (ListView) findViewById(R.id.timeline_listView);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the item that was clicked
                TimelineRow row = (TimelineRow) parent.getItemAtPosition(position);
                Toast.makeText(HomeActivity.this, row.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        myListView.setAdapter(myAdapter);
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
    private void RetriveUserImage() {
        // Getting profile picture to set in the profile button
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        firebaseFirestore.collection("USERDATA").document(currentUserEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> snapshot = task.getResult().getData();
                    try {
                        if (snapshot.get("profileUrl").toString() != "") {
                            // If the url is not null, then adding the image
                            Picasso.get().load(snapshot.get("profileUrl").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).into(profile_button);
                        }
                    } catch (Exception e) {
                        Log.d("xxxxxxx", "onComplete Exception in setting data to profile : " + e.getLocalizedMessage());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d("xxxx", "onFailure: " + e.getLocalizedMessage());
            }
        });
     
    }
    /*private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener
                (new ChipNavigationBar.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int i) {
                        Fragment fragment = null;
                        switch (i){
                            case R.id.nav_home:
                                fragment = new ActiveGoalFragment();
                                break;
                            case R.id.nav_new_archive:
                                fragment = new ArchiveGoalFragment();
                                break;
                            case R.id.nav_new_ranking:
                                fragment = new RankFragment();
                                break;
                            case R.id.nav_settings:
                                fragment = new SettingsFragment();
                                break;
                        }
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_container_nav,
                                        fragment).commit();

                    }
                });
    }*/
}