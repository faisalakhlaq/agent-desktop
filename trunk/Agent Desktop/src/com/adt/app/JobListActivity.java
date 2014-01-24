package com.adt.app;

import java.util.ArrayList;

import model.Job;
import adapters.JobListAdapter;
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
import database.ADTDBHelper;

public class JobListActivity extends Activity implements OnItemClickListener, OnClickListener
{
	private JobListAdapter adapter = null; // TODO check if these variables are
											// needed?????? even if they are
											// removed wont make any difference

	private ArrayList<Job> taskList = null;

	private ListView list = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.task_list);

		fillTaskList();
		adapter = new JobListAdapter(this, taskList);
		list = (ListView) findViewById(R.id.task_listview);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		ImageButton homeBtn = (ImageButton) findViewById(R.id.header_home);
		homeBtn.setOnClickListener(this);
	}

	private void fillTaskList()
	{
		ADTDBHelper db = new ADTDBHelper(this);
		taskList = db.getAllJobs();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		JobListAdapter adp = (JobListAdapter) parent.getAdapter();
		Job task = adp.getItem(position);
		if (task != null)
		{
			Intent i = new Intent(view.getContext(), JobActivity.class);
			i.putExtra("task", task);
			this.startActivity(i);
		}
		else
		{
			// TODO this should never be true CHECK - TEST
			CharSequence msg = "Sorry the JOB details cannot be retrieved";
			Toast.makeText(JobListActivity.this, msg, Toast.LENGTH_SHORT).show();
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
