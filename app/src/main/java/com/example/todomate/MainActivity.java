package com.example.todomate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todomate.adapter.TaskAdapter;
import com.example.todomate.databinding.ActivityMainBinding;
import com.example.todomate.viewmodel.TaskViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.tasksRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);

        taskViewModel.getTasks().observe(this, tasks -> taskAdapter.setTasks(tasks));
    }
}