package com.samar.delivery;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.microsoft.maps.MapView;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

public class TaskDetail extends AppCompatActivity {
    private final int GALLERY_INTENT_CODE = 993;
    private final int CAMERA_INTENT_CODE = 990;
    List<com.samar.delivery.models.Task> tasks;

    RecyclerView recyclerView;
    TextView name,consis,left,task_lft_pert, notes;
    TextView Tdays, Dleft, Sdate, Edate;
    RelativeLayout rel;
    String currentUserID;
    String description;
    long Days;
    String task_end, task_create;
    MCalendarView mCalendarView;
    ArrayList<DateData> dataArrayList;
    private StorageReference UserProfileImagesRef;
    ProgressDialog progressDialog;
    DatabaseReference RootRef,HelloREf,newRef,notesRef;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat justDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Handler handler = new Handler();
    private Runnable runnable;
    private static final String MY_API_KEY = "AlwLTKgevIemLkhFY8wA2oDQwpxY8SBBAR8a5dXymXDFKTmfGWKkXnJGQkGzXUMM";
    private MapView mapView;
    private MapElementLayer mPinLayer;
    private MapImage mPinImage;
    private int mUntitledPushpinCount = 0;
    private Geopoint geopoint;
    private ScrollView scrollView;
    ImageView extendedFloatingShareButton;
    ImageView extendedFloatingEditButton;
    ImageView deleteTask, NewNote, resetTask;
    ImageButton add_img;
    ImageView  Alarm;
    CircleImageView taskPic;

    ImageView shareStreak;
    String TaskName;
    String id;

    private WebView signatureWebView;
    private static final int PICK_FILE_REQUEST_CODE = 1;
    private String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private String JUSTDATE_FORMAT = "yyyy-MM-dd";
    private String EVENT_DATE_TIME = "null";
    private FloatingActionButton direction;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Window window = getWindow();
        window.setNavigationBarColor(getColor(R.color.blue));
        InitializationMethod();
        clearCalendar();
        id = getIntent().getStringExtra("currentTaskid");

        RetriveData(id);

