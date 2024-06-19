package com.example.todomate.model;

public class Task {
    private String id;
    private String title;
    private String description;
    private long dueDate;

    public Task(String id, String title, String description, long dueDate) {
        this.id = id;
        this.dueDate = dueDate;
        this.description = description;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}