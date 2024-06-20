package com.example.todomate.model;

public class TodoTaskData {
        private String id;
        private String userId; // New field to store Firebase Auth user ID
        private String title;
        private String description;
        private long dueDate;

        public TodoTaskData(String id, TodoTask task) {
            this.id = id;
            this.userId = task.getUserId();
            this.dueDate = task.getDueDate();
            this.description = task.getDescription();
            this.title = task.getTitle();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
}
