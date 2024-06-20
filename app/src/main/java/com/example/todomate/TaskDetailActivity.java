package com.example.todomate;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.todomate.Fragments.EditTaskFragment;
import com.example.todomate.Fragments.ReminderFragment;
import com.example.todomate.databinding.ActivityTaskDetailBinding;
import com.example.todomate.model.TodoTask;
import com.example.todomate.model.TodoTaskData;
import com.example.todomate.viewmodel.TaskViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public class TaskDetailActivity extends AppCompatActivity implements EditTaskFragment.OnFragmentInteractionListener {

    private ActivityTaskDetailBinding binding;

    TodoTaskData taskData;

    private String taskId;

    private final TaskViewModel taskViewModel;
    private final FirebaseAuth mAuth;

    public TaskDetailActivity() {
        taskViewModel = new TaskViewModel();
        mAuth = FirebaseAuth.getInstance();
    }

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

        binding.buttonEdit.setOnClickListener(v -> loadFragment(((Void) -> {
            loadEditTaskFragment();
            return null;
        })));
        binding.buttonSetReminder.setOnClickListener(v -> loadFragment(((Void) -> {
            loadSetReminderFragment();
            return null;
        })));
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

    private void loadFragment(Function<Void, Void> function) {
        taskViewModel.getTaskData(taskId).addOnCompleteListener(taskData -> {
            if (taskData.isSuccessful()) {
                TodoTask todoTask = taskData.getResult().toObject(TodoTask.class);

                if (todoTask == null) {
                    Log.d("TaskDetailActivity", "Task not found");
                    Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                if(mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().getUid().equals(todoTask.getUserId())) {
                    Log.d("TaskDetailActivity", "Task is not yours");
                    Toast.makeText(this, "Task is not yours", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }


                function.apply(null);
            } else{
                Toast.makeText(this, Objects.requireNonNull(taskData.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadEditTaskFragment() {
        // Create an instance of EditTaskFragment
        EditTaskFragment editTaskFragment = new EditTaskFragment();

        // Pass the task ID to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("taskId", taskId);
        editTaskFragment.setArguments(bundle);

        // Clear the back stack
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

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

        // Clear the back stack
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

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
