package com.inopek.duvana.sink.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import com.inopek.duvana.sink.constants.SinkConstants;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(SinkConstants.DATE_FORMAT_DD_MM_YYYY);

    private EditText dateText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        DateTime dateTime = new DateTime(year, month + 1, day, 0, 0, 0);
        dateText.setText(dateTime.toString(DATE_TIME_FORMATTER));
    }

    public void setDateText(EditText dateText) {
        this.dateText = dateText;
    }
}
