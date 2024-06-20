package com.example.todomate;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todomate.databinding.ActivityAddTaskBinding;
import com.example.todomate.model.TodoTask;
import com.example.todomate.viewmodel.TaskViewModel;
import com.example.todomate.Fragments.DatePickerFragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddTaskActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener {

    private ActivityAddTaskBinding binding;
    private TaskViewModel taskViewModel;
    private Calendar dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("TodoMate - Add Task");
        setSupportActionBar(binding.toolbar);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.DAY_OF_MONTH, 1);
        onDateSelected(currentDate);

        binding.saveTaskButton.setOnClickListener(v -> saveTask());
        binding.selectDueDateButton.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void saveTask() {
        String title = Objects.requireNonNull(binding.taskTitleEditText.getText()).toString().trim();
        String description = Objects.requireNonNull(binding.taskDescriptionEditText.getText()).toString().trim();

        if (!title.isEmpty() && !description.isEmpty() && dueDate != null) {
            TodoTask task = new TodoTask(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), title, description, dueDate.getTimeInMillis());
            taskViewModel.addTask(task).addOnCompleteListener(_task -> {
                if (_task.isSuccessful()) {
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if(_task.getException() != null) {
                        Toast.makeText(this, _task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Show an error message or handle the empty fields
            Toast.makeText(this, "Please enter a title, description, and select a due date", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDateSelected(Calendar selectedDate) {
        if(!selectedDate.after(Calendar.getInstance())) {
            Toast.makeText(this, "Please select a future date", Toast.LENGTH_SHORT).show();
            return;
        }

        dueDate = selectedDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        binding.taskDueDateEditText.setText(dateFormat.format(dueDate.getTime()));
    }
}