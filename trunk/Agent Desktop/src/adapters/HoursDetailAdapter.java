package adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import model.Hours;
import utils.Helper;
import utils.Notifier;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adt.app.R;

public class HoursDetailAdapter extends ArrayAdapter<Hours>
{
	private Notifier notifier;

	private Context context;

	private ArrayList<Hours> hoursList = null;

	static class ViewHolder
	{
		public TextView checkIn;

		public TextView checkOut;

		public TextView date;

		public TextView duration;
	}

	public HoursDetailAdapter(Context context, ArrayList<Hours> list)
	{
		super(context, R.layout.hours_detail_item_test, list);
		this.context = context;
		if (list != null && list.size() > 0) hoursList = list;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = convertView;
		if (rowView == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
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
			final Hours hour = hoursList.get(position);
			ViewHolder holder = (ViewHolder) rowView.getTag();
			Helper helper = new Helper();
			holder.checkIn.setText(helper.convertMS(hour.getCheckInTime()));
			holder.checkOut.setText(helper.convertMS(hour.getCheckOutTime()));
			holder.date.setText((new Date(hour.getCheckInTime())).toString());
			holder.date.setText((new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(hour.getCheckInTime())));
			holder.duration.setText(helper.convertMS(hour.getTotalHours()));
			// TODO if total working hours = 0 then calculate

			ImageButton deleteIcon = (ImageButton) rowView.findViewById(R.id.hours_detail_item_delete_icon);
			final ImageButton deletebtn = (ImageButton) rowView.findViewById(R.id.hours_detail_item_delete_button);

			if (hour.isEditMode())
			{
				deleteIcon.setVisibility(View.VISIBLE);
				deleteIcon.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						deletebtn.setVisibility(View.VISIBLE);
						deletebtn.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								deletebtn.setVisibility(View.GONE);
								notifier.notify(hour, "DEL");
							}
						});
					}
				});
			}
			else
			{
				deletebtn.setVisibility(View.GONE);
				deleteIcon.setVisibility(View.GONE);
			}
		}
		return rowView;
	}

	public void setNotifier(Notifier notifier)
	{
		this.notifier = notifier;
	}

}
