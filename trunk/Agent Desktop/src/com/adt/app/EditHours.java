package com.adt.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adt.database.ADTDBHelper;
import com.adt.model.WorkHours;
import com.adt.utils.DatePickerFragment;
import com.adt.utils.Helper;
import com.adt.utils.Notifier;
import com.adt.utils.TimePickerFragment;
import com.adt.utils.Utils;

public class EditHours extends FragmentActivity implements OnClickListener, Notifier
{
	// Check if the WorkHour is active (user is checkedin and not checkedout)
	private boolean isActive = false;

	private Date fromDate = null, toDate = null;

	private int fromHour = 0, fromMinute = 0, toHour = 0, toMinute = 0;

	private Button fromDateBtn, toDateBtn, fromTimeBtn, toTimeBtn;

	private WorkHours wh;

	// TODO timeChanged make a variable that checks if the date of time has been
	// changed

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.edit_hour_layout);

		Intent i = getIntent();
		wh = (WorkHours) i.getSerializableExtra("HOURS");

		fromDateBtn = (Button) findViewById(R.id.eh_fromdate_btn);
		toDateBtn = (Button) findViewById(R.id.eh_todate_btn);
		fromTimeBtn = (Button) findViewById(R.id.eh_fromtime_btn);
		toTimeBtn = (Button) findViewById(R.id.eh_totime_btn);

		displayValues();
		addButtonListeners();
	}

	private void addButtonListeners()
	{
		Button saveBtn = (Button) findViewById(R.id.eh_update_btn);
		saveBtn.setOnClickListener(this);

		fromDateBtn.setOnClickListener(this);
		toDateBtn.setOnClickListener(this);
		fromTimeBtn.setOnClickListener(this);
		toTimeBtn.setOnClickListener(this);
	}

	private void displayValues()
	{
		if (wh != null)
		{
			String title = wh.getJobTitle();
			TextView titleTv = (TextView) findViewById(R.id.eh_title_tv);
			EditText duration = (EditText) findViewById(R.id.eh_duration_et);

			titleTv.setText(title);

			Helper helper = new Helper();
			fromTimeBtn.setText(helper.convertMSFormated(wh.getCheckInTime()));
			fromDateBtn.setText((new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(wh.getCheckInTime())));

			toTimeBtn.setText(helper.convertMSFormated(wh.getCheckOutTime()));
			toDateBtn.setText((new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(wh.getCheckOutTime())));

			duration.setText(helper.msToHMS(wh.getTotalHours()));
			if (wh.getCheckOutTime() == 0)
			{
				isActive = true;
				// if check out time == 0 then the total working hours = 0.
				// Means the user is still working on this task. Therefore,
				// calculate the duration from checkin time upto the current
				// time
				long time = Calendar.getInstance().getTimeInMillis();
				time += Helper.getTimeOffset(time);

				duration.setText(helper.msToHMS(time - wh.getCheckInTime()));

				// indicate that the user is still checked in by writing checked
				// in under the checkout time
				toDateBtn.setText("Checked in");
				toTimeBtn.setText("Checked in");
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();

		switch (id)
		{
		case R.id.eh_update_btn:
		{
			if (isActive)
			{
				Utils u = new Utils();
				u.showMessage("Sorry", "Please Checkout. Cannot edit checked in jobs", EditHours.this).run();
			}
			else
			{
				updateWorkHours();
			}
			break;
		}
		case R.id.eh_fromtime_btn:
		{
			DialogFragment tpf = new TimePickerFragment(fromTimeBtn, EditHours.this);
			tpf.show(getSupportFragmentManager(), "timePicker");
			break;
		}
		case R.id.eh_totime_btn:
		{
			DialogFragment tpf = new TimePickerFragment(toTimeBtn, EditHours.this);
			tpf.show(getSupportFragmentManager(), "timePicker");
			break;
		}
		case R.id.eh_fromdate_btn:
		{
			DialogFragment dpf = new DatePickerFragment(fromDateBtn, EditHours.this);
			dpf.show(getSupportFragmentManager(), "datePicker");
			break;
		}
		case R.id.eh_todate_btn:
		{
			DialogFragment dpf = new DatePickerFragment(toDateBtn, EditHours.this);
			dpf.show(getSupportFragmentManager(), "datePicker");
			break;
		}
		}
	}

	private void updateWorkHours()
	{
		if (fromDate == null)
		{
			fromDate = new Date(wh.getCheckInTime());
		}
		if (toDate == null)
		{
			toDate = new Date(wh.getCheckOutTime());
		}
		{
			WorkHours whNew = new WorkHours();
			whNew.setJobTitle(wh.getJobTitle());

			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			long time = 0;

			// If the user has not changed the hour/minutes then we take the
			// previous values
			// otherwise we have to add the offset to time
			// Offset is not added in case of previous time as it has been
			// already added to it
			if (fromHour == 0 && fromMinute == 0)
			{
				fromHour = getHoursFromLong(wh.getCheckInTime());
				fromMinute = getMinutesFromLong(wh.getCheckInTime());
				c.set(Calendar.HOUR_OF_DAY, fromHour);
				c.set(Calendar.MINUTE, fromMinute);
				time = c.getTimeInMillis();
				time -= Helper.getTimeOffset(time);
				c.setTimeInMillis(time);
				fromDate = c.getTime();
			}
			else
			{
				c.set(Calendar.HOUR_OF_DAY, fromHour);
				c.set(Calendar.MINUTE, fromMinute);
				time = c.getTimeInMillis();
				fromDate = c.getTime();
			}
			time += Helper.getTimeOffset(time);
			whNew.setCheckInTime(time);

			// c.set(Calendar.HOUR_OF_DAY, toHour);
			// c.set(Calendar.MINUTE, toMinute);
			// toDate = c.getTime();

			c.setTime(toDate);
			if (toHour == 0 && toMinute == 0)
			{
				toHour = getHoursFromLong(wh.getCheckOutTime());
				toMinute = getMinutesFromLong(wh.getCheckOutTime());
				c.set(Calendar.HOUR_OF_DAY, toHour);
				c.set(Calendar.MINUTE, toMinute);
				time = c.getTimeInMillis();
				time -= Helper.getTimeOffset(time);
				c.setTimeInMillis(time);
				toDate = c.getTime();
			}
			else
			{
				c.set(Calendar.HOUR_OF_DAY, toHour);
				c.set(Calendar.MINUTE, toMinute);
				time = c.getTimeInMillis();
				toDate = c.getTime();
			}
			time += Helper.getTimeOffset(time);
			whNew.setCheckOutTime(time);

			if (toDate.before(fromDate))
			{
				Utils u = new Utils();
				u.showMessage("Wrong Dates", "To date cannot be before from date", EditHours.this).run();
				displayValues();
				resetValues();
			}
			else
			{
				ADTDBHelper db = new ADTDBHelper(EditHours.this);
				if (db.updateWorkHours(wh, whNew))
				{
					Toast.makeText(getApplicationContext(), "Hours Updated", Toast.LENGTH_SHORT).show();
					Intent i = new Intent(EditHours.this, HoursListActivity.class);
					startActivity(i);
				}
				// TODO else
			}
		}
	}

	@Override
	public void notify(Object data, String type)
	{
		// TODO update the duration text box on date and time change
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

	private int getMinutesFromLong(long date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(date));

		return c.get(Calendar.MINUTE);
	}

	private int getHoursFromLong(long date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(date));
		return c.get(Calendar.HOUR_OF_DAY);
	}

	private void resetValues()
	{
		fromDate = null;
		toDate = null;

		fromHour = 0;
		fromMinute = 0;
		toHour = 0;
		toMinute = 0;
	}
}
