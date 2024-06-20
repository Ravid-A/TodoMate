package com.example.todomate.Fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.todomate.R;
import com.example.todomate.receiver.ReminderBroadcastReceiver;

import java.util.Calendar;
import java.util.Locale;

public class ReminderFragment extends Fragment {

    private TimePicker timePicker;

    private Calendar dueDate;

    private String taskId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        timePicker = view.findViewById(R.id.timePicker);
        Button buttonSetReminder = view.findViewById(R.id.buttonSetReminder);

        // Initialize TimePicker with current time
        initializeTimePicker();

        // Example of retrieving selected time from TimePicker
        buttonSetReminder.setOnClickListener(v -> {
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();

            // Handle the selected time (e.g., set a reminder alarm)
            setReminder(hour, minute);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String date = dateFormat.format(dueDate.getTime());

            // Show confirmation message
            Toast.makeText(requireContext(), "Reminder set for " + date + " " + hour + ":" + minute, Toast.LENGTH_SHORT).show();

            // Close the fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });


        // get extra
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            long bundleLong = bundle.getLong("dueDate");
            dueDate = Calendar.getInstance();
            dueDate.setTimeInMillis(bundleLong);

            taskId = bundle.getString("taskId");
        }

        return view;
    }

    private void initializeTimePicker() {
        // Initialize TimePicker with current time
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);
        timePicker.setCurrentHour(currentHour);
        timePicker.setCurrentMinute(currentMinute);
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setReminder(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(dueDate.getTimeInMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
        calendar.add(Calendar.SECOND, 10);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), ReminderBroadcastReceiver.class);
        intent.putExtra("taskId", taskId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
