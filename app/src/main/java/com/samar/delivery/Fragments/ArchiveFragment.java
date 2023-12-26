package com.samar.delivery.Fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.samar.delivery.R;
import com.samar.delivery.TaskDetail;
import com.samar.delivery.models.Task;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArchiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArchiveFragment extends Fragment {

    public static final int REQUEST_TASK_DETAIL = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageView profile_button;
    TextView counter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ChipNavigationBar chipNavigationBar;
    private ListView myListViewDone;
    private FirebaseAuth firebaseAuth;
    private ArrayList<TimelineRow> timelineRowsListDone;
    private DatabaseReference databaseReference;
    private List<Task> tasks;
    private TimelineViewAdapter myAdapterDone;
    private String currentUserEmail;
    private SparseArray<String> taskIdsMap = new SparseArray<>();
    private final SparseArray<String> clonedTaskIdsMap = new SparseArray<>();
    private EditText taskSearch;

    public ArchiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArchiveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArchiveFragment newInstance(String param1, String param2) {
        ArchiveFragment fragment = new ArchiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_archive, container, false);

        // Get the ListView and Bind it with the Timeline Adapter
        myListViewDone = view.findViewById(R.id.timeline_listViewDone);
        taskSearch = view.findViewById(R.id.goal_search);
        counter = view.findViewById(R.id.counter);
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
                Toast.makeText(getContext(), row.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), TaskDetail.class);
                String currentTaskId = taskIdsMap.get(position);
                intent.putExtra("currentTaskid", currentTaskId);
                //startActivity(intent);
                // Launch TaskDetail with startActivityForResult
                startActivityForResult(intent, REQUEST_TASK_DETAIL);
                // finish();
            }
        });
        taskSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Creating new list of tasks based on the entered value in the taskSearch
                ArrayList<TimelineRow> newTimelineRowsList = new ArrayList<>();
                SparseArray<String> newTaskIdsMap = new SparseArray<>();

                for (int i = 0; i < timelineRowsListDone.size(); i++) {
                    // Get the TimelineRow at position i
                    TimelineRow row = timelineRowsListDone.get(i);

                    // Check if the entered text matches with the task name or description
                    if (row.getTitle().toLowerCase().contains(editable.toString().toLowerCase()) ||
                            row.getDescription().toLowerCase().contains(editable.toString().toLowerCase())) {
                        // Add the matching TimelineRow to the new list
                        newTimelineRowsList.add(row);

                        // Map the position in the new list to the currentTaskId
                        newTaskIdsMap.put(newTimelineRowsList.size() - 1, clonedTaskIdsMap.get(i));
                    }
                }
                taskIdsMap = null;
                taskIdsMap = newTaskIdsMap.clone();


                // Update the adapter with the new list of TimelineRows
                myAdapterDone = new TimelineViewAdapter(getContext(), 0, newTimelineRowsList,
                        //if true, list will be sorted by date
                        false);
                myListViewDone.setAdapter(myAdapterDone);
                myAdapterDone.notifyDataSetChanged();


            }
        });
        return view;
    }

    private void loadData() {
        // Vérifier l'authentification de l'utilisateur avant de charger les données
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            // Référence à votre collection Firestore
            CollectionReference tasksCollection = firestore.collection("tasksCollection");
            // Utiliser addSnapshotListener pour écouter les modifications en temps réel
            tasksCollection.whereEqualTo("assignedUser", user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("FirestoreListener", "Listen failed.", e);
                        return;
                    }
                    if (!isAdded()) {
                        // Le fragment n'est pas attaché à une activité
                        return;
                    }
                    int numberOfTasksDone = 0;

                    if (snapshot != null && !snapshot.isEmpty()) {
                        // La collection a été modifiée, mettre à jour les données dans votre application

                        timelineRowsListDone.clear();
                        tasks = new ArrayList<>();

                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            // Votre code pour extraire les données et mettre à jour l'interface utilisateur
                            // ...
                            if (doc.get("status").toString().equals("faite")) {
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
                                String d = doc.get("heureFinReelle").toString() + ":00";
                                Log.d("*******************", " " + d + " " + currentTaskId);
                                Date date = dateFormat.parse(d);
                                // Date dateSymitric = calculateSymmetricDate(date);
                                myRow.setDate(date);
                                myRow.setDateColor(Color.argb(255, 30, 100, 0));
                            } catch (ParseException ex) {
                                throw new RuntimeException(ex);
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
                            myRow.setDateColor(getResources().getColor(R.color.colorTheme2));
// To set row Title text color (optional)
                            myRow.setTitleColor(getResources().getColor(R.color.colorTheme2));
// To set row Description text color (optional)
                            myRow.setDescriptionColor(getResources().getColor(R.color.colorTheme2));

// Add the new row to the list
                          //  if (doc.get("status").toString().equals("faite")) {
                                timelineRowsListDone.add(myRow);
                                // Map the currentTaskId to the position in the list
                                taskIdsMap.put(timelineRowsListDone.size() - 1, currentTaskId);
                                numberOfTasksDone++; // Incrémente le compteur pour les tâches faite
                            }


// SparseArray<String> clonedTaskIdsMap = new SparseArray<>();
                            for (int i = 0; i < taskIdsMap.size(); i++) {
                                clonedTaskIdsMap.put(taskIdsMap.keyAt(i), taskIdsMap.valueAt(i));
                            }

                        }
                    } else {
                        Log.d("FirestoreListener", "Current data: null");

                    }
                    myAdapterDone = new TimelineViewAdapter(getContext(), 0, timelineRowsListDone,
                            //if true, list will be sorted by date
                            false);
                    myListViewDone.setAdapter(myAdapterDone);
                    myAdapterDone.notifyDataSetChanged();
                    // Afficher le compteur dans votre TextView
                    if (numberOfTasksDone > 0) {
                        counter.setVisibility(View.VISIBLE);
                        counter.setText("Total Tasks Done : " + numberOfTasksDone);
                    } else {
                        counter.setVisibility(View.GONE);
                    }

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