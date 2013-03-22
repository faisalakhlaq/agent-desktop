package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;

public class Utils
{
	private static boolean log = true;

	private AlertDialog.Builder alrtdialog;

	public static void println(Object str)
	{
		if (log)
			System.out.println(str);
	}

	public Runnable showMessage(final String title, final String messag, final Context context)
	{
		Runnable populate = new Runnable()
		{
			public void run()
			{
				alrtdialog = new AlertDialog.Builder(context);
				alrtdialog.setTitle(title);
				alrtdialog.setMessage(messag);
				alrtdialog.setCancelable(false).setNeutralButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						dialog = null;
					}
				});
				alrtdialog.show();
			}
		};
		return populate;
	}

	public static void errorLogToFile(String name)
	{
		final String logName = name;
		Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread thread, Throwable ex)
			{
				PrintWriter pw;
				try
				{
					Utils.println("ERROR is loged At " + Environment.getExternalStorageDirectory() + "/" + logName + " ERROR.log");
					FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/" + logName + " Error.log", true);
					pw = new PrintWriter(fw);
					ex.printStackTrace(pw);
					pw.flush();
					pw.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public String formatStringFordb(String str)
	{
		do
		{
			if (str.contains("\'"))
			{
				str = str.replace("\'", "");
			}
		}
		while (str.contains("\'"));
		return str;
	}

}