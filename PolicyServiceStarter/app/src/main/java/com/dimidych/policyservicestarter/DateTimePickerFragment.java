package com.dimidych.policyservicestarter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DateTimePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public Calendar DateCalendar;

    public DateTimePickerFragment() {
        DateCalendar = Calendar.getInstance();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), this, DateCalendar.get(Calendar.YEAR), DateCalendar.get(Calendar.MONTH), DateCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        DateCalendar.set(year, month, day);
        Intent intent = new Intent();
        intent.putExtra("selected_year", year);
        intent.putExtra("selected_month", month);
        intent.putExtra("selected_day", day);
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), Constants.DIALOG_OK, intent);
    }
}
