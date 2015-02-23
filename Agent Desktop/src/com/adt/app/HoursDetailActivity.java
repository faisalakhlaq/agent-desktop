package com.adt.app;

import java.util.ArrayList;
import java.util.Date;

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

import com.adt.adapters.HoursDetailAdapter;
import com.adt.database.ADTDBHelper;
import com.adt.model.WorkHours;
import com.adt.utils.DeleteBtnVisibilityListener;
import com.adt.utils.Notifier;
import com.adt.utils.Utils;

public class HoursDetailActivity extends Activity implements OnClickListener, Notifier, DeleteBtnVisibilityListener
{
	private HoursDetailAdapter adapter;

	private View visibileDelBtnRow = null;

	private ArrayList<WorkHours> hoursList;

	private ADTDBHelper db;

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.hours_detail);

		Intent i = getIntent();
		String callerActivity = i.getStringExtra("activity");
		// if ("DisplayHoursActivity".equals(caller.getClassName()))
		db = new ADTDBHelper(this);
		if ("DisplayHoursActivity".equals(callerActivity))
		{
			hoursList = (ArrayList<WorkHours>) i.getSerializableExtra("hours");
		}
		else
		{
			String jobTitle = (String) i.getStringExtra("job-title");
			hoursList = db.getHoursDescription(jobTitle);
		}
		adapter = new HoursDetailAdapter(this, hoursList);
		adapter.setNotifier(this);
		adapter.setDeleteBtnVisibilityListener(this);

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
				WorkHours hour = hoursList.get(i);
				((WorkHours) hoursList.get(i)).setEditMode(true);
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
				((WorkHours) hoursList.get(i)).setEditMode(true);
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
				((WorkHours) hoursList.get(i)).setEditMode(false);
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void notify(Object data, String type)
	{
		WorkHours hour = (WorkHours) data;
		Utils.println("Deleting hours" + hour.getJobTitle());
		if (db.deleteHours(hour))
		{
			CharSequence msg = "Selection deleted";
			Toast.makeText(HoursDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
			refreshData(hour);
		}
		else
		{
			CharSequence msg = "Error while deleting";
			Toast.makeText(HoursDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
		}
	}

	private void refreshData(WorkHours hour)
	{
		Intent i = getIntent();
		String title = (String) i.getSerializableExtra("job-title");
		if ("ALL".equals(title))
		{
			Date fromDate = (Date) i.getSerializableExtra("fromDate");
			Date toDate = (Date) i.getSerializableExtra("toDate");
			hoursList = db.getHours(fromDate, toDate);
		}
		else
		{
			hoursList = db.getHoursDescription(title);
		}
		runOnUiThread(populate);
	}

	@Override
	public void setDelBtnVisible(boolean visible)
	{
		if (!visible && visibileDelBtnRow != null)
		{
			ImageButton deleteBtn = (ImageButton) visibileDelBtnRow.findViewById(R.id.hours_detail_item_delete_button);
			deleteBtn.setVisibility(View.GONE);
			// adapter.notifyDataSetChanged();
			visibileDelBtnRow = null;
		}
	}

	@Override
	public void setVisibleBtnRow(View row)
	{
		visibileDelBtnRow = row;
	}

}