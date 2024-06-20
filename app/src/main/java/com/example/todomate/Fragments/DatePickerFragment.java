package com.example.todomate.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todomate.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private OnDateSelectedListener listener;
    private DatePicker datePicker;

    public interface OnDateSelectedListener {
        void onDateSelected(Calendar selectedDate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker, container, false);

        datePicker = view.findViewById(R.id.datePicker);
        Button setDueDateButton = view.findViewById(R.id.setDueDateButton);

        setDueDateButton.setOnClickListener(v -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);

            if (listener != null) {
                listener.onDateSelected(selectedDate);
            }

            dismiss();
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if (context instanceof OnDateSelectedListener) {
            listener = (OnDateSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDateSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}