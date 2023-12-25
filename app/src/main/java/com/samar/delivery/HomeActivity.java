package com.samar.delivery;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.rd.PageIndicatorView;
import com.samar.delivery.Adapter.TaskAdapter;
import com.samar.delivery.Fragments.ArchiveFragment;
import com.samar.delivery.Fragments.ChatFragment;
import com.samar.delivery.Fragments.HomeFragment;
import com.samar.delivery.Fragments.NotificationFragment;
import com.samar.delivery.Fragments.SettingsFragment;
import com.squareup.picasso.Picasso;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Window window = getWindow();
        window.setNavigationBarColor(getColor(R.color.blue));


        // Dans votre activité principale ou Application class
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themePreference = preferences.getString("theme_preference", "system");

        if ("system".equals(themePreference)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if ("light".equals(themePreference)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            //setTheme(R.style.Theme_Delivery);
        } else if ("dark".equals(themePreference)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }




        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
        String settingsFragmentPreference = preferences.getString("settings_fragment", "false");
        if(settingsFragmentPreference.equals("true"))
        {
            chipNavigationBar.setItemSelected(R.id.nav_settings,true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_container_nav,
                            new SettingsFragment()).commit();
           // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("settings_fragment", "false");
            editor.apply();

        } else{
            chipNavigationBar.setItemSelected(R.id.nav_home,true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_container_nav,
                            new HomeFragment()).commit();

        }
        Log.d("idididididid",""+settingsFragmentPreference);
        // if(chipNavigationBar.getSelectedItemId())

        Log.d("idididididid",""+chipNavigationBar.getSelectedItemId());
        profile_button = findViewById(R.id.logout_btn);


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
                        fragment = null;
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
                                fragment = new ChatFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frag_container_nav,
                                                fragment).commit();
                                break;
                            case R.id.nav_new_notif:
                                // fragment = new RankFragment();
                                fragment = new NotificationFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frag_container_nav,
                                                fragment).commit();
                                break;
                            case R.id.nav_settings:
                               // fragment = new SettingsFragment();
                                fragment = new SettingsFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frag_container_nav,
                                                fragment).commit();
                                break;
                        }
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("settings_fragment", "false");
                        editor.apply();

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
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
       // super.onBackPressed();
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
      //  finishAffinity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("settings_fragment", "false");
                        editor.apply();
    }*/

    }


