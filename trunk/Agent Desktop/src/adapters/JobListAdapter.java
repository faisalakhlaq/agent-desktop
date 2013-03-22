package adapters;

import java.util.ArrayList;

import model.Job;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adt.app.R;

public class JobListAdapter extends ArrayAdapter<Job>
{
	private Context context;

	private ArrayList<Job> taskList = null;

	static class ViewHolder
	{
		public TextView title;

		public TextView companyName;

		public TextView duties;
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
			viewHolder.title = (TextView) rowView.findViewById(R.id.task_item_task_title_txtview);
			viewHolder.companyName = (TextView) rowView.findViewById(R.id.task_item_company_txtview);
			viewHolder.duties = (TextView) rowView.findViewById(R.id.task_item_duties_txtview);
			rowView.setTag(viewHolder);
		}
		if (taskList != null && taskList.size() > position)
		{
			Job task = taskList.get(position);
			ViewHolder holder = (ViewHolder) rowView.getTag();
			holder.title.setText(task.getTitle());
			holder.companyName.setText(task.getOrganizationName());
			holder.duties.setText(task.getDuties());
		}
		return rowView;
	}
}