        NewNote = findViewById(R.id.newNote);
        NewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(TaskDetail.this, Signature.class);
                startActivity(i2);
            }
        });



        countDownStart();





        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowOptionsforProfilePic();
            }
        });

        //checkBreak();
        direction = findViewById(R.id.fabButton);
        scrollView = findViewById(R.id.scrollView);
        mapView = findViewById(R.id.mapView);
        mapView.setCredentialsKey(MY_API_KEY);
        mapView.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);
        mapView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // désactiver le défilement du ScrollView
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // activer le défilement du ScrollView
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // transmettre les événements tactiles au MapView
                mapView.onTouchEvent(event);
                return true;
            }
        });



    }





    private void InitializationMethod() {

        Intent intent = getIntent();
        id = intent.getStringExtra("LISTKEY");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
       RootRef = FirebaseDatabase.getInstance().getReference("tasksCollection");
        //recyclerView = findViewById(R.id.streaknotes);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        name = findViewById(R.id.desc_task_name);

        Alarm = findViewById(R.id.alarm);

        extendedFloatingEditButton = findViewById(R.id.edit_task_btn);
        consis = findViewById(R.id.desc_task_const);
        left = findViewById(R.id.desc_task_left);
        mCalendarView= findViewById(R.id.history_calendarView);
        task_lft_pert = findViewById(R.id.desc_task_leftper);

        add_img = findViewById(R.id.add_img);
        taskPic = findViewById(R.id.imageIcon);


        //Streak Overview
        Tdays = findViewById(R.id.totalDays);
        Dleft = findViewById(R.id.daysLeft);
        Sdate = findViewById(R.id.startDate);
        Edate = findViewById(R.id.endDate);




    }









    private void ShowOptionsforProfilePic() {

        new MaterialAlertDialogBuilder(TaskDetail.this).setBackground(getResources().getDrawable(R.drawable.material_dialog_box)).setTitle("Change profile photo").setItems(new String[]{"Choose from gallery", "Take a new picture"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i)
                {
                    // Choosing image from gallery
                    case 0:
                        // Defining Implicit Intent to mobile gallery
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(
                                Intent.createChooser(
                                        intent,
                                        "Select Image from here..."),
                                GALLERY_INTENT_CODE);
                        break;

                    // Clicking a new picture using camera
                    case 1:
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cameraIntent,CAMERA_INTENT_CODE);
                        }
                        startActivityForResult(cameraIntent,CAMERA_INTENT_CODE);
                        break;
                }
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null)
        {
            Uri uri = (Uri) data.getData();
            switch (requestCode)
            {
                // Image received from gallery
                case GALLERY_INTENT_CODE:
                    try {
                        // Converting the image uri to bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        taskPic.setImageBitmap(bitmap);
                        uploadTaskPic(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                // Image received from camera
                case CAMERA_INTENT_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    taskPic.setImageBitmap(bitmap);
                    uploadTaskPic(bitmap);
                    break;
            }
        }
    }

    private void uploadTaskPic(Bitmap bitmap) {


        StorageReference storageReference = UserProfileImagesRef.child ( id + ".jpg");


        showProgressDialog();

        // Converting image bitmap to byte array for uploading to firebase storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("Target")
                .child(id+".jpeg");
        byte[] pfp = baos.toByteArray();

        // Uploading the byte array to firebase storage
        storageReference.putBytes(pfp).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // Getting url of the image uploaded to firebase storage
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                // Setting the image url as the user_image property of the user in the database
                                String pfpUrl = task.getResult().toString();
                                RootRef.child(id).child("task_image").setValue(pfpUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Task picture updated", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to upload task picture", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed to upload task picture", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to upload task picture", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showProgressDialog() {
        progressDialog = new ProgressDialog(TaskDetail.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_diaglog);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }


    private void RetriveData(String taskDocId) {


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("tasksCollection").document(taskDocId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("qqqqqqqqqqqqqqqqqq", "Task name : " + documentSnapshot.getId());
                Log.d("qqqqqqqqqqqqqqqqqq", "Task name : " + documentSnapshot.get("name").toString());
                if(documentSnapshot.get("status").toString().equals("faite"))
                {Alarm.setVisibility(View.GONE);

                }
                if (documentSnapshot.get("name") != null)
                    name.setText(documentSnapshot.get("name").toString());

                if (documentSnapshot.get("heureDateFinPrevu") != null) {
                    left.setText(documentSnapshot.get("heureDateFinPrevu").toString());
                    if(documentSnapshot.get("status").toString().equals("faite"))
                    {// Récupérer la date depuis Firestore
                        String dateString = documentSnapshot.get("heureDateFinPrevu").toString();

// Définir le format de la date d'entrée
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

                        try {
                            // Convertir la chaîne de date en objet Date
                            Date date = inputFormat.parse(dateString);

                            // Définir le format de la date de sortie
                            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' HH:mm", Locale.getDefault());

                            // Formater la date en tant que chaîne dans le nouveau format
                            String formattedDate = outputFormat.format(date);

                            // Définir le texte dans la vue
                            left.setText("Done on " + formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        left.setTextColor(getResources().getColor(R.color.blue));

                    }
                    task_create = documentSnapshot.get("heureDateFinPrevu").toString()+":00";;
                }
                if (documentSnapshot.get("heureDateFinPrevu") != null){
                    Edate.setText(documentSnapshot.get("heureDateFinPrevu").toString());
                EVENT_DATE_TIME = documentSnapshot.get("heureDateFinPrevu").toString()+":00";
                    EVENT_DATE_TIME = documentSnapshot.get("heureDateFinPrevu").toString()+":00";
               task_end = documentSnapshot.get("heureDateFinPrevu").toString()+":00";
                }
                if (documentSnapshot.get("heureDateDebutPrevu") != null)
                    Sdate.setText(documentSnapshot.get("heureDateDebutPrevu").toString());

                mPinLayer = new MapElementLayer();
                mapView.getLayers().add(mPinLayer);
                mPinImage = getPinImage();
                if ((documentSnapshot.contains("latitude")&&documentSnapshot.contains("longitude"))&&((documentSnapshot.get("latitude") != null) && (documentSnapshot.get("longitude") != null))) {
                    geopoint = new Geopoint(Double.valueOf(documentSnapshot.get("latitude").toString()), Double.valueOf(documentSnapshot.get("longitude").toString()));
                    addPin(geopoint, documentSnapshot.get("name").toString());
                    mapView.setScene(
                            MapScene.createFromLocationAndZoomLevel(geopoint, 15),
                            MapAnimationKind.NONE);
                    direction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //String uri = "http://maps.google.com/maps?saddr=" + myLatitude + "," + myLongitude + "&daddr=" + elementLatitude + "," + elementLongitude;
                            String uri = "https://www.google.com/maps/dir/?api=1&destination=" + Double.valueOf(documentSnapshot.get("latitude").toString()) + "," + Double.valueOf(documentSnapshot.get("longitude").toString());
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);

                        }
                    });
                }
                else {

                }





            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failures
                Log.e("Firebase", "Error getting document", e);
            }
        });

    }


    private MapImage getPinImage() {
        // Create a pin image from a drawable resource
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pin, null);

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return new MapImage(bitmap);
    }
    private void addPin(Geopoint location, String title) {
        // Add a pin to the map at the given location
        MapIcon pushpin = new MapIcon();
        pushpin.setLocation(location);
        pushpin.setTitle(title);
        pushpin.setImage(mPinImage);

        pushpin.setNormalizedAnchorPoint(new PointF(0.5f, 1f));
        if (title.isEmpty()) {
            pushpin.setContentDescription(String.format(
                    Locale.ROOT,
                    "Untitled pushpin %d",
                    ++mUntitledPushpinCount));
        }
        mPinLayer.getElements().add(pushpin);
    }




    public void AlarmAct(View view) {
        Intent i = new Intent(getApplicationContext(), AlarmActivity.class); //Pass to AlarmActivity Class
        i.putExtra("TaskName", TaskName); //Passing Task Name
        startActivity(i);
    }

    private void   highLightDate(){

        //ArrayList<DateData> dataArrayList;
        /*HelloREf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    dataArrayList = new ArrayList<DateData>();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){

                        String strDate= snapshot1.getKey();
                        // Log.d("ParseException",strDate);
                        int day = Integer.parseInt(strDate.substring(0,strDate.indexOf("-")));
                        int month= Integer.parseInt(strDate.substring(3,strDate.lastIndexOf("-")));
                        int year= Integer.parseInt(strDate.substring(strDate.lastIndexOf("-")+1,9));
                        DateData date= new DateData(year,month,day);
                        dataArrayList.add(date);

                    }
                    // MCalendarView mCalendarView= findViewById(R.id.history_calendarView);
                    for(int i=0; i< dataArrayList.size();i++){

                        DateData date= dataArrayList.get(i);

                        mCalendarView.markDate(date.getYear(),
                                date.getMonth(),
                                date.getDay());

                        mCalendarView.setMarkedStyle(MarkStyle.BACKGROUND,Color.BLUE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearCalendar();
//        progressDialog.dismiss();
    }

    private void clearCalendar(){

        MarkedDates markedDates= mCalendarView.getMarkedDates();

        ArrayList<DateData> currDataList= markedDates.getAll();

        for(int i=0; i<currDataList.size();i++){

            DateData data= currDataList.get(i);

            mCalendarView.unMarkDate(data.getYear(),data.getMonth(),data.getDay());
        }
    }


    private void countDownStart() {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date event_date = dateFormat.parse(EVENT_DATE_TIME);
                    Date current_date = new Date();
                    Date created = dateFormat.parse(task_create);
                    Log.d("oooooooooooooooooooo","Date created = dateFormat.parse(task_create); "+task_create);
                    Log.d("llllllllllllll","Edate.setText(task_end.substring(0,10).trim()); "+task_create.substring(0,10).trim()+"  "+current_date.after(event_date));

                    if (!current_date.after(event_date)) {
                        long diff = event_date.getTime() - current_date.getTime();
                        long diffCreate = (event_date.getTime() - created.getTime()) / (24 * 60 * 60 * 1000);
                        Days = diff / (24 * 60 * 60 * 1000);
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;
                        long totaldays= event_date.getTime()/(24 * 60 * 60 * 1000);
                        long percent= (Days*100/totaldays);
                        //StreakOvewview Data
                        Tdays.setText(String.format("%02d",diffCreate)+"d");
                        Dleft.setText(String.format("%02d",Days)+"d");
                        Sdate.setText(task_create.substring(0,10).trim());
                        //Log.d("llllllllllllll","Edate.setText(task_end.substring(0,10).trim()); "+task_create.substring(0,10).trim());

                        Edate.setText(task_end.substring(0,10).trim());
                        //notes.setText(description);
                        left.setText(String.format("%02d",Days)+" days  "+String.format("%02d", Hours)+":"+String.format("%02d", Minutes)+":"+String.format("%02d", Seconds));
                        if(percent<=33) {
                            left.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                            //rel.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.lightred));
                        }
                        else if(percent<=66)
                        {
                            left.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow));
                            //rel.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.lightyellow));
                        }
                        else
                        {
                            left.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            //rel.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.lightgreen));
                        }
                    } else {

                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

}