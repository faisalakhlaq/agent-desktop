package com.adt.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.adt.adapters.HoursDetailAdapter;
import com.adt.database.ADTDBHelper;
import com.adt.model.WorkHours;
import com.adt.utils.DeleteBtnVisibilityListener;
import com.adt.utils.Helper;
import com.adt.utils.Notifier;
import com.adt.utils.Utils;

public class HoursDetailActivity extends Activity implements OnClickListener, Notifier, DeleteBtnVisibilityListener, OnItemClickListener
{
	private HoursDetailAdapter adapter;

	private View visibileDelBtnRow = null;

	private ArrayList<WorkHours> hoursList;

	private ListView listView;

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

		listView = (ListView) findViewById(R.id.list);
		// listView = getListView();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

		addButtonListeners();
	}

	private void addButtonListeners()
	{
		ImageButton homeBtn = (ImageButton) findViewById(R.id.adt_header_home);
		homeBtn.setOnClickListener(this);

		ImageButton editBtn = (ImageButton) findViewById(R.id.adt_header_edit_btn);
		editBtn.setOnClickListener(this);

		ImageButton doneBtn = (ImageButton) findViewById(R.id.header_done_btn);
		doneBtn.setOnClickListener(this);

		ImageButton menuBtn = (ImageButton) findViewById(R.id.menu);
		menuBtn.setOnClickListener(this);
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
		else if (v.getId() == R.id.menu)
		{
			showDialog();
			// TODO show the dialog displaying the menu
		}
		else if (v.getId() == R.id.adt_header_edit_btn)
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
		else if (v.getId() == R.id.header_done_btn)
		{
			// TODO check if the hours are shown for a specific
			// time and task then retrieve the hours for the time not the entire
			// hours
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if (listView != null)
		{
			HoursDetailAdapter adapter = (HoursDetailAdapter) parent.getAdapter();
			WorkHours workHours = (WorkHours) adapter.getItem(position);
			if (!workHours.isEditMode())
			{
				Intent i = new Intent(view.getContext(), EditHours.class);
				i.putExtra("HOURS", workHours);
				startActivity(i);
			}
		}
	}

	private void showDialog()
	{
		final Dialog dialog = new Dialog(this);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sms_email_menu);

		Button smsBtn = (Button) dialog.findViewById(R.id.sms_btn);
		smsBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO start sms
				dialog.dismiss();
				sendSms();
			}
		});
		Button emailBtn = (Button) dialog.findViewById(R.id.email_btn);
		emailBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
				sendEmail();
			}
		});
		dialog.show();
	}

	private void sendEmail()
	{
		String message = getSMSBody();
		if (message == null)
		{
			Utils u = new Utils();
			u.showMessage("Sorry", "No hours to send SMS", HoursDetailActivity.this).run();
		}
		else
		{
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setData(Uri.parse("mailto:"));
			// emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
			// emailIntent.putExtra(Intent.EXTRA_CC, cc);
			// emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
			emailIntent.putExtra(Intent.EXTRA_TEXT, message);
			emailIntent.setType("message/rfc822");
			startActivity(Intent.createChooser(emailIntent, "Email"));
		}
	}

	private void sendSms()
	{
		String messageBody = getSMSBody();
		if (messageBody == null)
		{
			Utils u = new Utils();
			u.showMessage("Sorry", "No hours to send SMS", HoursDetailActivity.this).run();
		}
		else
		{
			Intent i = new Intent(android.content.Intent.ACTION_VIEW);
			messageBody += "Job: " + i.putExtra("sms_body", messageBody);
			i.setType("vnd.android-dir/mms-sms");
			startActivity(i);
		}
	}

	private String getSMSBody()
	{
		String messageBody = new String();
		if (hoursList != null && hoursList.size() > 0)
		{
			String title = "";
			Helper helper = new Helper();
			for (WorkHours h : hoursList)
			{
				if (!title.equals(h.getJobTitle()))
				{
					title = h.getJobTitle();
					messageBody += "Title: " + h.getJobTitle() + "\n\n";
				}
				String date = (new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(h.getCheckInTime()));
				messageBody += "Date: " + date + "\n" + "Start: " + helper.convertMSFormated(h.getCheckInTime()) + "\n" + "End: " + helper.convertMSFormated(h.getCheckOutTime())
						+ "\n" + "Duration: " + helper.msToHMS(h.getTotalHours()) + "\n\n";
			}
		}
		else
		{
			messageBody = null;
		}
		return messageBody;
	}
}