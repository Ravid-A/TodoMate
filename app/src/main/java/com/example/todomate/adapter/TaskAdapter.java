package com.example.todomate.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todomate.TaskDetailActivity;
import com.example.todomate.databinding.ItemTaskBinding;
import com.example.todomate.model.TodoTaskData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<TodoTaskData> tasks;

    public void setTasks(List<TodoTaskData> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TodoTaskData task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private ItemTaskBinding binding;

        public TaskViewHolder(@NonNull ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TodoTaskData task) {
            binding.taskTitleTextView.setText(task.getTitle());
            binding.taskDescriptionTextView.setText(task.getDescription());
            binding.taskDueDateTextView.setText(getFormattedDate(task.getDueDate()));

            //OnClickListener for the item
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), TaskDetailActivity.class);
                intent.putExtra("taskId", task.getId());
                v.getContext().startActivity(intent);
            });
        }

        private String getFormattedDate(long timeInMillis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return String.format("Due on %s", sdf.format(calendar.getTime()));
        }
    }
}
