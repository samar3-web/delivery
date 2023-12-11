package com.samar.delivery;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

public class TaskDetail extends AppCompatActivity {
    private final int GALLERY_INTENT_CODE = 993;
    private final int CAMERA_INTENT_CODE = 990;
    private static final int PDF_INTENT_CODE = 992; // You can choose any unique code

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
    ImageView deleteTask, fileUploadButton, resetTask;
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
    private String currentUserEmail;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference, taskReference;
    private List<String> fileUrls;
    private ImageView button_supprimer;
    private ListenerRegistration documentSnapshotListener;
    private AlertDialog dialog;

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Window window = getWindow();
        window.setNavigationBarColor(getColor(R.color.blue));
        id = getIntent().getStringExtra("currentTaskid");
        InitializationMethod();
        clearCalendar();


        RetriveData(id);

        fileUploadButton = findViewById(R.id.newFile);
        fileUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionsForFileUpload();
            }
        });




        countDownStart();





        /*add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowOptionsforProfilePic();
            }
        });*/

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
        //id = intent.getStringExtra("LISTKEY");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
       RootRef = FirebaseDatabase.getInstance().getReference("tasksCollection");

      //  EVENT_DATE_TIME = documentSnapshot.get("heureDateFinPrevu").toString()+":00";
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
        firebaseFirestore = FirebaseFirestore.getInstance();

        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        documentReference = firebaseFirestore.collection("USERDATA").document(currentUserEmail);
        Log.d("qqqqqqqqqqqqqqqqq"," "+id);
        taskReference = firebaseFirestore.collection("tasksCollection").document(id);




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
        DocumentReference documentReference = firebaseFirestore.collection("tasksCollection").document(taskDocId);

        documentSnapshotListener = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Firebase", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("Firebase", "Current data: " + documentSnapshot.getData());

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
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            left.setTextColor(getResources().getColor(R.color.blue));

                        }
                        task_create = documentSnapshot.get("heureDateFinPrevu").toString()+":00";;
                    }
                    if (documentSnapshot.get("heureDateFinPrevu") != null){
                        Edate.setText(documentSnapshot.get("heureDateFinPrevu").toString());
                        EVENT_DATE_TIME = documentSnapshot.get("heureDateFinPrevu").toString()+":00";
                       // EVENT_DATE_TIME = documentSnapshot.get("heureDateFinPrevu").toString()+":00";
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

                    // Inside RetriveData method
                    if (documentSnapshot.get("fileUrls") != null) {
                        fileUrls = (List<String>) documentSnapshot.get("fileUrls");
                        // Update your UI with the fileUrls (e.g., display in a ListView)
                        updateFileUrlsListView(fileUrls);
                    }
                } else {
                    Log.d("Firebase", "Current data: null");
                }
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

   /* protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }*/

    private void showOptionsForFileUpload() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TaskDetail.this);
        alertDialogBuilder
                .setTitle("Upload File")
                .setItems(new String[]{"Choose from gallery", "Take a new picture", "Choose a PDF"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            // Choosing file from gallery
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
                                startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY_INTENT_CODE);
                                break;

                            case 1:
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(cameraIntent, CAMERA_INTENT_CODE);
                                }
                                startActivityForResult(cameraIntent, CAMERA_INTENT_CODE);
                                break;

                            // Choosing a PDF file
                            case 2:
                                Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                pdfIntent.setType("application/pdf");
                                startActivityForResult(Intent.createChooser(pdfIntent, "Select PDF"), PDF_INTENT_CODE);
                                break;
                        }
                    }
                });

        // Set a custom background drawable
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_INTENT_CODE:
                    // Image received from gallery
                    Uri galleryUri = data.getData();
                  //  Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryUri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    uploadFile(galleryUri, "image", bitmap);
                    break;

                case CAMERA_INTENT_CODE:
                    // Image received from camera
                    // The image is usually stored in a file; you need to use the file path to create a Uri
                    Uri cameraUri = data.getData();
                    Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");
                    uploadFile(cameraUri, "image", bitmap1);
                    break;

                case PDF_INTENT_CODE:
                    // PDF file
                    Uri pdfUri = data.getData();

                    uploadFile(pdfUri, "pdf", null);
                    break;
            }
        }
    }

    private void uploadFile(Uri fileUri, String fileType, Bitmap bitmap) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        // Define the storage reference path based on file type
        String storagePath = "/joints/" + id+"_"+FirebaseAuth.getInstance().getCurrentUser().getEmail() + "_" + currentDateTime;
        if ("pdf".equals(fileType)) {
            storagePath += ".pdf";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(storagePath);
            storageReference.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Getting the URL of the uploaded file
                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> uriTask) {
                                if (uriTask.isSuccessful()) {
                                    String fileUrl = uriTask.getResult().toString();

                                    // Update the database with the file URL
                                    //documentReference.update("fileUrl", fileUrl);
                                    // Inside uploadFile method
                                    taskReference.update("fileUrls", FieldValue.arrayUnion(fileUrl));

                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed to get file URL", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to upload file", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if ("image".equals(fileType)) {
            storagePath += ".jpg";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(storagePath);
            // Converting image bitmap to byte array for uploading to firebase storage

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] taskImageJoint = baos.toByteArray();
            storageReference.putBytes(taskImageJoint).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Getting url of the image uploaded to firebase storage
                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {

                                    String fileUrl = task.getResult().toString();

                                    // Update the database with the file URL
                                    //documentReference.update("fileUrl", fileUrl);
                                    // Inside uploadFile method
                                    taskReference.update("fileUrls", FieldValue.arrayUnion(fileUrl));

                                    progressDialog.dismiss();

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(TaskDetail.this, "Failed to update Joint Image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(TaskDetail.this, "Failed to update Joint Image"+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



        showProgressDialog();

        // Uploading the file to Firebase Storage

    }

   /* private void updateFileUrlsListView(List<String> fileUrls) {
        // Assuming you have a ListView named 'fileUrlsListView'
        ListView fileUrlsListView = findViewById(R.id.fileUrlsListView);

        // Use an ArrayAdapter to display the file URLs in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_file, fileUrls);
        fileUrlsListView.setAdapter(adapter);
    }*/
   private void updateFileUrlsListView(List<String> fileUrls) {
   // Assuming you have a ListView named 'fileUrlsListView'
   ListView fileUrlsListView = findViewById(R.id.fileUrlsListView);
       fileUrlsListView.setOnTouchListener(new View.OnTouchListener() {


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
               fileUrlsListView.onTouchEvent(event);
               return true;
           }
       });

    // Use a custom ArrayAdapter with the custom layout
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_file, R.id.textFileName, fileUrls) {
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Inflate the custom layout
            View view = super.getView(position, convertView, parent);

            // Get the file URL at the current position
            String fileUrl = getItem(position);

            // Set the file name in the TextView
            TextView textFileName = view.findViewById(R.id.textFileName);
            textFileName.setText(position+". "+getFileNameFromUrl(fileUrl));

            return view;
        }
    };

    fileUrlsListView.setAdapter(adapter);
       // Use an ArrayAdapter to display the file URLs in the ListView
       /*ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileUrls);
       fileUrlsListView.setAdapter(adapter);*/

       // Set a click listener for each item in the ListView
       fileUrlsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Open an AlertDialog or perform any other action when an item is clicked
               showFileOptionsDialog(fileUrls.get(position));
           }
       });
}
/*    private void showFileOptionsDialog(String fileUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("File Options")
                .setMessage("Choose an action for the file.")
                .setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Implement code to view the file, e.g., show a fragment
                        // You can call a method to display the file using a fragment
                        // displayFileFragment(fileUrl);
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call the deleteFile method
                        deleteFileUrl(fileUrl);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel the dialog
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }*/
private void showFileOptionsDialog(String fileUrl) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
    builder.setView(dialogView);

    TextView title = dialogView.findViewById(R.id.dialogTitle);
    TextView message = dialogView.findViewById(R.id.dialogMessage);
    Button btnView = dialogView.findViewById(R.id.btnView);
    Button btnDelete = dialogView.findViewById(R.id.btnDelete);
    Button btnCancel = dialogView.findViewById(R.id.btnCancel);

    title.setText("File Options");
    message.setText("Choose an action for the file.");

    // Set icon and click listener for the View button
    btnView.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_view, 0, 0, 0);
    btnView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Implement code to view the file, e.g., show a fragment
            // You can call a method to display the file using a fragment
            // displayFileFragment(fileUrl);
            downloadFile(fileUrl);
            dialog.dismiss();
        }
    });

    // Set icon and click listener for the Delete button
    btnDelete.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete, 0, 0, 0);
    btnDelete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Call the deleteFile method
            deleteFileUrl(fileUrl);
            dialog.dismiss();
        }
    });

    // Click listener for the Cancel button
    btnCancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Cancel the dialog
            dialog.dismiss();
        }
    });

    dialog = builder.create();
    dialog.show();
}

    // Helper method to extract the file name from the URL
    private String getFileNameFromUrl(String fileUrl) {
        // Extract file name from URL
        String fileName = Uri.parse(fileUrl).getLastPathSegment();

        // Check for "pdf" and "jpg" extensions
        if (fileName.toLowerCase().contains(".pdf")) {
            return "PDF File";
        } else if (fileName.toLowerCase().contains(".jpg")) {
            return "Image File";
        }

        // Return original file name if no specific extension found
        return fileName;
    }
    public void deleteFileUrl(String fileUrl) {
        // Use the StorageReference to delete the file from Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        // Get the document reference
                        DocumentReference documentRef = firebaseFirestore.collection("tasksCollection").document(id);

                        // Update the document
                        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        // Get the list of file URLs
                                        List<String> fileUrlsInFb = (List<String>)documentSnapshot.get("fileUrls");

                                        if (fileUrlsInFb == null) {
                                            fileUrlsInFb = new ArrayList<>();
                                        }

                                        // Remove the URL from the list
                                        fileUrlsInFb.remove(fileUrl);

                                        // Update the document
                                        documentRef.update("fileUrls", fileUrlsInFb);

                                        // Now remove the file URL from the list and update the ListView
                                        fileUrls.remove(fileUrl);
                                        updateFileUrlsListView(fileUrls);

                                        Toast.makeText(TaskDetail.this, "File deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to get document
                                        Toast.makeText(TaskDetail.this, "Failed to get document for updating file URLs", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete the file
                        Toast.makeText(TaskDetail.this, "Failed to delete the file", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    public void test(View view) {
        Toast.makeText(TaskDetail.this,"hello",Toast.LENGTH_LONG).show();
    }
    private void downloadFile(String fileUrl) {
        // Create a DownloadManager instance
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        // Parse the file URL
        Uri uri = Uri.parse(fileUrl);

        // Create a DownloadManager.Request with the file URL
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // Set the destination directory and file name for the downloaded file
        String fileName = "delivery "+ UUID.randomUUID().toString();; // Replace with the desired file name
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Enqueue the download request
        long downloadId = downloadManager.enqueue(request);

        // Display a toast indicating that the download has started
        Toast.makeText(this, "Downloading file...", Toast.LENGTH_SHORT).show();
    }
}

