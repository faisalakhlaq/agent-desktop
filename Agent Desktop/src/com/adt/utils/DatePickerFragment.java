package com.adt.utils;

import java.util.Calendar;
import java.util.Date;

import com.adt.app.R;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements OnDateSetListener
{
	private Notifier notifier = null;

	private Button dateBtn;

	public DatePickerFragment(Button dateBtn, Notifier notifier)
	{
		this.dateBtn = dateBtn;
		this.notifier = notifier;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
	{
		dateBtn.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
		Calendar c = Calendar.getInstance();
		c.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
		Date date = c.getTime();

		int btnId = dateBtn.getId();

		if (btnId == R.id.ah_fromdate_btn || btnId == R.id.eh_fromdate_btn)
		{
			notifier.notify(date, "FROMDATE");
		}
		else if (btnId == R.id.ah_todate_btn || btnId == R.id.eh_todate_btn)
		{
			notifier.notify(date, "TODATE");
		}
		// switch (btnId)
		// {
		// case R.id.ah_fromdate_btn:
		// {
		// notifier.notify(date, "FROMDATE");
		// break;
		// }
		// case R.id.ah_todate_btn:
		// {
		// notifier.notify(date, "TODATE");
		// break;
		// }
		// }
	}
}