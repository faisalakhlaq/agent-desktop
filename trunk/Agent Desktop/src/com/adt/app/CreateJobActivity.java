package com.adt.app;

import model.Job;
import utils.Helper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import database.ADTDBHelper;

public class CreateJobActivity extends Activity implements OnClickListener
{
	// TODO make the description buttton 3 lines hight
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
			// TODO Auto-generated method stub
			EditText t = (EditText) findViewById(R.id.create_task_title_txt);
			EditText c = (EditText) findViewById(R.id.create_task_org_name_txt);
			EditText a = (EditText) findViewById(R.id.create_task_address_txt);
			EditText d = (EditText) findViewById(R.id.create_task_desc_txt1);
			EditText w = (EditText) findViewById(R.id.create_task_hourly_wage_txt);

			String title = String.valueOf(t.getText());
			CharSequence msg = "Job created successfully";
			if (title == null || title.trim().equals(""))
			{ // TODO not an efficient way
				msg = "Job title cannot be empty";
				Toast.makeText(CreateJobActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
			else
			{
				// TODO create a parent class and put the protected db in
				// that
				// float hourlyWage;
				// String s = String.valueOf(w.getText());
				// if (s == null || s.trim().equals(""))
				// {
				// hourlyWage = 0;
				// }
				// else
				// {
				// try
				// {
				// hourlyWage = Float.valueOf(s);
				// }
				// catch (Exception e)
				// {
				// Utils.println(e.getMessage());
				// hourlyWage = 0;
				// }
				// }
				Job j = new Job(title, String.valueOf(c.getText()), String.valueOf(a.getText()), String.valueOf(d.getText()), Helper.getFloatFromEditable(w.getText()));
				ADTDBHelper db = new ADTDBHelper(CreateJobActivity.this);
				if (db.insertJob(j)) Toast.makeText(CreateJobActivity.this, msg, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

}
