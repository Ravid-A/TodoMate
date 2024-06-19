package com.example.todomate;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.Openable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.todomate.databinding.ActivityAddTaskBinding;
import com.example.todomate.model.Task;
import com.example.todomate.viewmodel.TaskViewModel;
import com.example.todomate.DatePickerFragment;

import java.util.Calendar;
import java.util.Objects;

public class AddTaskActivity extends AppCompatActivity implements com.example.todomate.DatePickerFragment.OnDateSelectedListener {

    private ActivityAddTaskBinding binding;
    private TaskViewModel taskViewModel;
    private Calendar dueDate;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        binding.selectDueDateButton.setOnClickListener(v -> showDatePicker());
        binding.saveTaskButton.setOnClickListener(v -> saveTask());

        navController = Navigation.findNavController(this, R.id.saveTaskButton);
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    private void showDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void saveTask() {
        String title = Objects.requireNonNull(binding.taskTitleEditText.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.taskDescriptionEditText.getText()).toString().trim();

        if (!title.isEmpty() && !description.isEmpty()) {
            Task task = new Task("0" ,title, description, (dueDate != null ? dueDate.getTimeInMillis() : 0));
            taskViewModel.addTask(task);
            finish(); // Close the activity after saving the task
        } else {
            // Show an error message or handle the empty fields
            Toast.makeText(this, "Please enter a title and description", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSelected(Calendar selectedDate) {
        dueDate = selectedDate;
        // Update the UI to display the selected due date
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (Openable) null) || super.onSupportNavigateUp();
    }
}