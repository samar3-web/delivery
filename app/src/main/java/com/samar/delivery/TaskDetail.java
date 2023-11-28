package com.samar.delivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
    TextView name,consis,left,goal_lft_pert, notes;
    TextView Tdays, Dleft, Sdate, Edate;
    RelativeLayout rel;
    String currentUserID;
    String description;
    long Days;
    String goal_end, goal_create;
    MCalendarView mCalendarView;
    ArrayList<DateData> dataArrayList;
    private StorageReference UserProfileImagesRef;
    ProgressDialog progressDialog;
    DatabaseReference RootRef,HelloREf,newRef,notesRef;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
    SimpleDateFormat justDateFormat = new SimpleDateFormat("dd/M/yyyy");
    private Handler handler = new Handler();
    private Runnable runnable;
    ImageView extendedFloatingShareButton;
    ImageView extendedFloatingEditButton;
    ImageView deleteGoal, NewNote, resetGoal;
    ImageButton add_img;
    ImageView  Alarm;
    CircleImageView goalPic;

    ImageView shareStreak;
    String GoalName;
    String id;

    private WebView signatureWebView;
    private static final int PICK_FILE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
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









        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowOptionsforProfilePic();
            }
        });

        //checkBreak();




    }





    private void InitializationMethod() {

        Intent intent = getIntent();
        id = intent.getStringExtra("LISTKEY");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
       RootRef = FirebaseDatabase.getInstance().getReference("tasksCollection");
        //recyclerView = findViewById(R.id.streaknotes);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        name = findViewById(R.id.desc_goal_name);

        Alarm = findViewById(R.id.alarm);

        extendedFloatingEditButton = findViewById(R.id.edit_goal_btn);
        consis = findViewById(R.id.desc_goal_const);
        left = findViewById(R.id.desc_goal_left);
        mCalendarView= findViewById(R.id.history_calendarView);
        goal_lft_pert = findViewById(R.id.desc_goal_leftper);

        add_img = findViewById(R.id.add_img);
        goalPic = findViewById(R.id.imageIcon);


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
                        goalPic.setImageBitmap(bitmap);
                        uploadGoalPic(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                // Image received from camera
                case CAMERA_INTENT_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    goalPic.setImageBitmap(bitmap);
                    uploadGoalPic(bitmap);
                    break;
            }
        }
    }

    private void uploadGoalPic(Bitmap bitmap) {


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
                                RootRef.child(id).child("goal_image").setValue(pfpUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Goal picture updated", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to upload goal picture", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed to upload goal picture", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to upload goal picture", Toast.LENGTH_SHORT).show();
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
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("qqqqqqqqqqqqqqqqqq", "Task name : " + documentSnapshot.getId());
                Log.d("qqqqqqqqqqqqqqqqqq", "Task name : " + documentSnapshot.get("name").toString());
                if (documentSnapshot.get("name") != null)
                    name.setText(documentSnapshot.get("name").toString());

                if (documentSnapshot.get("heureFinReelle") != null)
                    left.setText(documentSnapshot.get("heureFinReelle").toString());

                if (documentSnapshot.get("heureFinReelle") != null)
                    Edate.setText(documentSnapshot.get("heureFinReelle").toString());
                if (documentSnapshot.get("heureDebutReelle") != null)
                    Sdate.setText(documentSnapshot.get("heureDebutReelle").toString());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failures
                Log.e("Firebase", "Error getting document", e);
            }
        });

    }



    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }



    public void AlarmAct(View view) {
        Intent i = new Intent(getApplicationContext(), AlarmActivity.class); //Pass to AlarmActivity Class
        i.putExtra("GoalName", GoalName); //Passing Goal Name
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




}