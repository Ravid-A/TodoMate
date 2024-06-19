package com.example.todomate.repository;

import com.example.todomate.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private static TaskRepository instance;
    private List<Task> tasks;

    private TaskRepository() {
        tasks = new ArrayList<>();
        // Add some dummy tasks
        tasks.add(new Task("1", "Buy groceries", "Milk, eggs, bread", System.currentTimeMillis() + 86400000));
        tasks.add(new Task("2", "Pay bills", "Electricity, internet, phone", System.currentTimeMillis() + 172800000));
        tasks.add(new Task("3", "Clean house", "Vacuum, mop, dust", System.currentTimeMillis() + 259200000));
    }

    public static TaskRepository getInstance() {
        if (instance == null) {
            instance = new TaskRepository();
        }
        return instance;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    // Add other methods to update, add, or delete tasks as needed
}