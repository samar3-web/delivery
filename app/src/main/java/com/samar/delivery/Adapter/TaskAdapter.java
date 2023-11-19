package com.samar.delivery.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samar.delivery.R;
import com.samar.delivery.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();

    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tache_cardview, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.textViewTitle.setText(task.getLibelle());
        holder.textViewdateDebutReelle.setText(task.getHeureDateDebutReelle());
        holder.textViewdateFinReelle.setText(task.getHeureDateFinReelle());

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewdateDebutReelle;
        TextView textViewdateFinReelle;

        TaskViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_item);
            textViewdateDebutReelle = itemView.findViewById(R.id.dateDebutReele);
            textViewdateFinReelle = itemView.findViewById(R.id.dateFinReele);
        }
    }
}
