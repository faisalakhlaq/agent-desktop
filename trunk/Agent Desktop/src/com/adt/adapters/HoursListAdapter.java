package com.adt.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adt.app.R;
import com.adt.model.WorkHours;
import com.adt.utils.Helper;

public class HoursListAdapter extends ArrayAdapter<WorkHours>
{
	private Context context;

	private ArrayList<WorkHours> hoursList = null;

	static class ViewHolder
	{
		public TextView title;

		public TextView totalHours;
	}

	public HoursListAdapter(Context context, ArrayList<WorkHours> list)
	{
		super(context, R.layout.hours_item, list);
		this.context = context;
		if (list != null && list.size() > 0) hoursList = list;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = convertView;
		if (rowView == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			rowView = inflater.inflate(R.layout.hours_item, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) rowView.findViewById(R.id.job_name_tv);
			viewHolder.totalHours = (TextView) rowView.findViewById(R.id.total_hours_tv);
			rowView.setTag(viewHolder);
		}
		if (hoursList != null && hoursList.size() > position)
		{
			WorkHours hour = hoursList.get(position);
			ViewHolder holder = (ViewHolder) rowView.getTag();
			holder.title.setText(hour.getJobTitle());
			long totalHours = hour.getTotalHours();
			// if (totalHours == 0) totalHours = (new Date()).getTime() -
			// hour.getCheckInTime(); // FIXME
			Helper helper = new Helper();
			holder.totalHours.setText(helper.convertMSFormated(totalHours));
		}
		return rowView;
	}
}
