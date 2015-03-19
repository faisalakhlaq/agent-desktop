package com.adt.model;

import java.io.Serializable;

public class WorkHours implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean editMode = false;

	private String jobTitle = null;

	private long checkInTime = 0;

	private long checkOutTime = 0;

	private long totalHours = 0;

	private String stringDuration;

	public WorkHours()
	{
	}

	public WorkHours(String jobTitle, long checkInTime, long checkOutTime, long totalHours)
	{
		this.jobTitle = jobTitle;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.totalHours = totalHours;
	}

	public WorkHours(String jobTitle, long checkInTime, long checkOutTime)
	{
		this.jobTitle = jobTitle;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
	}

	public WorkHours(String jobTitle, long checkInTime)
	{
		this.jobTitle = jobTitle;
		this.checkInTime = checkInTime;
	}

	public String getJobTitle()
	{
		return jobTitle;
	}

	public void setJobTitle(String jobTitle)
	{
		this.jobTitle = jobTitle;
	}

	public long getCheckInTime()
	{
		return checkInTime;
	}

	public void setCheckInTime(long checkInTime)
	{
		this.checkInTime = checkInTime;
	}

	public long getCheckOutTime()
	{
		return checkOutTime;
	}

	public void setCheckOutTime(long checkOutTime)
	{
		this.checkOutTime = checkOutTime;
	}

	public long getTotalHours()
	{
		return totalHours;
	}

	public void setTotalHours(long totalHours)
	{
		this.totalHours = totalHours;
	}

	public void setEditMode(boolean editMode_)
	{
		editMode = editMode_;
	}

	public boolean isEditMode()
	{
		return editMode;
	}

	public String getStringDuration()
	{
		return stringDuration;
	}

	public void setStringDuration(String stringDuration)
	{
		this.stringDuration = stringDuration;
	}
}
