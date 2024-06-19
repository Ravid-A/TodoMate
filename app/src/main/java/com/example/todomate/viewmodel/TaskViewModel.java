package com.example.todomate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.todomate.model.Task;
import com.example.todomate.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends ViewModel {
    private MutableLiveData<List<Task>> tasks;
    private TaskRepository repository;

    public TaskViewModel() {
        repository = TaskRepository.getInstance();
        tasks = new MutableLiveData<>();
        loadTasks();
    }

    private void loadTasks() {
        tasks.setValue(repository.getTasks());
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
    }

    // Add other methods to update, add, or delete tasks as needed
}