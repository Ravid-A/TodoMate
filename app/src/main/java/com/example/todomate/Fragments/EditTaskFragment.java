package com.example.todomate.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.todomate.R;
import com.example.todomate.model.TodoTask;
import com.example.todomate.model.TodoTaskData;
import com.example.todomate.viewmodel.TaskViewModel;

import java.util.Calendar;

public class EditTaskFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private TaskViewModel taskViewModel;
    private EditText editTextTitle;
    private EditText editTextDescription;

    private TodoTaskData taskData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_task, container, false);

        taskViewModel = new TaskViewModel();

        // Initialize views
        editTextTitle = rootView.findViewById(R.id.editTextTitle);
        editTextDescription = rootView.findViewById(R.id.editTextDescription);
        Button buttonUpdateTask = rootView.findViewById(R.id.buttonUpdateTask);

        // Set click listener for update button
        buttonUpdateTask.setOnClickListener(v -> updateTask());

        // get extra
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String taskId = bundle.getString("taskId");

            taskViewModel.getTaskData(taskId).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    System.out.println("Task data: " + task.getResult().getData());

                    TodoTask todoTask = task.getResult().toObject(TodoTask.class);
                    taskData = null;
                    if (todoTask != null) {
                        taskData = new TodoTaskData(taskId, todoTask);
                    }
                }
            });
        }

        return rootView;
    }

    private void updateTask() {
        // Retrieve updated task details from EditText fields
        String updatedTitle = editTextTitle.getText().toString().trim();
        String updatedDescription = editTextDescription.getText().toString().trim();

        // Check if any changes were made
        if (updatedTitle.equals(taskData.getTitle()) && updatedDescription.equals(taskData.getDescription())) {
            Toast.makeText(requireContext(), "No changes made", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update task details if changes were made
        if(!updatedTitle.equals(taskData.getTitle()) && !updatedTitle.isEmpty()) {
            taskData.setTitle(updatedTitle);
        }

        if(!updatedDescription.equals(taskData.getDescription()) && !updatedDescription.isEmpty()) {
            taskData.setDescription(updatedDescription);
        }

        // Update the task in Firestore
        taskViewModel.updateTask(taskData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();

                closeFragment();
                // Close the fragment
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                if(task.getException() != null) {
                    Toast.makeText(requireContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void closeFragment() {
        // Perform any necessary tasks before closing the fragment
        mListener.onFragmentClosed(); // Notify activity that fragment is closing
    }

    public interface OnFragmentInteractionListener {
        void onFragmentClosed();
    }
}
