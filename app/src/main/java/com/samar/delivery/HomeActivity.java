package com.samar.delivery;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
        window.setNavigationBarColor(Color.parseColor("#3a67ff"));


        linearLayout1 = findViewById(R.id.linearLayout1);
        linearLayout2 = findViewById(R.id.linearLayout2);
         pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setCount(2); // specify total count of indicators
        pageIndicatorView.clearSelection();
        pageIndicatorView.setSelection(0);

        pageIndicatorView.setAnimationType(AnimationType.WORM);

        linearLayout1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startX = event.getX();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    endX = event.getX();

                    float deltaX = endX - startX;
                    Log.d("lllllllllll11111111111","deltaX :"+deltaX+" yyyyy "+pageIndicatorView.getSelection());

                    if (deltaX < 0) {
                        // Swipe de gauche à droite
                        linearLayout1.animate()
                                .alpha(0.0f)
                                .translationX(-linearLayout1.getWidth())
                                .setDuration(1000)
                                .start();
                        Log.d("01","deltaX :"+deltaX);
                        linearLayout2.animate()
                                .alpha(1.0f)
                                .translationX(0)
                                .setDuration(1000)
                                .start();
                        Log.d("02","deltaX :"+deltaX);


                        linearLayout1.setVisibility(View.GONE);
                        linearLayout2.setVisibility(View.VISIBLE);
                        pageIndicatorView.clearSelection();
                        pageIndicatorView.setSelection(1);
                    }
                }

                return true;
            }

            private float startX;
            private float endX;
        });
        linearLayout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startX = event.getX();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    endX = event.getX();

                    float deltaX = endX - startX;
                    Log.d("lllllllllll2222222222","deltaX :"+deltaX+" yyyyy ");
                    if (deltaX > 0)  {
                        // Swipe de droite à gauche
                        linearLayout2.animate()
                                .alpha(0.0f)
                                .translationX(linearLayout2.getWidth())
                                .setDuration(1000)
                                .start();
                        Log.d("07","deltaX :"+deltaX);

                        linearLayout1.animate()
                                .alpha(1.0f)
                                .translationX(0)
                                .setDuration(1000)
                                .start();
                        Log.d("08","deltaX :"+deltaX);
                        linearLayout2.setVisibility(View.GONE);
                        linearLayout1.setVisibility(View.VISIBLE);
                        pageIndicatorView.clearSelection();
                        pageIndicatorView.setSelection(0);
                    }
                }

                return true;
            }

            private float startX;
            private float endX;
        });

       /* // Initialiser la visibilité des LinearLayout
        linearLayout1.setVisibility(View.VISIBLE);
        linearLayout2.setVisibility(View.GONE);*/

        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
        chipNavigationBar.setItemSelected(R.id.nav_home,
                true);
        profile_button = findViewById(R.id.logout_btn);
     //   gestureDetector = new GestureDetector(this, new SwipeGestureDetector());

        // Get the ListView and Bind it with the Timeline Adapter
        myListView = (ListView) findViewById(R.id.timeline_listView);
        myListView1 = (ListView) findViewById(R.id.timeline_listView1);

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


// Create Timeline rows List
        timelineRowsList = new ArrayList<>();
        timelineRowsList1 = new ArrayList<>();
        loadData();

        Log.d("aaaaaaaaaaaaaaaa","timelineRowsList.size() : "+timelineRowsList.size());
// Create the Timeline Adapter



       // String currentTaskid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the item that was clicked
                TimelineRow row = (TimelineRow) parent.getItemAtPosition(position);
                Toast.makeText(HomeActivity.this, row.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this,TaskDetail.class);
                // Retrieve the currentTaskId using the mapping
                String currentTaskId = taskIdsMap.get(position);
                intent.putExtra("currentTaskid", currentTaskId);
                startActivity(intent);
                Log.d("iiiiiiiiiiid",currentTaskId);

                // finish();
            }
        });

        myListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the item that was clicked
                TimelineRow row = (TimelineRow) parent.getItemAtPosition(position);
                Toast.makeText(HomeActivity.this, row.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this,TaskDetail.class);
                // Retrieve the currentTaskId using the mapping
                String currentTaskId = taskIdsMap1.get(position);
                intent.putExtra("currentTaskid", currentTaskId);

                Log.d("iiiiiiiiiiid",currentTaskId);
                startActivity(intent);
                // finish();
            }
        });

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
                              //  fragment = new ActiveGoalFragment();
                                break;
                            case R.id.nav_new_archive:
                                Intent ProfileIntent = new Intent ( HomeActivity.this,ArchiveActivity.class );
                                startActivity ( ProfileIntent );
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        RetriveUserImage();
        chipNavigationBar.setItemSelected(R.id.nav_home,
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
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    }


