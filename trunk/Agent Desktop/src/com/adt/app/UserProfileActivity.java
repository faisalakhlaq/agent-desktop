package com.adt.app;

import com.adt.database.ADTDBHelper;
import com.adt.model.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class UserProfileActivity extends Activity implements OnClickListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);

		Button submitBtn = (Button) findViewById(R.id.profile_submit_btn);
		submitBtn.setOnClickListener(this);
		
		ImageButton homeBtn = (ImageButton) findViewById(R.id.header_home);
		homeBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		EditText fn = (EditText) findViewById(R.id.profile_fname_txt);
		EditText ln = (EditText) findViewById(R.id.profile_lname_txt);
		EditText e = (EditText) findViewById(R.id.profile_email_txt);
		EditText a = (EditText) findViewById(R.id.profile_address_txt);
		
		CharSequence msg = "Welcome!";
		String fName = String.valueOf(fn.getText());
		
		if (v.getId() == R.id.header_home)
		{
			Intent i = new Intent(this, ADT.class);
			startActivity(i);
		}
		
		if(fName == null || fName.trim().equals(""))
		{
			// TODO not an efficient way
			msg = "First name cannot be empty";
			Toast.makeText(UserProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
		}
		else
		{
			User u = new User(fName, String.valueOf(ln.getText()), String.valueOf(e.getText()), String.valueOf(a.getText()));
			ADTDBHelper db = new ADTDBHelper(UserProfileActivity.this); 
			// TODO create a parent class and put the protected db in that
			if(db.insertUser(u))
				Toast.makeText(UserProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
		}
	}

}
