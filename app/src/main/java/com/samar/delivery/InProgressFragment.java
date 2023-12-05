package com.samar.delivery;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InProgressFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView myListView1;
    private SparseArray<String> taskIdsMap1 = new SparseArray<>();
    private ChipNavigationBar chipNavigationBar;
    ImageView profile_button;
    private ListView myListViewDone;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private List<com.samar.delivery.models.Task> tasks;
    private TimelineViewAdapter myAdapterDone;
    private String currentUserEmail;
    ArrayList<TimelineRow> timelineRowsList1;
    ArrayAdapter<TimelineRow> myAdapter1;
    public InProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InProgressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InProgressFragment newInstance(String param1, String param2) {
        InProgressFragment fragment = new InProgressFragment();
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
        View view= inflater.inflate(R.layout.fragment_in_progress, container, false);

        // Initialiser FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Vérifier l'authentification de l'utilisateur lors de la création de l'activité
        checkUserAuthentication();

        databaseReference = FirebaseDatabase.getInstance().getReference("tasksCollection");


        //setupRecyclerView();


// Create Timeline rows List
        timelineRowsList1 = new ArrayList<>();
        loadData();
        myListView1 = (ListView) view.findViewById(R.id.timeline_listView1);

        myListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the item that was clicked
                TimelineRow row = (TimelineRow) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), row.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),TaskDetail.class);
                // Retrieve the currentTaskId using the mapping
                String currentTaskId = taskIdsMap1.get(position);
                intent.putExtra("currentTaskid", currentTaskId);

                Log.d("iiiiiiiiiiid",currentTaskId);
                startActivity(intent);
                // finish();
            }
        });
        return view;
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
                                if(doc.get("status").toString().equals("en cours")){
                                    timelineRowsList1.add(myRow);
                                    // Map the currentTaskId to the position in the list
                                    taskIdsMap1.put(timelineRowsList1.size() - 1, currentTaskId);

                                }



                            }
                            myAdapter1 = new TimelineViewAdapter(getContext(), 0, timelineRowsList1,
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
}