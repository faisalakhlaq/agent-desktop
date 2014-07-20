package com.adt.app;

import com.adt.adapters.HoursListAdapter;
import com.adt.database.ADTDBHelper;
import com.adt.model.WorkHours;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class HoursListActivity extends Activity implements OnItemClickListener, OnClickListener
{
	private HoursListAdapter adapter = null; // TODO check if these variables
												// are

	private ListView list = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.hours_list);

		ADTDBHelper db = new ADTDBHelper(this);
		adapter = new HoursListAdapter(this, db.getHours());
		// HoursListAdapter adapter = new HoursListAdapter(this, db.getHours());

		list = (ListView) findViewById(R.id.hours_lv);
		// ListView list = (ListView) findViewById(R.id.hours_lv);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		ImageButton homeBtn = (ImageButton) findViewById(R.id.header_home);
		homeBtn.setOnClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		HoursListAdapter adp = (HoursListAdapter) parent.getAdapter();
		WorkHours hours = adp.getItem(position);

		if (hours != null)
		{
			Intent i = new Intent(view.getContext(), HoursDetailActivity.class);
			i.putExtra("job-title", hours.getJobTitle().trim());
			this.startActivity(i);
		}
		else
		{
			// TODO this should never be true CHECK - TEST
			CharSequence msg = "Sorry the JOB details cannot be retrieved";
			Toast.makeText(HoursListActivity.this, msg, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.header_home)
		{
			Intent i = new Intent(this, ADT.class);
			startActivity(i);
		}
	}

}
