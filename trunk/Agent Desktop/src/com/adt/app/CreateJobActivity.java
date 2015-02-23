package com.adt.app;

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

public class CreateJobActivity extends Activity implements OnClickListener
{
	// TODO make the description button 3 lines height
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_task);

		Button createTaskBtn = (Button) findViewById(R.id.create_task_create_btn);
		createTaskBtn.setOnClickListener(this);

		ImageButton homeBtn = (ImageButton) findViewById(R.id.header_home);
		homeBtn.setOnClickListener(this);
	}

	@Override
	public void onBackPressed()
	{
		// do something on back??????
		return;
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.header_home)
		{
			Intent i = new Intent(this, ADT.class);
			startActivity(i);
		}
		else if (v.getId() == R.id.create_task_create_btn)
		{
			EditText t = (EditText) findViewById(R.id.create_task_title_txt);
			EditText c = (EditText) findViewById(R.id.create_task_org_name_txt);
			EditText a = (EditText) findViewById(R.id.create_task_address_txt);
			EditText d = (EditText) findViewById(R.id.create_task_desc_txt);
			EditText w = (EditText) findViewById(R.id.create_task_hourly_wage_txt);

			String title = String.valueOf(t.getText());
			if (title == null || title.trim().equals(""))
			{
				Utils u = new Utils();
				u.showMessage("Error", "Job title cannot be empty", this).run();
			}
			else
			{
				Job j = new Job();
				j.setTitle(title);
				j.setOrganizationName(String.valueOf(c.getText()));
				j.setAddress(String.valueOf(a.getText()));
				j.setDescription(String.valueOf(d.getText()));
				j.setHourlyWages(Helper.getFloatFromEditable(w.getText()));

				ADTDBHelper db = new ADTDBHelper(CreateJobActivity.this);
				CharSequence msg = "Job created successfully";
				if (db.insertJob(j))
				{
					Toast.makeText(CreateJobActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				else
				{
					Utils u = new Utils();
					u.showMessage("Error", "Unable to create the job", this).run();
				}
				finish();
			}
		}
	}
}