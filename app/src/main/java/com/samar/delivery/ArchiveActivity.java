package com.samar.delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ArchiveActivity extends AppCompatActivity {

    private ChipNavigationBar chipNavigationBar;
    ImageView profile_button;
    private ListView myListViewDone;
    private FirebaseAuth firebaseAuth;
    private ArrayList<TimelineRow> timelineRowsListDone;
    private DatabaseReference databaseReference;
    private List<com.samar.delivery.models.Task> tasks;
    private TimelineViewAdapter myAdapterDone;
    private String currentUserEmail;
    private SparseArray<String> taskIdsMap = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        Window window = getWindow();
        window.setNavigationBarColor(Color.parseColor("#3a67ff"));

        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
        chipNavigationBar.setItemSelected(R.id.nav_new_archive,
                true);
        profile_button = findViewById(R.id.logout_btn);

        // Get the ListView and Bind it with the Timeline Adapter
        myListViewDone = (ListView) findViewById(R.id.timeline_listViewDone);

        bottomMenu();
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ProfileIntent = new Intent ( ArchiveActivity.this,ProfileActivity.class );
                startActivity ( ProfileIntent );
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        RetriveUserImage();

        // Initialiser FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Vérifier l'authentification de l'utilisateur lors de la création de l'activité
        checkUserAuthentication();

        databaseReference = FirebaseDatabase.getInstance().getReference("tasksCollection");
        // Create Timeline rows List
        timelineRowsListDone = new ArrayList<>();
        loadData();

      
// Create the Timeline Adapter




        myListViewDone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the item that was clicked
                TimelineRow row = (TimelineRow) parent.getItemAtPosition(position);
                Toast.makeText(ArchiveActivity.this, row.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ArchiveActivity.this,TaskDetail.class);
                String currentTaskId = taskIdsMap.get(position);
                intent.putExtra("currentTaskid", currentTaskId);
                startActivity(intent);
                // finish();
            }
        });
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
                                String currentTaskId = doc.getId();
                                    /*com.samar.delivery.models.Task task1 = new com.samar.delivery.models.Task();
                                    task1.setId(doc.getId());
                                    task1.setLibelle(doc.get("name").toString());
                                    task1.setDuree(doc.get("duree").toString());
                                    task1.setStatus((doc.get("status")).toString());*/
                                //task1.setHeureDateDebutReelle(doc.get("HeureDateDebutReelle").toString());
                                //task1.setHeureDateFinReelle(doc.get("setHeureDateFinReelle").toString());
// Create new timeline row (Row Id)
                                TimelineRow myRow = new TimelineRow(0);

// To set the row Date (optional)
                               /* SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

                                try {
                                    Date date = dateFormat.parse(doc.get("heureDateDebutPrevu").toString());
                                    myRow.setDate(date);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }*/
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                try {
                                    // String d = doc.get("heureDateDebutPrevu").toString().replaceAll("\"", "");
                                    String d = doc.get("heureDateDebutPrevu").toString()+":00";

                                    Date date = dateFormat.parse(d);
                                   // Date dateSymitric = calculateSymmetricDate(date);
                                    myRow.setDate(date);
                                    myRow.setDateColor(Color.argb(255, 30, 100, 0));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
// To set the row Title (optional)
                                /*myRow.setDate(new Date());*/
                                myRow.setTitle(doc.get("name").toString());
// To set the row Description (optional)
                                myRow.setDescription(doc.get("description").toString());
// To set the row bitmap image (optional)
                                myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.img));

// To set row Below Line Size in dp (optional)
                                myRow.setBellowLineSize(6);
// To set row Image Size in dp (optional)
                                myRow.setImageSize(30);
// To set background color of the row image (optional)
                                switch (doc.get("priority").toString()) {
                                    case "basse":
                                        myRow.setBackgroundColor(Color.argb(255, 30, 100, 0));
                                        // To set row Below Line Color (optional)
                                        myRow.setBellowLineColor(Color.argb(255, 30, 100, 0));

                                        break;
                                    case "moyenne":
                                        myRow.setBackgroundColor(Color.argb(255, 255, 165, 0));
                                        // To set row Below Line Color (optional)
                                        myRow.setBellowLineColor(Color.argb(255, 255, 165, 0));
                                        break;
                                    case "haute":
                                        myRow.setBackgroundColor(Color.argb(255, 255, 0, 0));
                                        // To set row Below Line Color (optional)
                                        myRow.setBellowLineColor(Color.argb(255, 255, 0, 0));
                                        break;
                                    default:
                                        System.out.println("Priorité non valide");
                                }
                                // myRow.setBackgroundColor(Color.argb(255, 30, 100, 0));
// To set the Background Size of the row image in dp (optional)
                                myRow.setBackgroundSize(40);
// To set row Date text color (optional)
                                myRow.setDateColor(Color.argb(255, 0, 0, 0));
// To set row Title text color (optional)
                                myRow.setTitleColor(Color.argb(255, 0, 0, 0));
// To set row Description text color (optional)
                                myRow.setDescriptionColor(Color.argb(255, 0, 0, 0));

// Add the new row to the list
                                if(doc.get("status").toString().equals("faite")){
                                    timelineRowsListDone.add(myRow);
                                    // Map the currentTaskId to the position in the list
                                    taskIdsMap.put(timelineRowsListDone.size() - 1, currentTaskId);
                                }




                                   /* tasks.add(task1);

                                toDoAdapter = new TaskAdapter(HomeActivity.this, tasks, R.layout.tache_cardview);

                                // Ajuster l'adaptateur et le gestionnaire de disposition de la recyclerViewToDo existante
                                recyclerViewToDo.setAdapter(toDoAdapter);
                                recyclerViewToDo.setLayoutManager(new LinearLayoutManager(HomeActivity.this));*/
                            }
                            myAdapterDone = new TimelineViewAdapter(getApplicationContext(), 0, timelineRowsListDone,
                                    //if true, list will be sorted by date
                                    false);
                            myListViewDone.setAdapter(myAdapterDone);

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
    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener
                (new ChipNavigationBar.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int i) {
                        //  Fragment fragment = null;
                        switch (i){
                            case R.id.nav_home:
                                Intent ProfileIntent = new Intent ( ArchiveActivity.this,HomeActivity.class );
                                startActivity ( ProfileIntent );
                                break;
                            case R.id.nav_new_archive:

                                break;
                            /*case R.id.nav_new_chat:
                                // fragment = new RankFragment();
                                break;*/
                            case R.id.nav_settings:
                                // fragment = new SettingsFragment();
                                break;
                        }
                       /* getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_container_nav,
                                        fragment).commit();*/

                    }
                });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        RetriveUserImage();
        chipNavigationBar.setItemSelected(R.id.nav_new_archive,
                true);
        //   loadData();
    }


    //pour bien calculer la date de laiste view (remaining) par symmetric

    private static Date calculateSymmetricDate(Date givenDate) {
        Date currentDate = new Date();
        long timeDifference = givenDate.getTime() - currentDate.getTime();
        long symmetricTime = currentDate.getTime() - timeDifference;

        return new Date(symmetricTime);
    }
}