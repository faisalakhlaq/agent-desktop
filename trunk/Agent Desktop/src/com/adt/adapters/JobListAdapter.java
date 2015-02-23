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
import com.adt.model.Job;

public class JobListAdapter extends ArrayAdapter<Job>
{
	private Context context;

	private ArrayList<Job> taskList = null;

	static class ViewHolder
	{
		public TextView title;

		public TextView companyName;

		public TextView description;
	}

	public JobListAdapter(Context context, ArrayList<Job> list)
	{
		super(context, R.layout.task_item, list);
		this.context = context;
		if (list != null && list.size() > 0) taskList = list;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = convertView;
		if (rowView == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			rowView = inflater.inflate(R.layout.task_item, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) rowView.findViewById(R.id.ti_task_title_tv);
			viewHolder.companyName = (TextView) rowView.findViewById(R.id.ti_company_tv);
			viewHolder.description = (TextView) rowView.findViewById(R.id.ti_description_tv);
			rowView.setTag(viewHolder);
		}
		if (taskList != null && taskList.size() > position)
		{
			Job task = taskList.get(position);
			ViewHolder holder = (ViewHolder) rowView.getTag();
			holder.title.setText(task.getTitle());
			holder.companyName.setText(task.getCompanyName());
			holder.description.setText(task.getDescription());
		}
		return rowView;
	}
}
