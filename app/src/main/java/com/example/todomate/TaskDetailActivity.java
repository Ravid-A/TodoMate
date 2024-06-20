package com.example.todomate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.todomate.Fragments.EditTaskFragment;
import com.example.todomate.Fragments.ReminderFragment;
import com.example.todomate.databinding.ActivityTaskDetailBinding;
import com.example.todomate.model.TodoTask;
import com.example.todomate.model.TodoTaskData;
import com.example.todomate.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity implements EditTaskFragment.OnFragmentInteractionListener {

    private ActivityTaskDetailBinding binding;

    TodoTaskData taskData;

    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("TodoMate - Task Detail");
        setSupportActionBar(binding.toolbar);

        taskId = getIntent().getStringExtra("taskId");

        // Update UI with task data
        updateUI();

        binding.buttonEdit.setOnClickListener(v -> loadEditTaskFragment());
        binding.buttonSetReminder.setOnClickListener(v -> loadSetReminderFragment());
    }

    private void updateUI() {
        TaskViewModel taskViewModel = new TaskViewModel();

        // Fetch task data asynchronously
        taskViewModel.getTaskData(taskId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                TodoTask todoTask = task.getResult().toObject(TodoTask.class);
                taskData = null;
                if (todoTask != null) {
                    taskData = new TodoTaskData(taskId, todoTask);

                    binding.taskTitleTextView.setText(taskData.getTitle());
                    binding.taskDescriptionTextView.setText(taskData.getDescription());
                    binding.taskDueDateTextView.setText(getFormattedDate(taskData.getDueDate()));
                }
            }
        });
    }

    private String getFormattedDate(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return String.format("Due on %s", sdf.format(calendar.getTime()));
    }

    private void loadEditTaskFragment() {
        // Create an instance of EditTaskFragment
        EditTaskFragment editTaskFragment = new EditTaskFragment();

        // Pass the task ID to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("taskId", taskId);
        editTaskFragment.setArguments(bundle);

        // Begin a fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace the fragment_container with the new fragment
        transaction.replace(R.id.fragmentContainerView, editTaskFragment);

        // Add the transaction to the back stack
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private void loadSetReminderFragment() {
        // Create an instance of SetReminderFragment
        ReminderFragment setReminderFragment = new ReminderFragment();

        // Pass the due date to the fragment
        Bundle bundle = new Bundle();
        bundle.putLong("dueDate", taskData.getDueDate());
        setReminderFragment.setArguments(bundle);

        // Begin a fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace the fragment_container with the new fragment
        transaction.replace(R.id.fragmentContainerView, setReminderFragment);

        // Add the transaction to the back stack
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        // Handle back press to close the fragment if it is open
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            updateUI();
        } else {
            super.onBackPressed();
        }
    }

    public void onFragmentClosed() {
        // Handle fragment closed event here
        // For example, refresh data or update UI
        updateUI(); // Example method to update UI after fragment is closed
    }
}
