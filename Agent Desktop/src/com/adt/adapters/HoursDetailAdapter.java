package com.adt.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adt.app.R;
import com.adt.model.WorkHours;
import com.adt.utils.DeleteBtnVisibilityListener;
import com.adt.utils.Helper;
import com.adt.utils.Notifier;

public class HoursDetailAdapter extends ArrayAdapter<WorkHours>
{
	private Notifier notifier;

	private DeleteBtnVisibilityListener delBtnListener = null;

	private Context context;

	private ArrayList<WorkHours> hoursList = null;

	static class ViewHolder
	{
		public TextView checkIn;

		public TextView checkOut;

		public TextView date;

		public TextView duration;
	}

	public HoursDetailAdapter(Context context, ArrayList<WorkHours> list)
	{
		super(context, R.layout.hours_detail_item_test, list);
		this.context = context;
		if (list != null && list.size() > 0) hoursList = list;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = convertView;
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		final View row = rowView;
		if (rowView == null)
		{
			rowView = inflater.inflate(R.layout.hours_detail_item_test, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.checkIn = (TextView) rowView.findViewById(R.id.hours_detail_item_checkin_lbl);
			viewHolder.checkOut = (TextView) rowView.findViewById(R.id.hours_detail_item_checkout_lbl);
			viewHolder.date = (TextView) rowView.findViewById(R.id.hours_detail_item_date_lbl);
			viewHolder.duration = (TextView) rowView.findViewById(R.id.hours_detail_item_duration_lbl);
			rowView.setTag(viewHolder);
		}
		if (hoursList != null && hoursList.size() > position)
		{
			final WorkHours hour = hoursList.get(position);
			LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.hdi_linear_layout);
			if ((hoursList.indexOf(hour) % 2) == 0)
			{
				layout.setBackgroundColor(Color.parseColor("#006600"));
			}
			ViewHolder holder = (ViewHolder) rowView.getTag();
			Helper helper = new Helper();
			holder.checkIn.setText(helper.convertMSFormated(hour.getCheckInTime()));
			holder.checkOut.setText(helper.convertMSFormated(hour.getCheckOutTime()));
			// holder.date.setText((new
			// Date(hour.getCheckInTime())).toString());
			holder.date.setText((new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(hour.getCheckInTime())));
			holder.duration.setText(helper.convertMSFormated(hour.getTotalHours()));
			if (hour.getCheckOutTime() == 0)
			{
				// if check out time == 0 then the total working hours = 0.
				// Means the user is still working on this task. Therefore,
				// calculate the duration from checkin time upto the current
				// time
				long time = Calendar.getInstance().getTimeInMillis();
				time += Helper.getTimeOffset(time);

				Date in = new Date(hour.getCheckInTime());
				Date out = new Date(time);
				holder.duration.setText(helper.convertMSFormated(out.getTime() - in.getTime()));

				// indicate that the user is still checked in by writing checked
				// in under the checkout time
				holder.checkOut.setText("Checked in");
			}

			ImageButton deleteIcon = (ImageButton) rowView.findViewById(R.id.hours_detail_item_delete_icon);
			final ImageButton deleteBtn = (ImageButton) rowView.findViewById(R.id.hours_detail_item_delete_button);

			if (hour.isEditMode())
			{
				deleteIcon.setVisibility(View.VISIBLE);
				deleteIcon.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if (deleteBtn.isShown())
						{
							deleteBtn.setVisibility(View.GONE);
							delBtnListener.setVisibleBtnRow(null);
						}
						else
						{
							delBtnListener.setDelBtnVisible(false);
							delBtnListener.setVisibleBtnRow(row);
							deleteBtn.setVisibility(View.VISIBLE);
							deleteBtn.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									delBtnListener.setDelBtnVisible(false);
									// deleteBtn.setVisibility(View.GONE);
									notifier.notify(hour, "DEL");
								}
							});
						}
					}
				});
			}
			else
			{
				deleteBtn.setVisibility(View.GONE);
				// delBtnListener.setDelBtnVisible(false);
				deleteIcon.setVisibility(View.GONE);
			}
		}
		return rowView;
	}

	public void setNotifier(Notifier notifier)
	{
		this.notifier = notifier;
	}

	public void setDeleteBtnVisibilityListener(DeleteBtnVisibilityListener l)
	{
		delBtnListener = l;
	}

}