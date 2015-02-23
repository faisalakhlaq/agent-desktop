package com.adt.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.adt.database.ADTDBHelper;
import com.adt.model.WorkHours;
import com.adt.utils.Utils;

/**
 * Display the hours according to date or task or both
 */
public class DisplayHoursActivity extends Activity implements OnClickListener
{
	private Date fromDate = null;

	private Date toDate = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_hours_options);

		addListenersToButtons();

		populateJobList();
	}

	private void addListenersToButtons()
	{
		Button displayAllBtn = (Button) findViewById(R.id.dho_display_all_btn);
		displayAllBtn.setOnClickListener(this);

		Button fromDateBtn = (Button) findViewById(R.id.dho_from_date_btn);
		fromDateBtn.setOnClickListener(this);

		Button toDateBtn = (Button) findViewById(R.id.dho_to_date_btn);
		toDateBtn.setOnClickListener(this);

		RadioButton dateWiseRBtn = (RadioButton) findViewById(R.id.dho_datewise_rbtn);
		dateWiseRBtn.setOnCheckedChangeListener(new RadioButtonListener());

		RadioButton dateTaskWiseRBtn = (RadioButton) findViewById(R.id.dho_date_taskwise_rbtn);
		dateTaskWiseRBtn.setOnCheckedChangeListener(new RadioButtonListener());

		RadioButton taskWiseRBtn = (RadioButton) findViewById(R.id.dho_taskwise_rbtn);
		taskWiseRBtn.setOnCheckedChangeListener(new RadioButtonListener());

		Button showResultBtn = (Button) findViewById(R.id.dho_show_btn);
		showResultBtn.setOnClickListener(this);
	}

	public void onRadioButtonClicked(View v)
	{
		boolean checked = ((RadioButton) v).isChecked();

		switch (v.getId())
		{
		case R.id.dho_date_taskwise_rbtn:
			if (checked)
			{
				Button fromDateBtn = (Button) findViewById(R.id.dho_from_date_btn);
				fromDateBtn.setEnabled(true);

				Button toDateBtn = (Button) findViewById(R.id.dho_to_date_btn);
				toDateBtn.setEnabled(true);

				Spinner taskSpinner = (Spinner) findViewById(R.id.dho_tasks_spinner);
				taskSpinner.setEnabled(true);

				Button showBtn = (Button) findViewById(R.id.dho_show_btn);
				showBtn.setEnabled(true);
			}
			break;
		case R.id.dho_datewise_rbtn:
			if (checked)
			{
				Button fromDateBtn = (Button) findViewById(R.id.dho_from_date_btn);
				fromDateBtn.setEnabled(true);

				Button toDateBtn = (Button) findViewById(R.id.dho_to_date_btn);
				toDateBtn.setEnabled(true);

				Button showBtn = (Button) findViewById(R.id.dho_show_btn);
				showBtn.setEnabled(true);

				Spinner taskSpinner = (Spinner) findViewById(R.id.dho_tasks_spinner);
				taskSpinner.setEnabled(false);
			}
			break;
		case R.id.dho_taskwise_rbtn:
			if (checked)
			{
				Spinner taskSpinner = (Spinner) findViewById(R.id.dho_tasks_spinner);
				taskSpinner.setEnabled(true);

				Button showBtn = (Button) findViewById(R.id.dho_show_btn);
				showBtn.setEnabled(true);

				Button fromDateBtn = (Button) findViewById(R.id.dho_from_date_btn);
				fromDateBtn.setEnabled(false);

				Button toDateBtn = (Button) findViewById(R.id.dho_to_date_btn);
				toDateBtn.setEnabled(false);
			}
			break;
		}
	}

	private void populateJobList()
	{
		Spinner taskSpinner = (Spinner) findViewById(R.id.dho_tasks_spinner);
		ADTDBHelper dbHelper = new ADTDBHelper(this);
		ArrayList<String> jobNames = dbHelper.getAllJobNames();
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, jobNames);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		taskSpinner.setAdapter(dataAdapter);
		taskSpinner.setEnabled(false);
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.dho_display_all_btn)
		{
			Intent intent = new Intent(DisplayHoursActivity.this, HoursListActivity.class);
			startActivity(intent);
		}
		else if (v.getId() == R.id.dho_from_date_btn)
		{
			Button fromDateBtn = (Button) findViewById(R.id.dho_from_date_btn);
			showDatePickerDialog(fromDateBtn);
		}
		else if (v.getId() == R.id.dho_to_date_btn)
		{
			Button toDateBtn = (Button) findViewById(R.id.dho_to_date_btn);
			showDatePickerDialog(toDateBtn);
		}
		else if (v.getId() == R.id.dho_date_taskwise_rbtn || v.getId() == R.id.dho_taskwise_rbtn)
		{
			onRadioButtonClicked(v);
		}
		else if (v.getId() == R.id.dho_show_btn)
		{
			getResults();
		}
	}

	private void getResults()
	{
		ADTDBHelper dbHelper = new ADTDBHelper(this);
		boolean checked = ((RadioButton) findViewById(R.id.dho_date_taskwise_rbtn)).isChecked();
		if (checked)
		{
			if (fromDate == null || toDate == null)
			{
				Utils u = new Utils();
				Runnable msg = u.showMessage("Error", "Please provide dates", this);
				msg.run();
			}
			else
			{
				Spinner taskSpinner = (Spinner) findViewById(R.id.dho_tasks_spinner);
				String taskName = (String) taskSpinner.getSelectedItem();

				ArrayList<WorkHours> hours = dbHelper.getHours(fromDate, toDate, taskName);
				Intent intent = new Intent(DisplayHoursActivity.this, HoursDetailActivity.class);
				intent.putExtra("hours", hours);
				intent.putExtra("activity", "DisplayHoursActivity");
				intent.putExtra("job-title", taskName);
				startActivity(intent);
			}
		}
		else if (((RadioButton) findViewById(R.id.dho_datewise_rbtn)).isChecked())
		{
			if (fromDate == null || toDate == null)
			{
				Utils u = new Utils();
				Runnable msg = u.showMessage("Error", "Please provide dates", this);
				msg.run();
			}
			else
			{
				ArrayList<WorkHours> hours = dbHelper.getHours(fromDate, toDate);
				Intent intent = new Intent(DisplayHoursActivity.this, HoursDetailActivity.class);
				intent.putExtra("hours", hours);
				intent.putExtra("activity", "DisplayHoursActivity");
				intent.putExtra("job-title", "ALL");
				intent.putExtra("fromDate", fromDate);
				intent.putExtra("toDate", toDate);
				// Sending 'ALL' as task name means that hours for
				// all tasks are retrieved. Not just for one task. And we have
				// to provide the from and to date as well.
				startActivity(intent);
			}
		}
		else if (((RadioButton) findViewById(R.id.dho_taskwise_rbtn)).isChecked())
		{
			Spinner taskSpinner = (Spinner) findViewById(R.id.dho_tasks_spinner);
			String taskName = (String) taskSpinner.getSelectedItem();

			ArrayList<WorkHours> hours = dbHelper.getHours(taskName);
			Intent intent = new Intent(DisplayHoursActivity.this, HoursDetailActivity.class);
			intent.putExtra("hours", hours);
			intent.putExtra("activity", "DisplayHoursActivity");
			intent.putExtra("job-title", taskName);
			startActivity(intent);
		}
	}

	/**
	 * Display the date picker dialog and set the button text to the selected
	 * date
	 */
	private void showDatePickerDialog(final Button btn)
	{
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
		{
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
			{
				btn.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
				Calendar calendar = Calendar.getInstance();

				if (btn.getId() == R.id.dho_from_date_btn)
				{
					calendar.set(year, (monthOfYear), dayOfMonth, 0, 0, 0);
					fromDate = calendar.getTime();
				}
				else if (btn.getId() == R.id.dho_to_date_btn)
				{
					toDate = calendar.getTime();
					calendar.set(year, (monthOfYear), dayOfMonth, 24, 0, 0);
				}
			}
		}, mYear, mMonth, mDay);
		dpd.show();
	}

	private class RadioButtonListener implements OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton btn, boolean arg1)
		{
			onRadioButtonClicked(btn);
		}
	}
}