package com.adt.app;

import adapters.HoursListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import database.ADTDBHelper;

public class HoursListActivity extends Activity implements OnClickListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{ // TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hours_list);
		ADTDBHelper db = new ADTDBHelper(this);
		HoursListAdapter adapter = new HoursListAdapter(this, db.getHours());
		ListView listView = (ListView) findViewById(R.id.hours_lv);
		listView.setAdapter(adapter);
		
		ImageButton homeBtn = (ImageButton) findViewById(R.id.header_home);
		homeBtn.setOnClickListener(this);
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
