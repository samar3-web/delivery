package com.samar.delivery.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.samar.delivery.R;
import com.samar.delivery.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    Context context;
    int customlayout_id;
    private List<Task> taskList;

    List<Task> listFull;
    String user;
    FirebaseDatabase firebaseDB;

    public TaskAdapter(Context context, List taskList, int customlayout_id){
        this.context= context;
        this.taskList = taskList;
        this.customlayout_id = customlayout_id;
        listFull = new ArrayList<>(taskList);
        this.user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    }
    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();

    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(customlayout_id, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.textViewTitle.setText(task.getLibelle());
        holder.textViewdateDebutReelle.setText(task.getHeureDateDebutReelle());
        holder.textViewdateFinReelle.setText(task.getHeureDateFinReelle());
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewdateDebutReelle, textViewdateFinReelle;


        public ViewHolder(View view) {
            super(view);
            //Getting all the views
            textViewTitle = view.findViewById(R.id.text_view_item);
            textViewdateDebutReelle = view.findViewById(R.id.dateDebutReele);
            textViewdateFinReelle = view.findViewById(R.id.dateFinReele);


        }


    }

}
