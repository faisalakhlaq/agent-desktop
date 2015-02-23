package com.adt.app;

import java.util.ArrayList;
import java.util.Calendar;

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

import com.adt.database.ADTDBHelper;
import com.adt.model.WorkHours;
import com.adt.utils.Helper;
import com.adt.utils.Utils;

/*
 * Author: Faisal Akhlaq
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
		// setContentView(R.layout.adt);
		setContentView(R.layout.working_hours_tracker);

		db = new ADTDBHelper(ADT.this);
		addListeners();
		congifureButtons();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.working_hours_chk_in_btn:
		{
			// Get the inactive jobs. jobs
			// will be made active once the
			// user checks in for a job
			checkIn(db.getInActiveJobNames());
			break;
		}
		case R.id.working_hours_chk_out_btn:
		{
			checkOut(db.getActiveJobNames());
			break;
		}
		// case R.id.adt_edit_task_lbl:
		// {
		// Intent intent = new Intent(ADT.this, JobListActivity.class);
		// startActivity(intent);
		// break;
		// }
		case R.id.working_hours_my_hours:
		{
			Intent i = new Intent(ADT.this, DisplayHoursActivity.class);
			startActivity(i);
			break;
		}
		case R.id.wht_addhour_btn:
		{
			Utils u = new Utils();
			u.showMessage("Sorry", "Feature not available", this).run();
			break;
		}
		default:
		{
			break;
		}
		}
	}

	private void addListeners()
	{
		Button checkIn = (Button) findViewById(R.id.working_hours_chk_in_btn);
		checkIn.setOnClickListener(this);

		Button checkOut = (Button) findViewById(R.id.working_hours_chk_out_btn);
		checkOut.setOnClickListener(this);

		Button myHours = (Button) findViewById(R.id.working_hours_my_hours);
		myHours.setOnClickListener(this);

		Button addHours = (Button) findViewById(R.id.wht_addhour_btn);
		addHours.setOnClickListener(this);
	}

	private void congifureButtons()
	{
		configCreateTaskBtn();
		configEditTaskBtn();

		configShowHoursBtn();
		configEditHoursBtn();
	}

	private void configEditHoursBtn()
	{
		// TODO Auto-generated method stub

	}

	private void configShowHoursBtn()
	{
		Button showHours = (Button) findViewById(R.id.working_hours_show_hours_btn_lbl);
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
		Button editTaskBtn = (Button) findViewById(R.id.wht_edit_task_btn);
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
		// TODO execute this job in another thread??
		if (jobNameList == null || jobNameList.size() < 1)
		{
			Utils u = new Utils();
			u.showMessage("Sorry", "No checked in job or there is no job", this).run();
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
				WorkHours h = new WorkHours();
				h.setJobTitle(String.valueOf(items[item]));
				long time = Calendar.getInstance().getTimeInMillis();
				time += Helper.getTimeOffset(time);
				h.setCheckOutTime(time);
				if (db.insertCheckOutHours(h)) Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				// TODO show failure message
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void checkIn(ArrayList<String> jobNameList)
	{
		// TODO execute this job in another thread??
		if (jobNameList == null || jobNameList.size() < 1)
		{
			Utils u = new Utils();
			u.showMessage("Sorry", "Either you checked-in already or there is no job to check-in", this).run();
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
				WorkHours h = new WorkHours();
				h.setJobTitle(String.valueOf(items[item]));
				long time = Calendar.getInstance().getTimeInMillis();
				time += Helper.getTimeOffset(time);
				h.setCheckInTime(time);
				if (db.insertCheckInHours(h)) Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
