package com.adt.utils;

import java.util.Calendar;

import com.adt.app.R;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener
{
	private Notifier notifier = null;

	private Button timeBtn;

	public TimePickerFragment(Button timeBtn, Notifier notifier)
	{
		this.timeBtn = timeBtn;
		this.notifier = notifier;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute)
	{
		timeBtn.setText(hourOfDay + ":" + minute);

		int btnId = timeBtn.getId();

		switch (btnId)
		{
		case R.id.ah_fromtime_btn:
		{
			notifier.notify(hourOfDay, "FROMHOUR");
			notifier.notify(minute, "FROMMINUTE");
			break;
		}
		case R.id.ah_totime_btn:
		{
			notifier.notify(hourOfDay, "TOHOUR");
			notifier.notify(minute, "TOMINUTE");
			break;
		}
		}

	}
}
