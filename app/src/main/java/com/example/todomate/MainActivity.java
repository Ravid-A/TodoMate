package com.example.todomate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.core.app.ActivityCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todomate.adapter.TaskAdapter;
import com.example.todomate.databinding.ActivityMainBinding;
import com.example.todomate.viewmodel.TaskViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;

    private FirebaseAuth mAuth;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        mAuth = FirebaseAuth.getInstance();

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        setupRecyclerView();

        binding.fabAddTask.setOnClickListener(v -> showAddTaskActivity());
        binding.fabRefresh.setOnClickListener(v -> onResume());

        // ask for POST_NOTIFICATIONS permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTasks();
        updateUI(mAuth.getCurrentUser());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            // Handle profile action
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_sign_out) {
            // Handle sign out action
            mAuth.signOut();
            updateUI(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        binding.tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter();
        binding.tasksRecyclerView.setAdapter(taskAdapter);
    }

    public void updateTasks() {
        taskViewModel.getTasks().observe(this, tasks -> {
            if (tasks != null) {
                taskAdapter.setTasks(tasks);
            }
        });
    }

    private void showAddTaskActivity() {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
