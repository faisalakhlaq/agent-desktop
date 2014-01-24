package utils;

import android.text.Editable;

public class Helper
{
	public String convertMS(long ms)
	{
		// int seconds = (int) ((ms / 1000) % 60);
		// int minutes = (int) (((ms / 1000) / 60) % 60);
		// int hours = (int) ((((ms / 1000) / 60) / 60) % 24);

		// if(hours == 0) return (((ms / 1000) / 60) % 60) + ":" + ((ms / 1000)
		// % 60) + " mm:ss";
		return ((((ms / 1000) / 60) / 60) % 24) + ":" + (((ms / 1000) / 60) % 60) + ":" + ((ms / 1000) % 60);
//		return ((((ms / 1000) / 60) / 60) % 24) + ":" + (((ms / 1000) / 60) % 60) + ":" + ((ms / 1000) % 60) + " hh:mm:ss";
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
