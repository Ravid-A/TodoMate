package com.example.todomate.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.todomate.model.TodoTask;
import com.example.todomate.model.TodoTaskData;
import com.example.todomate.repository.TaskRepository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class TaskViewModel extends ViewModel {

    private final TaskRepository taskRepository;

    public TaskViewModel() {
        taskRepository = new TaskRepository();
    }

    public LiveData<List<TodoTaskData>> getTasks() {
        return taskRepository.getTasks();
    }

    public Task<DocumentReference> addTask(TodoTask task) {
        return taskRepository.addTask(task);
    }

    public Task<DocumentSnapshot> getTaskData(String id) {
        return taskRepository.getTaskData(id);
    }

    public Task<Void> updateTask(TodoTaskData task) {
        return taskRepository.updateTask(task);
    }
}
