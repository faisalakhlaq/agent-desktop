package database;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import model.Hours;
import model.Job;
import model.User;
import utils.Utils;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class ADTDBHelper extends SQLiteOpenHelper
{
	private final static int DB_VERSION = 1;

	private final static String DB_NAME = "adtdatabase.db";

	private final static String DB_PATH = "/data/data/com.adt.app/databases/";

	// TODO if this is used only in one place then remove it
	private final static String JOBS_TABLE_NAME = "jobs";

	private final static String USERS_TABLE_NAME = "users";

	private final static String HOURS_TABLE_NAME = "hours";

	private SQLiteDatabase adtDB;

	private Context context;

	public ADTDBHelper(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		try
		{
			createDataBase();
		}
		catch (Exception e)
		{
			Utils.println(e.getMessage());
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO create index on company name column
		// create int primany keys for user and hour table
		// add if not exists to create queries when the tables are finalized
		try
		{
			String query = "CREATE TABLE IF NOT EXISTS " + JOBS_TABLE_NAME + " (" + "title TEXT PRIMARY KEY, " + "company TEXT, " + "hourly_wage FLOAT, "
					+ "creation_date LONG, active BOOLEAN DEFAULT 'FALSE');";
			db.execSQL(query);
			db.execSQL("CREATE INDEX jobs_company_idx ON jobs(company);");
			query = "CREATE TABLE IF NOT EXISTS  " + USERS_TABLE_NAME + " (first_name TEXT, last_name TEXT," + "email TEXT," + "admin INTEGER);";
			db.execSQL(query);
			query = "CREATE TABLE IF NOT EXISTS " + HOURS_TABLE_NAME + " (job_title TEXT, check_in_time LONG, check_out_time LONG, "
					+ "total_hours LONG, active BOOLEAN DEFAULT 'FALSE');";
			db.execSQL(query);
		}
		catch (Exception e)
		{
			Utils.println(e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		onCreate(db);
	}

	public boolean insertUser(User u)
	{
		boolean inserted = true;
		String query = "INSERT INTO users(first_name, last_name, email, admin) VALUES ('" + u.getfName() + "','" + u.getlName() + "','" + u.getEmail() + "'," + 1 + ")";
		try
		{
			openDataBase();
			adtDB.execSQL(query);
		}
		catch (Exception e)
		{
			Utils.println(e.getMessage());
			inserted = false;
		}
		finally
		{
			if (adtDB != null) adtDB.close();
		}
		return inserted;
	}

	// TODO check if the user has not already checked in for this task
	public boolean insertCheckInHours(Hours h)
	{
		boolean inserted = true;
		// inserting only task and check-in time and the table will be updated
		// on checkout
		String query = "INSERT INTO hours(job_title, check_in_time, active) VALUES ('" + h.getJobTitle() + "', " + h.getCheckInTime() + ", 'TRUE');";
		String updateSQL = "Update jobs set active = 'TRUE' where title LIKE '" + h.getJobTitle() + "';";
		try
		{
			openDataBase();
			adtDB.execSQL(query);
			adtDB.execSQL(updateSQL);
		}
		catch (Exception e)
		{
			Utils.println(e.getMessage());
			inserted = false;
		}
		finally
		{
			if (adtDB != null) adtDB.close();
		}
		return inserted;
	}

	public boolean insertCheckOutHours(Hours h)
	{
		boolean updated = true;
		Cursor cursor = null;

		try
		{
			openDataBase();
			String jobTitle = h.getJobTitle();
			// String query =
			// "SELECT check_in_time from hours where job_title = '" + jobTitle
			// + "' and check_out_time is null;";
			String query = "SELECT check_in_time from hours where job_title = '" + jobTitle + "' and active LIKE 'TRUE';";
			cursor = adtDB.rawQuery(query, null);
			long checkInTime = 0;
			while (cursor.moveToNext())
				checkInTime = cursor.getLong(0);

			// calculate total work hours
			long checkOutTime = h.getCheckOutTime();
			Date in = new Date(checkInTime);
			Date out = new Date(checkOutTime);
			// job_title TEXT, check_in_time LONG, check_out_time LONG,
			// " + "total_hours LONG
			query = "UPDATE hours set check_out_time = " + checkOutTime + ", total_hours = " + (out.getTime() - in.getTime()) + ", active = 'FALSE' WHERE job_title = '"
					+ h.getJobTitle() + "' and check_in_time = " + checkInTime + ";";
			// query = "UPDATE hours set check_out_time = " + checkOutTime +
			// ", total_hours = " + (out.getTime() - in.getTime()) +
			// " WHERE job_title = '" + h.getJobTitle()
			// + "' and check_out_time is NULL";
			adtDB.execSQL(query);
			query = "Update jobs set active = 'FALSE' where title LIKE '" + jobTitle + "';";
			adtDB.execSQL(query);
		}
		catch (Exception e)
		{
			Utils.println(e.getMessage());
			updated = false;
		}
		finally
		{
			if (cursor != null) cursor.close();
			adtDB.close();
		}
		return updated;
	}

	public boolean insertJob(Job job)
	{
		boolean inserted = true; // TODO remove this temp variable for saving
									// memory and to avoid GC
		try
		{
			String query = "INSERT INTO jobs(title, company, hourly_wage, creation_date) VALUES  ('" + job.getTitle() + "','" + job.getOrganizationName() + "',"
					+ job.getHourlyWages() + ",'" + job.getCreationDateInMillies() + "')";
			openDataBase();
			adtDB.execSQL(query);
		}
		catch (Exception e)
		{
			Utils.println(e.getMessage());
			inserted = false;
		}
		finally
		{
			close();
		}
		return inserted;
	}

	/**
	 * @param title
	 *            of the task to be changed,
	 *            <p>
	 *            Job containing the new values
	 * */
	public boolean updateJob(String title, Job j)
	{
		boolean updated = true; // TODO check if this variable is unnecessary
		// "title TEXT PRIMARY KEY, " + "company TEXT, " +
		// "hourly_wage FLOAT, "
		// + "creation_date LONG, active BOOLEAN DEFAULT 'TRUE');";
		String query = "UPDATE jobs set title = '" + j.getTitle() + "', company = '" + j.getOrganizationName() + "'," + "hourly_wage = " + j.getHourlyWages() + " WHERE title = '"
				+ title + "';";

		try
		{
			openDataBase();
			adtDB.execSQL(query);
		}
		catch (Exception e)
		{
			e.getStackTrace();
			updated = false;
		}
		finally
		{
			close();
		}
		return updated;
	}

	/**
	 * Provide the Job-Task title and it will be deleted from the database
	 */
	public boolean removeJob(String title)
	{
		boolean removed = true;
		String query = "DELETE FROM jobs where title LIKE '" + title + "';";
		try
		{
			openDataBase();
			adtDB.execSQL(query);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			removed = false;
		}
		finally
		{
			close();
		}
		return removed;
	}

	private ArrayList<String> getJobNames(String query)
	{
		ArrayList<String> list = null;
		Cursor cursor = null;
		try
		{
			openDataBase();
			cursor = adtDB.rawQuery(query, null);
			list = new ArrayList<String>(cursor.getCount());

			while (cursor.moveToNext())
				list.add(cursor.getString(0));
		}
		catch (Exception e)
		{
			Utils.println("get Job Names ERROR = " + e);
		}
		finally
		{
			if (cursor != null) cursor.close();
			close();
		}
		return list;
	}

	/**
	 * Get the job/task name for which user has checked in currently
	 * */
	public ArrayList<String> getActiveJobNames()
	{
		String sql = "select title from jobs where active LIKE 'TRUE'";
		return getJobNames(sql);
	}

	/**
	 * Get the job/task name for which user has not checked in currently
	 * */
	public ArrayList<String> getInActiveJobNames()
	{
		String sql = "select title from jobs where active LIKE 'FALSE'";
		return getJobNames(sql);
	}

	/**
	 * get the names of all jobs active and inactive
	 */
	public ArrayList<String> getAllJobNames()
	{
		String sql = "select title from jobs";
		return getJobNames(sql);
	}

	// TODO remove this method
	public ArrayList<Job> getAllJobs()
	{
		ArrayList<Job> list = null;
		Cursor cursor = null;
		try
		{
			String query = "select * from jobs";
			openDataBase();
			cursor = adtDB.rawQuery(query, null); // TODO check if the cursor
													// can be null?
			list = new ArrayList<Job>(cursor.getCount());

			while (cursor.moveToNext())
			{
				Job j = new Job();
				j.setTitle(cursor.getString(0));
				j.setOrganizationName(cursor.getString(1));
				j.setHourlyWages(cursor.getInt(2));
				j.setCreationDate(new Date(Long.parseLong(cursor.getString(3))));
				list.add(j);
			}
		}
		catch (Exception e)
		{
			Utils.println("getSearchResults ERROR = " + e);
		}
		finally
		{
			if (cursor != null) cursor.close();
			close();
		}
		return list;
	}

	/**
	 * Get the task name and total hours
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Hours> getHours()
	{
		ArrayList<Hours> result = null;
		ArrayList<String> jobNames = getAllJobNames();
		if (jobNames == null) return result;
		int noOfJobs = jobNames.size();
		result = new ArrayList<Hours>(noOfJobs);

		for (int i = 0; i < noOfJobs; i++)
		{
			Hours h = getTotalTaskHours(jobNames.get(i));
			result.add(h);
		}
		return result;
	}

	/**
	 * Get the details of hours.
	 * <p>
	 * Date, Checkin and Checkout time
	 * **/
	public ArrayList<Hours> getHoursDescription(String jobTitle)
	{
		ArrayList<Hours> list = null;
		Cursor cursor = null;
		try
		{
			String query = "select * from hours where job_title LIKE '" + jobTitle + "';";
			openDataBase();
			cursor = adtDB.rawQuery(query, null); // TODO check if the cursor
													// can be null?
			list = new ArrayList<Hours>(cursor.getCount());

			while (cursor.moveToNext())
			{
				Hours h = new Hours();
				h.setJobTitle(jobTitle);
				h.setCheckInTime(cursor.getLong(1));
				h.setCheckOutTime(cursor.getLong(2));
				h.setTotalHours(cursor.getLong(3));
				list.add(h);
			}
		}
		catch (Exception e)
		{
			Utils.println("getSearchResults ERROR = " + e);
		}
		finally
		{
			if (cursor != null) cursor.close();
			close();
		}
		return list;
	}

	/**
	 * Get the total time spent on a task
	 * */
	private Hours getTotalTaskHours(String taskName)
	{
		Hours hours = new Hours();
		hours.setJobTitle(taskName);
		Cursor cursor = null;
		try
		{
			openDataBase();
			cursor = adtDB.rawQuery("Select * from hours where job_title LIKE '" + taskName + "';", null);

			while (cursor.moveToNext())
			{
				long checkInTime = cursor.getLong(1);
				long totalHours = cursor.getLong(3);
				boolean active = cursor.getString(4).equalsIgnoreCase("TRUE");
				/*
				 * if the total hours is zero and user has checked in for the
				 * task it means he is still working on the task. In this case
				 * total time wont be available. Therefore, we have to calculate
				 * total time.
				 */
				if (totalHours == 0 && checkInTime != 0 && active)
				{
					totalHours = (new Date()).getTime() - (new Date(checkInTime)).getTime(); // FIXME
				}
				hours.setTotalHours(hours.getTotalHours() + totalHours);
			}
		}
		catch (Exception e)
		{
			Utils.println(e.getMessage());
		}
		finally
		{
			if (cursor != null) cursor.close();
			close();
		}
		return hours;
	}

	public void createDataBase() throws IOException
	{
		boolean dbExist = checkDataBase();
		if (!dbExist)
		{
			this.getReadableDatabase();
			try
			{
				copyDataBase();
			}
			catch (Exception e)
			{
				throw new Error("Error copying database");
			}
		}
	}

	private boolean checkDataBase()
	{
		SQLiteDatabase checkDB = null;
		String myPath = DB_PATH + DB_NAME;
		try
		{
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		}
		catch (SQLiteException e)
		{
			copyDataBase();
			Utils.println("DataBase Can not be accessed therefore new DataBase will be copied");
		}
		if (checkDB != null)
		{
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	private boolean copyDataBase()
	{
		try
		{
			Utils.println("copyDataBase()");
			InputStream myInput = context.getAssets().open(DB_NAME);
			String outFileName = DB_PATH + DB_NAME;
			OutputStream myOutput = new FileOutputStream(outFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0)
			{
				myOutput.write(buffer, 0, length);
			}
			myOutput.flush();
			myOutput.close();
			myInput.close();
		}
		catch (FileNotFoundException e)
		{
			Utils.println("DataBase NotFound ERROR = " + e);
		}
		catch (Exception e)
		{
			Utils.println("DataBase Copy ERROR = " + e);
		}
		return true;
	}

	public void openDataBase() throws SQLException
	{
		// TODO check if the database if already open then return the previous
		// opened database
		String myPath = DB_PATH + DB_NAME;
		try
		{
			adtDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		}
		catch (Exception e)
		{
			Utils.println("Database Open ERROR = " + e);
		}
	}

	public void close()
	{
		try
		{
			if (adtDB != null)
			{
				adtDB.close();
				super.close();
			}
		}
		catch (Exception e)
		{
			Utils.println("Error while closing the dataBase = " + e);
		}
	}

	public boolean deleteHours(Hours hour)
	{
		boolean removed = true;
		String query = "delete from hours where job_title LIKE '" + hour.getJobTitle() + "' and check_in_time = " + hour.getCheckInTime() + ";";
		try
		{
			openDataBase();
			adtDB.execSQL(query);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			removed = false;
		}
		finally
		{
			close();
		}
		return removed;
	}
}
