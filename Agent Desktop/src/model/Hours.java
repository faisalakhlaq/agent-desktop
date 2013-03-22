package model;

public class Hours
{
	private String jobTitle = null;

	private long checkInTime = 0;

	private long checkOutTime = 0;

	private long totalHours = 0;

	public Hours()
	{
	}

	public Hours(String jobTitle, long checkInTime, long checkOutTime, long totalHours)
	{
		this.jobTitle = jobTitle;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.totalHours = totalHours;
	}

	public Hours(String jobTitle, long checkInTime, long checkOutTime)
	{
		this.jobTitle = jobTitle;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
	}

	public Hours(String jobTitle, long checkInTime)
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

	public void setCheckOutTime(int checkOutTime)
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

}