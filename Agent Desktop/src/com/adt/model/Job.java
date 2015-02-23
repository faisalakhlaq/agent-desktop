package com.adt.model;

import java.io.Serializable;
import java.util.Date;

public class Job implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String title = null;

	private String organizationName = null;

	private String address = null;

	private String duties = null;

	private String description = null;

	private float hourlyWages = 0;

	private Date creationDate = null;

	private boolean active = false;

	public Job()
	{
		creationDate = new Date();
	}

	public Job(String jobTitle, String orgName, String orgAddress, String jobDuties, float hourlyWage)
	{ // TODO wrong implementation, just assign the values
		String t = jobTitle == null ? "Job witout title" : jobTitle;
		if (t.trim().equals("")) t = "Job witout title";
		this.title = t;

		t = orgName == null ? "---" : orgName;
		if (t.trim().equals("")) t = "--";
		this.organizationName = t;

		t = orgAddress == null ? "---" : orgAddress;
		if (t.trim().equals("")) t = "--";
		this.address = t;

		t = jobDuties == null ? "---" : jobDuties;
		if (t.trim().equals("")) t = "--";
		this.duties = t;

		this.hourlyWages = hourlyWage;
		creationDate = new Date();
	}

	public Date getCreationDate()
	{
		return creationDate;
	}

	public long getCreationDateInMillies()
	{
		return Long.valueOf(creationDate.getTime());
	}

	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getCompanyName()
	{
		return organizationName;
	}

	public void setOrganizationName(String organizationName)
	{
		this.organizationName = organizationName;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getDuties()
	{
		return duties;
	}

	public void setDuties(String duties)
	{
		this.duties = duties;
	}

	public float getHourlyWages()
	{
		return hourlyWages;
	}

	public void setHourlyWages(float hourlyWages)
	{
		this.hourlyWages = hourlyWages;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

}
