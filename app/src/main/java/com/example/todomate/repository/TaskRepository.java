package com.example.todomate.repository;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todomate.model.TodoTask;
import com.example.todomate.model.TodoTaskData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskRepository {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference tasksRef = db.collection("tasks");

    private FirebaseAuth mAuth;

    public LiveData<List<TodoTaskData>> getTasks() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        MutableLiveData<List<TodoTaskData>> tasksLiveData = new MutableLiveData<>();

        if(mAuth.getCurrentUser() == null)
        {
            return tasksLiveData;
        }

        tasksRef.get().addOnCompleteListener(_task -> {
            if (_task.isSuccessful()) {
                List<TodoTaskData> tasks = new ArrayList<>();
                for (QueryDocumentSnapshot document : _task.getResult()) {
                    TodoTask todotask = document.toObject(TodoTask.class);

                    if(mAuth.getCurrentUser() != null && !todotask.getUserId().equals(mAuth.getCurrentUser().getUid())){
                        continue;
                    }

                    String id = document.getId();
                    TodoTaskData task = new TodoTaskData(id, todotask);
                    tasks.add(task);
                }

                // Sort tasks by closer to current date
                tasks.sort(Comparator.comparing(TodoTaskData::getDueDate));

                tasksLiveData.setValue(tasks);
            } else {
                // Handle error
                tasksLiveData.setValue(null);
            }
        });
        return tasksLiveData;
    }

    public Task<DocumentReference> addTask(TodoTask task) {
        return tasksRef.add(task);
    }

    public Task<DocumentSnapshot> getTaskData(String id) {
        return tasksRef.document(id).get();
    }

    public static void deleteTasksOfUser(String userId) {
        // Assuming tasksRef is correctly initialized and points to the "tasks" collection
        CollectionReference tasksRef = FirebaseFirestore.getInstance().collection("tasks");

        tasksRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    TodoTask todoTask = document.toObject(TodoTask.class);
                    String id = document.getId();

                    // Check if the task belongs to the specified user
                    if (todoTask.getUserId().equals(userId)) {
                        tasksRef.document(id).delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Document successfully deleted
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                })
                                .addOnFailureListener(e -> {
                                    // Handle any errors
                                    Log.w(TAG, "Error deleting document", e);
                                });
                    }
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public Task<Void> updateTask(TodoTaskData task) {
        TodoTask todoTask = new TodoTask(task);
        return tasksRef.document(task.getId()).set(todoTask);
    }
}
