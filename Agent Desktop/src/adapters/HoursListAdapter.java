package adapters;

import java.util.ArrayList;
import java.util.Date;

import model.Hours;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adt.app.R;

public class HoursListAdapter extends ArrayAdapter<Hours>
{
	private Context context;

	private ArrayList<Hours> hoursList = null;

	static class ViewHolder
	{
		public TextView title;

		public TextView totalHours;
	}

	public HoursListAdapter(Context context, ArrayList<Hours> list)
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
			Hours hour = hoursList.get(position);
			ViewHolder holder = (ViewHolder) rowView.getTag();
			holder.title.setText(hour.getJobTitle());
			long totalHours = hour.getTotalHours();
			if (totalHours == 0) totalHours = (new Date()).getTime() - hour.getCheckInTime();
			holder.totalHours.setText(convertMS(totalHours));
		}
		return rowView;
	}

	public String convertMS(long ms)
	{
		// int seconds = (int) ((ms / 1000) % 60);
		// int minutes = (int) (((ms / 1000) / 60) % 60);
		// int hours = (int) ((((ms / 1000) / 60) / 60) % 24);

		// if(hours == 0) return (((ms / 1000) / 60) % 60) + ":" + ((ms / 1000)
		// % 60) + " mm:ss";
		return ((((ms / 1000) / 60) / 60) % 24) + ":" + (((ms / 1000) / 60) % 60) + ":" + ((ms / 1000) % 60) + " hh:mm:ss";
	}
}