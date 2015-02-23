package com.adt.app;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.adt.database.ADTDBHelper;
import com.adt.model.Job;
import com.adt.utils.Helper;
import com.adt.utils.Utils;

public class JobActivity extends Activity implements OnClickListener
{
	private Job job = null;

	private EditText orgName = null;

	private EditText title = null;

	private EditText wages = null;

	private EditText date = null; // TODO implement a solution for editing date

	private EditText description = null;

	private EditText address = null;

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
			populateTextFields(job);
			enableTextfileds(false);
			addListenersToButtons();
		}
	}

	private void initTextFields()
	{
		orgName = (EditText) findViewById(R.id.task_org_name_txt);
		title = (EditText) findViewById(R.id.task_title_txt);
		wages = (EditText) findViewById(R.id.task_hourly_wage_txt);
		date = (EditText) findViewById(R.id.task_created_on_txt);
		description = (EditText) findViewById(R.id.task_desc_txt);
		address = (EditText) findViewById(R.id.task_address_txt);
	}

	/**
	 * Enable / Disable text fields
	 */
	private void enableTextfileds(boolean enable)
	{
		orgName.setEnabled(enable);
		title.setEnabled(enable);
		wages.setEnabled(enable);
		description.setEnabled(enable);
		address.setEnabled(enable);
		date.setEnabled(false);
	}

	private void populateTextFields(Job task)
	{
		orgName.setText(task.getCompanyName());
		title.setText(task.getTitle());
		wages.setText(String.valueOf(task.getHourlyWages()));
		date.setText((new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(task.getCreationDate())));
		description.setText(String.valueOf(task.getDescription()));
		address.setText(String.valueOf(task.getAddress()));
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
		switch (v.getId())
		{
		case R.id.header_home:
		{
			Intent i = new Intent(this, ADT.class);
			startActivity(i);
			break;
		}
		case R.id.task_delete_btn:
		{
			deleteTask();
			break;
		}
		case R.id.task_edit_btn:
		{
			editTask();
			break;
		}

		}
	}

	private void editTask()
	{
		if (edit.getText().equals("Edit"))
		{
			enableTextfileds(true);
			edit.setText("Update");
		}
		else
		{
			String t = String.valueOf(title.getText()).trim();
			if (t.equals(""))
			{
				Utils u = new Utils();
				u.showMessage("Error", "Sorry  title cannot be empty", this).run();
			}
			else
			{
				Job j = new Job();
				j.setTitle(t);
				j.setOrganizationName(String.valueOf(orgName.getText()));
				j.setAddress(String.valueOf(address.getText()));
				j.setHourlyWages(Helper.getFloatFromEditable(wages.getText()));
				j.setDescription(String.valueOf(description.getText()));
				j.setCreationDate(job.getCreationDate());
				ADTDBHelper db = new ADTDBHelper(JobActivity.this);
				if (db.updateJob(job.getTitle(), j))
				{
					CharSequence msg = "Job - Task updated";
					Toast.makeText(JobActivity.this, msg, Toast.LENGTH_SHORT).show();
					edit.setText("Edit");
					enableTextfileds(false);
					Intent i = new Intent(this, JobListActivity.class);
					startActivity(i);
				}
				else
				{
					Utils u = new Utils();
					u.showMessage("Error", "Unable to update the job. Make sure you have not checked-in for this task", this).run();
					edit.setText("Edit");
					populateTextFields(job);
					enableTextfileds(false);
				}
			}
		}
	}

	private void deleteTask()
	{
		ADTDBHelper db = new ADTDBHelper(JobActivity.this);
		if (db.deleteJob(job.getTitle()))
		{
			CharSequence msg = "Job - Task deleted";
			Toast.makeText(JobActivity.this, msg, Toast.LENGTH_SHORT).show();
			Intent i = new Intent(this, JobListActivity.class);
			startActivity(i);
		}
		else
		{
			Utils u = new Utils();
			u.showMessage("Sorry Deletion Unsuccessfull", "Make sure you have not checked-in for this job", this).run();
		}
	}
}