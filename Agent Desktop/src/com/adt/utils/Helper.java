package com.adt.utils;

import java.util.TimeZone;

import android.text.Editable;

public class Helper
{
	public String convertMS(long ms)
	{
		return ((((ms / 1000) / 60) / 60) % 24) + ":" + (((ms / 1000) / 60) % 60) + ":" + ((ms / 1000) % 60);
	}

	/**
	 * Convert the millisecond to string in hh:mm:ss format
	 */
	public String convertMSFormated(long ms)
	{
		int seconds = (int) ((ms / 1000) % 60);
		int minutes = (int) (((ms / 1000) / 60) % 60);
		int hours = (int) ((((ms / 1000) / 60) / 60) % 24);
		String sec = String.valueOf(seconds);
		if (sec.length() < 2)
		{
			sec = "0" + sec;
		}
		String min = String.valueOf(minutes);
		if (min.length() < 2)
		{
			min = "0" + min;
		}
		String h = String.valueOf(hours);
		if (h.length() < 2)
		{
			h = "0" + h;
		}
		// if(hours == 0) return (((ms / 1000) / 60) % 60) + ":" + ((ms / 1000)
		// % 60) + " mm:ss";
		return h + ":" + min + ":" + sec;
		// return ((((ms / 1000) / 60) / 60) % 24) + ":" + (((ms / 1000) / 60) %
		// 60) + ":" + ((ms / 1000) % 60) + " hh:mm:ss";
	}

	/**
	 * Convert the millisecond to string in hh:mm:ss format Check if the number
	 * of days are more then one then add hours instead of day
	 */
	public String msToHMS(long ms)
	{
		int seconds = (int) ((ms / 1000) % 60);
		int minutes = (int) (((ms / 1000) / 60) % 60);
		int hours = (int) ((((ms / 1000) / 60) / 60) % 24);
		int diffDays = (int) ms / (24 * 60 * 60 * 1000);

		if (diffDays >= 1)
		{
			hours += diffDays * 24;
		}
		String sec = String.valueOf(seconds);
		if (sec.length() < 2)
		{
			sec = "0" + sec;
		}
		String min = String.valueOf(minutes);
		if (min.length() < 2)
		{
			min = "0" + min;
		}
		String h = String.valueOf(hours);
		if (h.length() < 2)
		{
			h = "0" + h;
		}
		// if(hours == 0) return (((ms / 1000) / 60) % 60) + ":" + ((ms / 1000)
		// % 60) + " mm:ss";
		return h + ":" + min + ":" + sec;
		// return ((((ms / 1000) / 60) / 60) % 24) + ":" + (((ms / 1000) / 60) %
		// 60) + ":" + ((ms / 1000) % 60) + " hh:mm:ss";
	}

	public static int getTimeOffset(long time)
	{
		TimeZone tz = TimeZone.getDefault();
		return tz.getOffset(time);
	}

	public static float getFloatFromEditable(Editable editable)
	{
		float result;
		String s = String.valueOf(editable);
		if (s == null || s.trim().equals(""))
		{
			result = 0;
		}
		else
		{
			try
			{
				result = Float.valueOf(s);
			}
			catch (Exception e)
			{
				Utils.println(e.getMessage());
				result = 0;
			}
		}
		return result;
	}
}