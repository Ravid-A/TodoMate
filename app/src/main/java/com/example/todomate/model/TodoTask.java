package com.example.todomate.model;

public class TodoTask {
    private String userId; // New field to store Firebase Auth user ID
    private String title;
    private String description;
    private long dueDate;

    public TodoTask() {
        // Required empty public constructor
    }

    public TodoTask(String userId, String title, String description, long dueDate) {
        this.userId = userId;
        this.dueDate = dueDate;
        this.description = description;
        this.title = title;
    }

    public TodoTask(TodoTaskData task) {
        this.userId = task.getUserId();
        this.dueDate = task.getDueDate();
        this.description = task.getDescription();
        this.title = task.getTitle();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    // Getters and setters for all fields (omitted for brevity)
}
