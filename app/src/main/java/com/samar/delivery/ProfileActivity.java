package com.samar.delivery;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.samar.delivery.models.Users;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;


import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    TextView welcome1, welcome2;
    private DatabaseReference reference, tillActive,Rootref;
    ProgressDialog progressDialog;
    private String userID;
    AppCompatButton Logout;
    ImageButton profilePicButton;
    ImageView editname;
    CircleImageView profilePic;
    private final int GALLERY_INTENT_CODE = 993;
    private final int CAMERA_INTENT_CODE = 990;
    private StorageReference UserProfileImagesRef;
    final private int REQUEST_CODE_PERMISSION = 111;
    ArrayList<String> GoalName;
    String fileName;
    File pdf;
    private String currentUserEmail;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    //private LovelySaveStateHandler saveStateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Window window = getWindow();
        window.setNavigationBarColor(getColor(R.color.blue));


        InitializationMethod();
        getUserDatafromFirebase();
       // Graph();

        editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditName();
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogOutMethod();

            }
        });

        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowOptionsforProfilePic();

            }
        });
    }


    private void InitializationMethod() {

        welcome1 = findViewById(R.id.users_name);
        welcome2 = findViewById(R.id.users_email);
        Logout = findViewById(R.id.logout);
        editname = findViewById(R.id.editName);

        UserProfileImagesRef = FirebaseStorage.getInstance ().getReference ().child ( "USERS_IMAGES" );

        // Button for adding profile pic
        profilePicButton = (ImageButton) findViewById(R.id.profile_pic_button);
        profilePic = (CircleImageView) findViewById(R.id.profile_pic);


       // saveStateHandler = new LovelySaveStateHandler();


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Log.d("xxxxxxxxxxx : userID","userID : "+userID);

        Rootref = FirebaseDatabase.getInstance ().getReference ().child("USERDATA").child(userID);
        Log.d("aaaaaaaaaaaaaaaa : Rootref","Rootref : "+Rootref);
        reference = FirebaseDatabase.getInstance().getReference("USERDATA");

        firebaseFirestore = FirebaseFirestore.getInstance();

        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        documentReference = firebaseFirestore.collection("USERDATA").document(currentUserEmail);

     //   tillActive = FirebaseDatabase.getInstance ().getReference ().child("Users").child(userID).child("Goals").child("Active");
    }

    private void getUserDatafromFirebase() {
        showProgressDialog();


        firebaseFirestore.collection("USERDATA").document(currentUserEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    Log.d("xxxxxx", "onComplete: getData()" + task.getResult().getId());
                    Log.d("xxxxxx", "onComplete: getData()" + task.getResult().getData());
                    Map<String, Object> snapshot = task.getResult().getData();
                    try {
                        if (snapshot.get("name") != null) {
                            welcome1.setText(snapshot.get("name").toString());
                        }
                        welcome2.setText(snapshot.get("email").toString());
                        // Getting the url of profile picture

                        if (snapshot.get("profileUrl").toString() != "") {
                            // If the url is not null, then adding the image
                            Picasso.get().load(snapshot.get("profileUrl").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).into(profilePic);
                        }

                       progressDialog.dismiss();


                    } catch (Exception e) {
                        Log.d("xxxxxxx", "onComplete Exception in setting data to profile : " + e.getLocalizedMessage());
                    }

                    /*Users userprofile = snapshot.getValue(Users.class);

                    if (userprofile != null) {
                        String fullname = userprofile.name;
                        String email = userprofile.email;

                        welcome1.setText(fullname);
                        welcome2.setText(email);*/

                    }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d("xxxx", "onFailure: " + e.getLocalizedMessage());
            }
        });
        /*reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userprofile = snapshot.getValue(Users.class);

                if (userprofile != null) {
                    String fullname = userprofile.name;
                    String email = userprofile.email;

                    welcome1.setText(fullname);
                    welcome2.setText(email);

                }

                // Getting the url of profile picture
                Object pfpUrl = snapshot.child("profileUrl").getValue();
                if (pfpUrl != null) {
                    // If the url is not null, then adding the image
                    Picasso.get().load(pfpUrl.toString()).placeholder(R.drawable.profile).error(R.drawable.profile).into(profilePic);
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Cannot fetch data", Toast.LENGTH_SHORT).show();
            }
        });*/
    }


    // Showing options for getting image to set as profile picture
    private void ShowOptionsforProfilePic() {

        new MaterialAlertDialogBuilder(ProfileActivity.this).setBackground(getResources().getDrawable(R.drawable.material_dialog_box)).setTitle("Change profile photo").setItems(new String[]{"Choose from gallery", "Take a new picture"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
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
                            startActivityForResult(cameraIntent, CAMERA_INTENT_CODE);
                        }
                        startActivityForResult(cameraIntent, CAMERA_INTENT_CODE);
                        break;
                }
            }
        }).show();

    }

    // Function for logout method
    private void LogOutMethod() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this,R.style.AlertDialogTheme);
        builder.setTitle("Logout");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage("Are you sure you want to logout?");
        builder.setBackground(getResources().getDrawable(R.drawable.material_dialog_box , null));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent loginIntenttt = new Intent(ProfileActivity.this, MainActivity.class);
                loginIntenttt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntenttt);
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.no, null);

        builder.show();


    }

    // Function for progress dialoge bar
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_diaglog);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = (Uri) data.getData();
            switch (requestCode) {
                // Image received from gallery
                case GALLERY_INTENT_CODE:
                    try {
                        // Converting the image uri to bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        profilePic.setImageBitmap(bitmap);
                        updateProfilePic(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                // Image received from camera
                case CAMERA_INTENT_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profilePic.setImageBitmap(bitmap);
                    updateProfilePic(bitmap);
                    break;
            }
        }
    }

    // Function for updating profile picture
    private void updateProfilePic(Bitmap bitmap) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());
      //  currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        StorageReference storageReference = UserProfileImagesRef.child ( FirebaseAuth.getInstance().getCurrentUser().getEmail() + "_" + currentDateTime + ".jpg");

        showProgressDialog();

        // Converting image bitmap to byte array for uploading to firebase storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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

                                String pfpUrl = task.getResult().toString();

                                documentReference.update("profileUrl", pfpUrl);
                                progressDialog.dismiss();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Failed to update profile picture"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void EditName() {
        new LovelyTextInputDialog(this, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.blue)
                .setTitle("Enter your Name")
                .setMessage("This will update your current Name")
                .setIcon(R.drawable.ic_baseline_edit_24)
                .setInputFilter("Wrong Input, please try again!", new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {

                        //return text.matches("\\w+");
                        return text.matches("^[a-zA-z0-9_]+( [a-zA-z0-9_]+)*$");
                    }
                })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {




                        documentReference.update("name", text);
                        welcome1.setText(text);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


    }

    /*public void PDF(View view) {
        GoalName = new ArrayList<>(); //Initialize
        tillActive.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GoalName.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) { //get all Goal IDs
                    GoingCLass going = snapshot1.getValue(GoingCLass.class);
                    String s = going.getGoalName(); //Get data of Goal Name from that ID
                    GoalName.add(s); //add in arraylist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Naming(); //Dialog EditText
    }*/

