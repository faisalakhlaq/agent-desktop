package com.adt.app;

import java.util.ArrayList;

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

import com.adt.database.ADTDBHelper;
import com.adt.utils.DatePickerFragment;
import com.adt.utils.TimePickerFragment;

public class AddHours extends FragmentActivity implements OnClickListener
{

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
			DialogFragment tpf = new TimePickerFragment(btn);
			tpf.show(getSupportFragmentManager(), "timePicker");
			break;
		}
		case R.id.ah_totime_btn:
		{
			Button btn = (Button) findViewById(id);
			DialogFragment tpf = new TimePickerFragment(btn);
			tpf.show(getSupportFragmentManager(), "timePicker");
			break;
		}
		case R.id.ah_fromdate_btn:
		{
			Button btn = (Button) findViewById(id);
			DialogFragment dpf = new DatePickerFragment(btn);
			dpf.show(getSupportFragmentManager(), "datePicker");
			break;
		}
		case R.id.ah_todate_btn:
		{
			Button btn = (Button) findViewById(id);
			DialogFragment dpf = new DatePickerFragment(btn);
			dpf.show(getSupportFragmentManager(), "datePicker");
			break;
		}
		}
	}

}
