package com.adt.database;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.adt.model.Job;
import com.adt.model.User;
import com.adt.model.WorkHours;
import com.adt.utils.Helper;
import com.adt.utils.Utils;

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
			// String query = "CREATE TABLE IF NOT EXISTS " + JOBS_TABLE_NAME +
			// " (" + "title TEXT PRIMARY KEY, " + "company TEXT, " +
			// "hourly_wage FLOAT, "
			// + "creation_date LONG, active BOOLEAN DEFAULT 'FALSE');";
			String query = "CREATE TABLE IF NOT EXISTS " + JOBS_TABLE_NAME + " (" + "title TEXT PRIMARY KEY, " + "company TEXT, " + "address TEXT, "
					+ "hourly_wage FLOAT, description TEXT," + "creation_date LONG, active BOOLEAN DEFAULT 'FALSE');";
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
	public boolean insertCheckInHours(WorkHours h)
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

	public boolean insertCheckOutHours(WorkHours h)
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
		boolean inserted = true;
		/*
		 * "CREATE TABLE IF NOT EXISTS " + JOBS_TABLE_NAME + " (" +
		 * "title TEXT PRIMARY KEY, " + "company TEXT, " + "address TEXT, " +
		 * "hourly_wage FLOAT, description TEXT," +
		 * "creation_date LONG, active BOOLEAN DEFAULT 'FALSE');";
		 */
		try
		{
			String query = "INSERT INTO jobs(title, company, address, hourly_wage, description, creation_date) VALUES  ('" + job.getTitle() + "','" + job.getCompanyName() + "','"
					+ job.getAddress() + "'," + job.getHourlyWages() + ",'" + job.getDescription() + "','" + job.getCreationDateInMillies() + "')";
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
	 * Update the job / task details.
	 * <p>
	 * if the job title is updated then update task name in Hours table
	 * 
	 * @param title
	 *            of the task to be changed,
	 *            <p>
	 *            Job containing the new values
	 * */
	public boolean updateJob(String title, Job j)
	{
		boolean updated = true;
		if (isActiveJob(title))
		{
			return false;
		}
		// "title TEXT PRIMARY KEY, " + "company TEXT, " +
		// "hourly_wage FLOAT, "
		// + "creation_date LONG, active BOOLEAN DEFAULT 'TRUE');";
		String query = "UPDATE jobs set title = '" + j.getTitle() + "', company = '" + j.getCompanyName() + "', description = '" + j.getDescription() + "'," + " address = '"
				+ j.getAddress() + "', hourly_wage = " + j.getHourlyWages() + " WHERE title = '" + title + "';";

		try
		{
			openDataBase();
			adtDB.execSQL(query);
			if (!title.equals(j.getTitle()))
			{
				query = "UPDATE hours set job_title = '" + j.getTitle() + "' where job_title = '" + title + "';";
				adtDB.execSQL(query);
			}
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
	 * Check if the given job is active (user has checked in for this job)
	 */
	private boolean isActiveJob(String title)
	{
		boolean active = false;

		Cursor cursor = null;
		try
		{
			openDataBase();
			String isActiveQuery = "Select active from jobs where title LIKE '" + title + "';";
			cursor = adtDB.rawQuery(isActiveQuery, null);

			while (cursor.moveToNext())
			{
				if ("true".equalsIgnoreCase(cursor.getString(0))) active = true;
			}
		}
		catch (Exception e)
		{
			Utils.println(e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null) cursor.close();
			close();
		}

		return active;
	}

	/**
	 * Task is deleted from the database after checking if the user has not
	 * checked-in for the job/task
	 * <p>
	 * Entries corresponding to this task in the hours table are also deleted
	 */
	public boolean deleteJob(String title)
	{
		boolean remove = true;
		String query = "DELETE FROM jobs where title LIKE '" + title + "';";
		try
		{
			if (!isActiveJob(title))
			{
				openDataBase();
				adtDB.execSQL(query);
				query = "DELETE FROM hours where job_title = '" + title + "';";
				adtDB.execSQL(query);
			}
			else
			{
				remove = false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Utils.println(e.getMessage());
			remove = false;
		}
		finally
		{
			close();
		}
		return remove;
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
	 * Retrieve the job/task name for which user has checked in currently
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

	public ArrayList<Job> getAllJobs()
	{
		ArrayList<Job> list = null;
		Cursor cursor = null;
		try
		{
			String query = "select * from jobs";
			openDataBase();
			cursor = adtDB.rawQuery(query, null);
			list = new ArrayList<Job>(cursor.getCount());

			/*
			 * "CREATE TABLE IF NOT EXISTS " + JOBS_TABLE_NAME + " (" +
			 * "title TEXT PRIMARY KEY, " + "company TEXT, " + "address TEXT, "
			 * + "hourly_wage FLOAT, description TEXT," +
			 * "creation_date LONG, active BOOLEAN DEFAULT 'FALSE');";
			 */
			while (cursor.moveToNext())
			{
				Job j = new Job();
				j.setTitle(cursor.getString(0));
				j.setOrganizationName(cursor.getString(1));
				j.setAddress(cursor.getString(2));
				j.setHourlyWages(cursor.getInt(3));
				j.setDescription(cursor.getString(4));
				j.setCreationDate(new Date(Long.parseLong(cursor.getString(5))));
				j.setActive("TRUE".equalsIgnoreCase(cursor.getString(6)));
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
	public ArrayList<WorkHours> getHours()
	{
		ArrayList<WorkHours> result = null;
		ArrayList<String> jobNames = getAllJobNames();
		if (jobNames == null) return result;
		int noOfJobs = jobNames.size();
		result = new ArrayList<WorkHours>(noOfJobs);

		for (int i = 0; i < noOfJobs; i++)
		{
			WorkHours h = getTotalTaskHours(jobNames.get(i));
			result.add(h);
		}
		return result;
	}

	/**
	 * Get the details of hours.
	 * <p>
	 * Date, Checkin and Checkout time
	 * **/
	public ArrayList<WorkHours> getHoursDescription(String jobTitle)
	{
		ArrayList<WorkHours> list = null;
		Cursor cursor = null;
		try
		{
			String query = "select * from hours where job_title LIKE '" + jobTitle + "';";
			openDataBase();
			cursor = adtDB.rawQuery(query, null); // TODO check if the cursor
													// can be null?
			list = new ArrayList<WorkHours>(cursor.getCount());

			while (cursor.moveToNext())
			{
				WorkHours h = new WorkHours();
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
	private WorkHours getTotalTaskHours(String taskName)
	{
		WorkHours hours = new WorkHours();
		hours.setJobTitle(taskName);
		Cursor cursor = null;
		try
		{
			// TODO Get the sum of total hours from the table using the SUM
			// query
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
					long time = Calendar.getInstance().getTimeInMillis();
					time += Helper.getTimeOffset(time);
					totalHours = (new Date(time)).getTime() - (new Date(checkInTime)).getTime(); 
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

	public boolean deleteHours(WorkHours hour)
	{
		boolean removed = true;
		String query = "delete from hours where job_title LIKE '" + hour.getJobTitle() + "' and check_in_time = " + hour.getCheckInTime() + ";";
		String checkOutQuery = "Update jobs set active = 'FALSE' where title LIKE '" + hour.getJobTitle() + "';";
		try
		{
			openDataBase();
			if (hour.getCheckOutTime() == 0)
			{
				// IF the check-out time for this WorkHour == 0 then it means
				// this
				// job is still checked-in and is active. The active WorkHours
				// are being deleted and the job wont remain active any longer.
				// Therefore, we have to set the job as inactive.
				adtDB.execSQL(checkOutQuery);
			}
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

	/**
	 * Retrieve the working hours within the date range
	 */
	public ArrayList<WorkHours> getHours(Date fromDate, Date toDate)
	{
		// return getHours(fromDate, toDate, null);
		Calendar c = Calendar.getInstance();
		c.setTime(fromDate);
		long from = Long.valueOf(fromDate.getTime());

		c.setTime(toDate);
		long to = Long.valueOf(toDate.getTime());

		String query = "select * from hours where check_in_time >= " + from + " and check_out_time <= " + to + ";";
		return getWorkHours(query);
	}

	/**
	 * Retrieve the working hours according to the taskName
	 */
	public ArrayList<WorkHours> getHours(String taskName)
	{
		String query = "select * from hours where job_title = '" + taskName + "' ;";
		return getWorkHours(query);
	}

	/**
	 * Retrieve working hours from the database according to task-name and
	 * between the date ranges
	 */
	public ArrayList<WorkHours> getHours(Date fromDate, Date toDate, String taskName)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(fromDate);
		long from = Long.valueOf(fromDate.getTime());

		c.setTime(toDate);
		long to = Long.valueOf(toDate.getTime());

		String query = "select * from hours where job_title = '" + taskName + "' and check_in_time >= " + from + " and check_out_time <= " + to + ";";
		return getWorkHours(query);
	}

	/**
	 * Retrieve the working hours using the provided query
	 */
	private ArrayList<WorkHours> getWorkHours(String query)
	{
		ArrayList<WorkHours> list = null;
		Cursor cursor = null;
		try
		{
			openDataBase();
			cursor = adtDB.rawQuery(query, null); // TODO check if the cursor
													// can be null?
			list = new ArrayList<WorkHours>(cursor.getCount());

			while (cursor.moveToNext())
			{
				WorkHours h = new WorkHours();
				h.setJobTitle(cursor.getString(0));
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
}