/*    private void Naming() {

        new LovelyTextInputDialog(this, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.blue)
                .setTitle("Save PDF")
                .setMessage("Enter a name for your PDF.")
                .setIcon(R.drawable.ic_baseline_edit_24)
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        fileName = text; //Saving Entered name in String
                        createPDF();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }*/

    /*private void createPDF() {
        int hasWritePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    new AlertDialog.Builder(this)
                            .setMessage("Access Storage Permission")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
            return;
        } else {
            File docPath = new File(Environment.getExternalStorageDirectory() + "/Documents");
            if (!docPath.exists()) {
                docPath.mkdir();
            }
            pdf = new File(docPath.getAbsolutePath(), fileName + ".pdf");
            try {
                OutputStream stream = new FileOutputStream(pdf);

                Document document = new Document();
                PdfWriter.getInstance(document, stream);
                document.open();
                document.add(new Paragraph("Your Active Goals are: \n"));


                for(String s : GoalName) {
                    document.add(new Paragraph(s));
                }
                document.close();
                Snackbar snacbar = Snackbar.make(findViewById(android.R.id.content), fileName + " Saved: " + pdf.toString(), Snackbar.LENGTH_SHORT);
                snacbar.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    /*public void Graph() {

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fetch = snapshot.child("Average").child("String").getValue().toString();
                String[] sp = fetch.split(";");


                ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);


                ValueLineSeries series = new ValueLineSeries();

                int x = Integer.parseInt(sp[6]);

                if(x>0 && x<=25) { series.setColor(getResources().getColor(R.color.red)); }
                else if(x>25 && x<=50) { series.setColor(getResources().getColor(R.color.orange)); }
                else if(x>50 && x<=75) { series.setColor(getResources().getColor(R.color.green)); }
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
    }*/



}