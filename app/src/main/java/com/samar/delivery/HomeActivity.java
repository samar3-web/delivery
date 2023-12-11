package com.samar.delivery;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.samar.delivery.Adapter.TaskAdapter;
import com.squareup.picasso.Picasso;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    ArrayList<TimelineRow> timelineRowsList,timelineRowsList1;

    List<com.samar.delivery.models.Task> tasks;
    private String currentUserEmail;
    ArrayAdapter<TimelineRow> myAdapter,myAdapter1;
    private ListView myListView,myListView1;
    private LinearLayout linearLayout1, linearLayout2;
    private GestureDetector gestureDetector;
    private PageIndicatorView pageIndicatorView;

    private SparseArray<String> taskIdsMap = new SparseArray<>();
    private SparseArray<String> taskIdsMap1 = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Window window = getWindow();
        window.setNavigationBarColor(getColor(R.color.blue));



        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
        chipNavigationBar.setItemSelected(R.id.nav_home,
                true);
        profile_button = findViewById(R.id.logout_btn);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container_nav,
                        new HomeFragment()).commit();
        bottomMenu();
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ProfileIntent = new Intent ( HomeActivity.this,ProfileActivity.class );
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

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("tasksCollection").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            Log.d("xxxxxdocs", "onComplete: of task data fetching " + task.getResult().getDocuments());
                            tasks = new ArrayList<com.samar.delivery.models.Task>();

                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                                String currentTaskId = doc.getId();

// Create new timeline row (Row Id)
                                TimelineRow myRow = new TimelineRow(0);

// To set the row Date (optional)

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                try {
                                   // String d = doc.get("heureDateDebutPrevu").toString().replaceAll("\"", "");
                                    String d = doc.get("heureDateDebutPrevu").toString()+":00";

                                    Date date = dateFormat.parse(d);
                                    Date dateSymitric = calculateSymmetricDate(date);
                                    myRow.setDate(dateSymitric);
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
                                if(doc.get("status").toString().equals("à faire")){
                                timelineRowsList.add(myRow);
                                    // Map the currentTaskId to the position in the list
                                    taskIdsMap.put(timelineRowsList.size() - 1, currentTaskId);
                                }
                                else if(doc.get("status").toString().equals("en cours")){
                                    timelineRowsList1.add(myRow);
                                    // Map the currentTaskId to the position in the list
                                    taskIdsMap1.put(timelineRowsList1.size() - 1, currentTaskId);

                                }
                                Log.d("pppppppppppppp","timelineRowsList.size() : "+timelineRowsList.size());


                            }
                            myAdapter = new TimelineViewAdapter(getApplicationContext(), 0, timelineRowsList,
                                    //if true, list will be sorted by date
                                    false);
                            myListView.setAdapter(myAdapter);
                            myAdapter1 = new TimelineViewAdapter(getApplicationContext(), 0, timelineRowsList1,
                                    //if true, list will be sorted by date
                                    false);
                            myListView1.setAdapter(myAdapter1);
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
    /*private void RetriveUserImage() {
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
     
    }*/
    private void RetriveUserImage() {
        // Getting profile picture to set in the profile button
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference userDocument = firebaseFirestore.collection("USERDATA").document(currentUserEmail);

        // Utiliser addSnapshotListener pour écouter les modifications en temps réel
        userDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("FirestoreListener", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    // Document a été modifié, mettre à jour l'image dans votre application

                    try {
                        String profileUrl = snapshot.getString("profileUrl");

                        if (profileUrl != null && !profileUrl.isEmpty()) {
                            // Si l'URL n'est pas nulle, alors ajoutez l'image
                            Picasso.get().load(profileUrl).placeholder(R.drawable.profile).error(R.drawable.profile).into(profile_button);
                        }
                    } catch (Exception ex) {
                        Log.d("xxxxxxx", "Exception in setting data to profile : " + ex.getLocalizedMessage());
                    }
                } else {
                    Log.d("FirestoreListener", "Current data: null");
                }
            }
        });
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener
                (new ChipNavigationBar.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int i) {
                        Fragment fragment = null;
                        switch (i){
                            case R.id.nav_home:
                                fragment = new HomeFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frag_container_nav,
                                                fragment).commit();
                                break;
                            case R.id.nav_new_archive:
                                /*Intent ProfileIntent = new Intent ( HomeActivity.this,ArchiveActivity.class );
                                startActivity ( ProfileIntent );
                                break;*/
                            fragment = new ArchiveFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frag_container_nav,
                                                fragment).commit();
                            break;
                            case R.id.nav_new_chat:
                               // fragment = new RankFragment();
                                break;
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

    public static Date calculateSymmetricDate(Date givenDate) {
        if (givenDate == null) {
            // Retourne la date actuelle si givenDate est null
            return new Date();
        }
        Date currentDate = new Date();
        long timeDifference = givenDate.getTime() - currentDate.getTime();
        long symmetricTime = currentDate.getTime() - timeDifference;

        return new Date(symmetricTime);
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit the application?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            /*Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();*/
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                           //onDestroy();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }*/

    }


