package com.adt.app;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.adt.database.ADTDBHelper;
import com.adt.model.Job;
import com.adt.utils.Helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class JobActivity extends Activity implements OnClickListener
{
	private Job job = null;

	private EditText orgName = null;

	private EditText title = null;

	private EditText wages = null;

	private EditText date = null;

	private Button edit = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task);
		Intent i = getIntent();
		job = (Job) i.getSerializableExtra("task");

		if (job != null)
		{
			initTextFields();
			displayValues(job);
			disableTextfileds();
			addListenersToButtons();
		}
	}

	private void initTextFields()
	{
		orgName = (EditText) findViewById(R.id.task_org_name_txt);
		title = (EditText) findViewById(R.id.task_title_txt);
		wages = (EditText) findViewById(R.id.task_hourly_wage_txt);
		date = (EditText) findViewById(R.id.task_created_on_txt);
	}

	/**
	 * The textfields will be disabled and will be showing the fields. The
	 * fields should be enabled on edit button pressed
	 */
	private void disableTextfileds()
	{
		orgName.setEnabled(false);
		title.setEnabled(false);
		wages.setEnabled(false);
		date.setEnabled(false);
	}

	/**
	 * Enable textfields on the edit button click event
	 */
	private void enableTextfileds()
	{
		orgName.setEnabled(true);
		title.setEnabled(true);
		wages.setEnabled(true);
		// date.setEnabled(true); // TODO implement a solution for editing date
		// as well
	}

	private void displayValues(Job task)
	{
		orgName.setText(task.getOrganizationName());
		title.setText(task.getTitle());
		wages.setText(String.valueOf(task.getHourlyWages()));
		date.setText((new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(task.getCreationDate())));
	}

	private void addListenersToButtons()
	{
		Button delete = (Button) findViewById(R.id.task_delete_btn);
		delete.setOnClickListener(this);

		ImageButton homeBtn = (ImageButton) findViewById(R.id.header_home);
		homeBtn.setOnClickListener(this);

		edit = (Button) findViewById(R.id.task_edit_btn);
		edit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		// TODO Check if this if-else approach is good or not?
		if (v.getId() == R.id.header_home)
		{
			Intent i = new Intent(this, ADT.class);
			startActivity(i);
		}
		else if (v.getId() == R.id.task_delete_btn)
		{
			ADTDBHelper db = new ADTDBHelper(JobActivity.this);
			if (db.removeJob(job.getTitle())) finish();//FIXME check if the job is active. if not then delete
		}
		else if (v.getId() == R.id.task_edit_btn)
		{
			if (edit.getText().equals("Edit"))
			{
				enableTextfileds();
				edit.setText("Update");
			}
			else
			{
				String t = String.valueOf(title.getText()).trim();
				if (t.equals(""))
				{
					CharSequence msg = "Sorry  title cannot be null";
					Toast.makeText(JobActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				else
				{
					// TODO update creation date separately
					Job j = new Job();
					j.setTitle(t);
					j.setOrganizationName(String.valueOf(orgName.getText()));
					// TODO null will cause exception in the wages field
					j.setHourlyWages(Helper.getFloatFromEditable(wages.getText()));
					ADTDBHelper db = new ADTDBHelper(JobActivity.this);
					if(db.updateJob(job.getTitle(), j))
					{
						edit.setText("Edit");
						CharSequence msg = "Job - Task updated";
						Toast.makeText(JobActivity.this, msg, Toast.LENGTH_SHORT).show();
						disableTextfileds(); // TODO new thread
					}
				}
			}
		}
	}
}
