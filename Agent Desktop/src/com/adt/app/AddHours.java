package com.adt.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.adt.database.ADTDBHelper;
import com.adt.model.WorkHours;
import com.adt.utils.DatePickerFragment;
import com.adt.utils.Helper;
import com.adt.utils.Notifier;
import com.adt.utils.TimePickerFragment;
import com.adt.utils.Utils;

/**
 * AddHours uses TimePicker and DatePicker Fragments to let the user select the
 * date and time.
 * <p>
 * Notifier is implemented in order to allow Time and DatePicker Fragments to
 * notify AddHours when date and time are set.
 * */
public class AddHours extends FragmentActivity implements OnClickListener, Notifier
{
	private Date fromDate, toDate;

	private int fromHour, fromMinute, toHour, toMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.add_hours);

		populateJobList();
		addListeners();
	}

	private void addListeners()
	{
		Button fromDateBtn = (Button) findViewById(R.id.ah_fromdate_btn);
		fromDateBtn.setOnClickListener(this);

		Button toDateBtn = (Button) findViewById(R.id.ah_todate_btn);
		toDateBtn.setOnClickListener(this);

		Button fromTimeBtn = (Button) findViewById(R.id.ah_fromtime_btn);
		fromTimeBtn.setOnClickListener(this);

		Button toTimeBtn = (Button) findViewById(R.id.ah_totime_btn);
		toTimeBtn.setOnClickListener(this);

		Button addHoursBtn = (Button) findViewById(R.id.ah_btn);
		addHoursBtn.setOnClickListener(this);
	}

	private void populateJobList()
	{
		Spinner taskSpinner = (Spinner) findViewById(R.id.ah_tasks_spinner);
		ADTDBHelper dbHelper = new ADTDBHelper(this);
		ArrayList<String> jobNames = dbHelper.getAllJobNames();
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, jobNames);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		taskSpinner.setAdapter(dataAdapter);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.ah_fromtime_btn:
		{
			Button btn = (Button) findViewById(id);
			DialogFragment tpf = new TimePickerFragment(btn, AddHours.this);
			tpf.show(getSupportFragmentManager(), "timePicker");
			break;
		}
		case R.id.ah_totime_btn:
		{
			Button btn = (Button) findViewById(id);
			DialogFragment tpf = new TimePickerFragment(btn, AddHours.this);
			tpf.show(getSupportFragmentManager(), "timePicker");
			break;
		}
		case R.id.ah_fromdate_btn:
		{
			Button btn = (Button) findViewById(id);
			DialogFragment dpf = new DatePickerFragment(btn, AddHours.this);
			dpf.show(getSupportFragmentManager(), "datePicker");
			break;
		}
		case R.id.ah_todate_btn:
		{
			Button btn = (Button) findViewById(id);
			DialogFragment dpf = new DatePickerFragment(btn, AddHours.this);
			dpf.show(getSupportFragmentManager(), "datePicker");
			break;
		}
		case R.id.ah_btn:
		{
			addHoursToDB();
			break;
		}
		}
	}

	private void addHoursToDB()
	{
		if (fromDate == null || toDate == null)
		{
			Utils u = new Utils();
			u.showMessage("Enter Dates", "Please specify from and to dates", AddHours.this).run();
		}
		else if (toDate.before(fromDate))
		{
			Utils u = new Utils();
			u.showMessage("Wrong Dates", "To date cannot be before from date", AddHours.this).run();
		}
		else
		{
			Spinner taskSpinner = (Spinner) findViewById(R.id.ah_tasks_spinner);
			String jobTitle = (String) taskSpinner.getSelectedItem();

			WorkHours wh = new WorkHours();
			wh.setJobTitle(jobTitle);

			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			c.set(Calendar.HOUR_OF_DAY, fromHour);
			c.set(Calendar.MINUTE, fromMinute);
			fromDate = c.getTime();

			long time = c.getTimeInMillis();
			time += Helper.getTimeOffset(time);
			wh.setCheckInTime(time);

			c.setTime(toDate);
			c.set(Calendar.HOUR_OF_DAY, toHour);
			c.set(Calendar.MINUTE, toMinute);
			toDate = c.getTime();

			time = c.getTimeInMillis();
			time += Helper.getTimeOffset(time);
			wh.setCheckOutTime(time);

			ADTDBHelper db = new ADTDBHelper(AddHours.this);
			if (db.addHours(wh))
			{
				Toast.makeText(getApplicationContext(), "Hours Added", Toast.LENGTH_SHORT).show();
				// TODO if hours are added reset the labels of the buttons
			}
			else
			{
				Utils u = new Utils();
				u.showMessage("Sorry", "An Error occured while adding hours", AddHours.this).run();
			}
		}
	}

	@Override
	public void notify(Object data, String type)
	{
		switch (type)
		{
		case "FROMHOUR":
		{
			fromHour = (int) data;
			break;
		}
		case "FROMMINUTE":
		{
			fromMinute = (int) data;
			break;
		}
		case "TOHOUR":
		{
			toHour = (int) data;
			break;
		}
		case "TOMINUTE":
		{
			toMinute = (int) data;
			break;
		}
		case "FROMDATE":
		{
			fromDate = (Date) data;
			break;
		}
		case "TODATE":
		{
			toDate = (Date) data;
			break;
		}
		}
	}

}
