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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
    ImageView shareNotes;
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
    ImageView shareCal, Alarm;
    CircleImageView goalPic;
    private String EVENT_DATE_TIME = "null";
    private String DATE_FORMAT = "dd/M/yyyy hh:mm:ss";
    private String JUSTDATE_FORMAT = "dd/M/yyyy";
    ImageView shareStreak;
    String GoalName;
    String id;
    public static final String ADD_TRIP_VALUE= TaskDetail.class.getName();
    public static final String ADD_TRIP_TAG="ADD_TRIP_TAG";
    public static final String ADD_TRIP_DATA_KEY="ADD_TRIP_DATA_KEY";
    AppCompatButton Leave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        InitializationMethod();
        clearCalendar();
        highLightDate();
        //RetriveData();
        id = getIntent().getStringExtra("currentTaskid");


        RetriveData(id);



        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowOptionsforProfilePic();
            }
        });

        //checkBreak();

        Leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStatus();
            }
        });



    }

    private void checkStatus() {
        RootRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.child("Status").getValue().toString();
                if(status.equals("OnBreak")) {
                    String e = snapshot.child("EndTime").getValue().toString();
                    String s = snapshot.child("BreakEndDate").getValue().toString();
                    cancelBreak(s, e);
                } else {
                    getLeaveDays();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cancelBreak(String s, String e1) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date n = new Date();
        String n1 = dateFormat.format(n);
        try {
            Date bEnd = dateFormat.parse(s);
            Date td = dateFormat.parse(n1);
            Date GoalEnd = dateFormat.parse(e1);

            long diff = bEnd.getTime() - td.getTime();
            long Days = diff / (24 * 60 * 60 * 1000);

            if(Days > 0) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(GoalEnd);// this would default to now
                calendar.add(Calendar.DAY_OF_MONTH, -1 * (int)Days);
                Date x = calendar.getTime();
                String x1 = dateFormat.format(x);

                DatabaseReference db = RootRef.child(id);
                db.child("Status").setValue("Active");
                db.child("EndTime").setValue(x1);
                db.child("BreakEndDate").removeValue();
                Intent i = new Intent(TaskDetail.this, HomeActivity.class);
                startActivity(i);
                finish();
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private void InitializationMethod() {

        Intent intent = getIntent();
        id = intent.getStringExtra("LISTKEY");

        //UserProfileImagesRef = FirebaseStorage.getInstance ().getReference ().child ( "Goal Images" );
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        //RootRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active");
       RootRef = FirebaseDatabase.getInstance().getReference("tasksCollection");
        //HelloREf = FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active").child(id).child("Win");

        //notesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Goals").child("Active").child(id).child("Notes");
        recyclerView = findViewById(R.id.streaknotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //newRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
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

        //Notes

        Leave = findViewById(R.id.LeaveButton);


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

    private Bitmap screenShot(View view) {
        View screenView = view;
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void share(Bitmap bitmap){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap , "IMG_" + Calendar.getInstance().getTime(), null);


        if (!TextUtils.isEmpty(pathofBmp)){
            Uri uri = Uri.parse(pathofBmp);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tracky : track your Goal");
            //Retrieve value of completed goal using shared preferences from RetreiveData() function
            String goal_cmpltd = PreferenceManager.getDefaultSharedPreferences(TaskDetail.this).getString("goal_completed","");
            //Retreive value of consistency using shared preferences from RetreiveData() function
            String goal_consistency = PreferenceManager.getDefaultSharedPreferences(TaskDetail.this).getString("consistency","");
            //Retreive goal name using shared preferences from RetreiveData() function
            String goal_name = PreferenceManager.getDefaultSharedPreferences(TaskDetail.this).getString("goal_name","");
            //Retreive name using Shared preference from Retrieve data function
            String user_name = PreferenceManager.getDefaultSharedPreferences(TaskDetail.this).getString("name","");


        }

    }

    private void getLeaveDays() {
        new LovelyTextInputDialog(this, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.blue)
                .setTitle("Take Break from Current Goal")
                .setMessage("How many days of break you need?")
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setIcon(R.drawable.ic_baseline_edit_24)
                .setInputFilter("Wrong Input, please try again!", new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return text.matches("\\w+");
                    }
                })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        String myText = text; //Saving Entered name in String
                        int Days = Integer.parseInt(myText);
                        if(myText.isEmpty())
                            Toast.makeText(TaskDetail.this, "Please input number of Days", Toast.LENGTH_SHORT).show();
                        else
                            askLeave(Days);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void askLeave(int days) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date endDate = dateFormat.parse(goal_end);
            Date today = new Date();
            String nDate = dateFormat.format(today);
            Date updatedToday = dateFormat.parse(nDate);
            long diff = endDate.getTime() - updatedToday.getTime();
            long Days = diff / (24 * 60 * 60 * 1000);
            int d = (int) Days - days;
            if(d < 1)
                Toast.makeText(this, "Please select less days" , Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "Leave Granted!", Toast.LENGTH_SHORT).show();
                updateGoal(days);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateGoal(long days) {

        Toast.makeText(this, ""+days, Toast.LENGTH_SHORT).show();

        int intt = (int) days;

        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

        String goal_created_date = goal_create;

        Date create_date = null;
        try {
            create_date = simpleDateFormat2.parse(goal_created_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Date today = new Date();

        Date today_date_after_increse = new Date(today.getTime() + (1000 * 60 * 60 * 24 * intt));
        Date create_date_after_increse = new Date(create_date.getTime() + (1000 * 60 * 60 * 24 * intt));


        String today_date_after_increse_string = simpleDateFormat2.format(today_date_after_increse);
        String create_date_after_increse_string = simpleDateFormat2.format(create_date_after_increse);

        HashMap<String,Object> onlineStat = new HashMap<> (  );
        onlineStat.put ( "TodayTime", create_date_after_increse_string);
        onlineStat.put ("Status", "OnBreak");
        onlineStat.put ("BreakEndDate", today_date_after_increse_string);

        RootRef.child(id)
                .updateChildren ( onlineStat );

        Intent i = new Intent(TaskDetail.this, HomeActivity.class);
        startActivity(i);
        finish();
    }



    /*public void Graph() {

        RootRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fetch = snapshot.child("Data").child("Seven").getValue().toString();
                String[] sp = fetch.split(":");


                ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);


                ValueLineSeries series = new ValueLineSeries();

                int x = Integer.parseInt(sp[6]);

                if(x>0 && x<=33) { series.setColor(getResources().getColor(R.color.orange)); }
                else if(x>33 && x<=66) { series.setColor(getResources().getColor(R.color.green)); }
                else { series.setColor(0xFF56B7F1); }

                series.addPoint(new ValueLinePoint("null", Integer.parseInt(sp[0])));
                series.addPoint(new ValueLinePoint("7th", Integer.parseInt(sp[0])));
                series.addPoint(new ValueLinePoint("6th", Integer.parseInt(sp[1])));
                series.addPoint(new ValueLinePoint("5th", Integer.parseInt(sp[2])));
                series.addPoint(new ValueLinePoint("4th", Integer.parseInt(sp[3])));
                series.addPoint(new ValueLinePoint("3rd", Integer.parseInt(sp[4])));
                series.addPoint(new ValueLinePoint("2nd", Integer.parseInt(sp[5])));
                series.addPoint(new ValueLinePoint("Today", Integer.parseInt(sp[6])));
                series.addPoint(new ValueLinePoint("null", Integer.parseInt(sp[6])));

                mCubicValueLineChart.addSeries(series);
                mCubicValueLineChart.startAnimation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
*/
    private void RetriveData(String houseDocId) {


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("tasksCollection").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                Log.d("xxxxxdocs", "onComplete: of task data fetching " + task.getResult().getDocuments());
                tasks = new ArrayList<com.samar.delivery.models.Task>();

                for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                    List<DocumentSnapshot> snapshot = task.getResult().getDocuments();
                    try {


                        if (doc.get("name") != null)
                            name.setText(doc.get("name").toString());

                        /*if (snapshot.get("heureFinReelle") != null)
                            goal_end.setText(snapshot.get("heureFinReelle").toString());

                        if (snapshot.get("city") != null)
                            city.setText(snapshot.get("city").toString());
                        if (snapshot.get("location") != null)
                            location.setText(snapshot.get("location").toString());

                        if (snapshot.get("street") != null)
                            street.setText(snapshot.get("street").toString());

                        if (snapshot.get("size") != null)
                            size.setText(snapshot.get("size").toString());

                        if (snapshot.get("houseNo") != null)
                            houseNo.setText(snapshot.get("houseNo").toString());
*/


                    } catch (Exception e) {
                        Log.d("xxxxxxx", "onComplete Exception in setting data to house : " + e.getLocalizedMessage());
                    }

                }
            }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(Exception e) {
            Log.d("xxxxx", "onFailure: of HouseData fectching " + e.getLocalizedMessage());
        }
    });
    }

    public static int GoalCOmpleteFn(String todaay, String goal_create, String goal_end) {

        float gh = (DayReturn(todaay,goal_create)+1)*100/(DayReturn(goal_end,goal_create)+1);
        return Math.round(gh);
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
                    Date created = dateFormat.parse(goal_create);
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
                        Sdate.setText(goal_create.substring(0,10).trim());
                        Edate.setText(goal_end.substring(0,10).trim());
                        notes.setText(description);
                        left.setText(String.format("%02d",Days)+" days  "+String.format("%02d", Hours)+":"+String.format("%02d", Minutes)+":"+String.format("%02d", Seconds));
                        if(percent<=33) {
                            left.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                            rel.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.lightred));
                        }
                        else if(percent<=66)
                        {
                            left.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.yellow));
                            rel.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.lightyellow));
                        }
                        else
                        {
                            left.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                            rel.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.lightgreen));
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

    public static long DayReturn(String high,String low){

        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/M/yyyy");
        Date date1=null,date2 = null;
        try {
            date2 = simpleDateFormat2.parse(low);
            date1 = simpleDateFormat2.parse(high);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        long different = date1.getTime() - date2.getTime();


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        return elapsedDays;
    }


    public String ConsistentFn(int node,String today_date,String create_date){


        float fl  = (float)(node*100)/(DayReturn(today_date,create_date)+1);
        int iu = Math.round(fl);
        return String.valueOf(iu);

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
        progressDialog.dismiss();
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