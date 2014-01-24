package com.adt.app;

import java.util.ArrayList;

import model.Hours;
import utils.Notifier;
import utils.Utils;
import adapters.HoursDetailAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import database.ADTDBHelper;

public class HoursActivity extends Activity implements OnClickListener, Notifier
{
	private HoursDetailAdapter adapter;

	private ArrayList<Hours> hoursList;

	private ADTDBHelper db;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.hours_detail);

		Intent i = getIntent();
		Hours hours = (Hours) i.getSerializableExtra("hours");
		// TODO get the job title

		String title = hours.getJobTitle();
		db = new ADTDBHelper(this);
		hoursList = db.getHoursDescription(title);
		adapter = new HoursDetailAdapter(this, hoursList);
		adapter.setNotifier(this);

		ListView list = (ListView) findViewById(R.id.hours_detail_lv);
		// ListView list = (ListView) findViewById(R.id.hours_lv);
		list.setAdapter(adapter);

		ImageButton homeBtn = (ImageButton) findViewById(R.id.adt_header_home);
		homeBtn.setOnClickListener(this);

		ImageButton editBtn = (ImageButton) findViewById(R.id.adt_header_edit_btn);
		editBtn.setOnClickListener(this);

		ImageButton doneBtn = (ImageButton) findViewById(R.id.header_done_btn);
		doneBtn.setOnClickListener(this);
	}

	Runnable populate = new Runnable()
	{
		@Override
		public void run()
		{
			adapter.clear();
			for (int i = 0; i < hoursList.size(); i++)
			{
				Hours hour = hoursList.get(i);
				((Hours) hoursList.get(i)).setEditMode(true);
				adapter.add(hour);
			}
			adapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.adt_header_home)
		{
			Intent i = new Intent(this, ADT.class);
			startActivity(i);
		}
		if (v.getId() == R.id.adt_header_edit_btn)
		{
			ImageButton editBtn = (ImageButton) findViewById(R.id.adt_header_edit_btn);
			editBtn.setVisibility(View.GONE);
			ImageButton doneBtn = (ImageButton) findViewById(R.id.header_done_btn);
			doneBtn.setVisibility(View.VISIBLE);
			for (int i = 0; i < hoursList.size(); i++)
			{
				((Hours) hoursList.get(i)).setEditMode(true);
			}
			adapter.notifyDataSetChanged();
		}
		if (v.getId() == R.id.header_done_btn)
		{
			ImageButton editBtn = (ImageButton) findViewById(R.id.adt_header_edit_btn);
			editBtn.setVisibility(View.VISIBLE);
			ImageButton doneBtn = (ImageButton) findViewById(R.id.header_done_btn);
			doneBtn.setVisibility(View.GONE);
			for (int i = 0; i < hoursList.size(); i++)
			{
				((Hours) hoursList.get(i)).setEditMode(false);
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void notify(Object data, String type)
	{
		Hours hour = (Hours) data;
		Utils.println("Deleting hours" + hour.getJobTitle());
		if (db.deleteHours(hour))
		{
			CharSequence msg = "Selection deleted";
			Toast.makeText(HoursActivity.this, msg, Toast.LENGTH_SHORT).show();
			refreshData();
		}
		else
		{
			CharSequence msg = "Error while deleting";
			Toast.makeText(HoursActivity.this, msg, Toast.LENGTH_SHORT).show();
		}
	}

	private void refreshData()
	{
		Intent i = getIntent();
		Hours hours = (Hours) i.getSerializableExtra("hours");
		// TODO get the job title

		String title = hours.getJobTitle();
		hoursList = db.getHoursDescription(title);
		runOnUiThread(populate);
	}
}
