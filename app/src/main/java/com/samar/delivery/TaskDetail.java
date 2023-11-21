package com.samar.delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TaskDetail extends AppCompatActivity {
    String GoalName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
    }

    public void AlarmAct(View view) {
        Intent i = new Intent(getApplicationContext(), AlarmActivity.class); //Pass to AlarmActivity Class
        i.putExtra("GoalName", GoalName); //Passing Goal Name
        startActivity(i);
    }
}