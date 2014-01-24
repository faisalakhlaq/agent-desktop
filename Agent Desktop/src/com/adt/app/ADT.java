package com.adt.app;

import java.util.ArrayList;
import java.util.Date;

import model.Hours;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import database.ADTDBHelper;

/*
 * Auther: Faisal Akhlaq
 * */

public class ADT extends Activity implements OnClickListener
{
	ADTDBHelper db = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//		setContentView(R.layout.adt);
		setContentView(R.layout.working_hours_tracker);

		db = new ADTDBHelper(ADT.this);
		addListeners();
		congifureButtons();
	}

	private void addListeners()
	{
//		TextView checkIn = (TextView) findViewById(R.id.adt_chk_in_lbl);
//		checkIn.setOnClickListener(this);
//
//		TextView checkOut = (TextView) findViewById(R.id.adt_chk_out_lbl);
//		checkOut.setOnClickListener(this);

		Button checkIn = (Button) findViewById(R.id.working_hours_chk_in_btn);
		checkIn.setOnClickListener(this);
		
		Button checkOut = (Button) findViewById(R.id.working_hours_chk_out_btn);
		checkOut.setOnClickListener(this);
	}

	private void congifureButtons()
	{
		configCreateTaskBtn();
		configEditTaskBtn();

		configShowHoursBtn();
		configEditHoursBtn();

		configCalendarEventsBtn();
		configCalculateIncomeBtn();
	}

	private void configCalculateIncomeBtn()
	{
		// TODO Auto-generated method stub

	}

	private void configCalendarEventsBtn()
	{
		// TODO Auto-generated method stub

	}

	private void configEditHoursBtn()
	{
		// TODO Auto-generated method stub

	}

	private void configShowHoursBtn()
	{
		Button showHours = (Button) findViewById(R.id.working_hours_show_hours_btn_lbl);
//		TextView showHours = (TextView) findViewById(R.id.adt_show_hours_lbl);
		showHours.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(ADT.this, HoursListActivity.class);
				startActivity(i);
			}
		});
	}

	private void configEditTaskBtn()
	{
		// TODO implement a separate listener for each task
		Button editTaskBtn = (Button) findViewById(R.id.working_hours_edit_task_btn);
//		TextView editTaskBtn = (TextView) findViewById(R.id.adt_edit_hours_lbl);
		if (editTaskBtn != null)
		{
			editTaskBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(ADT.this, JobListActivity.class);
					startActivity(intent);
				}
			});
		}
	}

	private void configCreateTaskBtn()
	{
//		TextView createTaskBtn = (TextView) findViewById(R.id.adt_create_task_lbl);
		Button createTaskBtn = (Button) findViewById(R.id.working_hours_create_task_btn);
		if (createTaskBtn != null)
		{
			createTaskBtn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(ADT.this, CreateJobActivity.class);
					startActivity(intent);
				}
			});
		}
	}

	private void checkOut(ArrayList<String> jobNameList)
	{
		// TODO execute this job in another thread
		if (jobNameList == null || jobNameList.size() < 1)
		{
			CharSequence msg = "No checked in job or there is no job";
			Toast.makeText(ADT.this, msg, Toast.LENGTH_SHORT).show();
			return;
		}
		int l = jobNameList.size();
		final CharSequence[] items = new String[l]; // TODO not a good way
		for (int i = 0; i < l; i++)
			items[i] = jobNameList.get(i);

		final AlertDialog.Builder builder = new AlertDialog.Builder(ADT.this);
		builder.setTitle("Check-out For");
		builder.setItems(items, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int item)
			{
				Hours h = new Hours();
				h.setJobTitle(String.valueOf(items[item]));
				h.setCheckOutTime(Long.valueOf(new Date().getTime()));
//				h.setCheckOutTime(Long.valueOf(new Date().getTime()).intValue());
				// TODO insert into the database in a background thread
				if (db.insertCheckOutHours(h)) Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				// TODO show failure message
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void checkIn(ArrayList<String> jobNameList)
	{
		// TODO execute this job in another thread
		if (jobNameList == null || jobNameList.size() < 1)
		{
			CharSequence msg = "Either you checked in already or there is no job to check-in";
			Toast.makeText(ADT.this, msg, Toast.LENGTH_SHORT).show();
			return;
		}
		int l = jobNameList.size();
		final CharSequence[] items = new String[l]; // TODO not a good way
		for (int i = 0; i < l; i++)
			items[i] = jobNameList.get(i);

		final AlertDialog.Builder builder = new AlertDialog.Builder(ADT.this);
		builder.setTitle("Check-in For");
		builder.setItems(items, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int item)
			{
				Hours h = new Hours();
				h.setJobTitle(String.valueOf(items[item]));
				long time = Long.valueOf(new Date().getTime()); // TODO remove the local variable
				String date = new Date(time).toString(); // Date returned is not correct 
				h.setCheckInTime(time);
//				h.setCheckInTime(Long.valueOf(new Date().getTime()).intValue());
				// TODO insert into the database in a background thread
				if (db.insertCheckInHours(h)) Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == (R.id.working_hours_chk_in_btn))
		{
			checkIn(db.getInActiveJobNames()); // Get the inactive jobs. jobs will be made active once the user checks in for a job
		}
		else if (v.getId() == (R.id.working_hours_chk_out_btn))
		{
			checkOut(db.getActiveJobNames());
		}
		else if (v.getId() == R.id.adt_edit_task_lbl)
		{
			Intent intent = new Intent(ADT.this, JobListActivity.class);
			startActivity(intent);
		}
	}

}
