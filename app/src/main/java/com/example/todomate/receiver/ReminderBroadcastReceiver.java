package com.example.todomate.receiver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todomate.MainActivity;
import com.example.todomate.R;
import com.example.todomate.model.TodoTask;
import com.example.todomate.viewmodel.TaskViewModel;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ReminderChannel";

    private void createNotificationChannel(Context context) {
        // Create the notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Channel";
            String description = "Channel for reminder notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        // Create the notification channel
        createNotificationChannel(context);

        // Get the task ID from the intent
        String taskId = intent.getStringExtra("taskId");

        TaskViewModel taskViewModel = new TaskViewModel();

        // Get the task from the database
        taskViewModel.getTaskData(taskId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                TodoTask todoTask = task.getResult().toObject(TodoTask.class);

                if (todoTask == null) {
                    return;
                }

                // Send the notification
                sendNotification(context, todoTask);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(Context context, TodoTask task) {
        // Create a PendingIntent to open the app when the notification is clicked
        Intent openAppIntent = new Intent(context, MainActivity.class); // Replace with your main activity
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // Replace with your app's notification icon
                .setContentTitle("Reminder")
                .setContentText("Remember to complete task: " + task.getTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        System.out.println("ReminderBroadcastReceiver.onReceive");

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}