package com.example.nasa_image_search;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final int MONTH_OFFSET = 1;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalDate today = LocalDate.now();

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        System.out.println(year + "/" + (month+1) + "/" + day);

        ((MainActivity)getActivity()).setDate(LocalDate.of(year, (month + MONTH_OFFSET), day));

    }
}
