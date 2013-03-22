package utils;

import android.text.Editable;

public class Helper
{
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
