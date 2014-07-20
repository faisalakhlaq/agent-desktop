package com.adt.model;

public class User
{
	private String fName = null;

	private String lName = null;

	private String address = null;

	/* To be used to for sending mail */
	private String email = null;

	/* Ask the user is s/he want to share the check-in check-out on facebook */
	private boolean shareCheckInOut = false;

	/* if yes then share automatically or after asking the user */
	private boolean shareAutomatically = false;

	/* Do you want to share your locations when you check-in */
	private boolean shareLocation = false;

	/*
	 * Ask the user if S/he wants to save the deleted work hours for one week?
	 */
	private boolean keepDeleted = false;

	public User(String fName, String lName, String address, String email, boolean shareCheckInOut, boolean shareAutomatically, boolean shareLocation, boolean keepDeleted)
	{
		this.fName = fName;
		this.lName = lName;
		this.address = address;
		this.email = email;
		this.shareCheckInOut = shareCheckInOut;
		this.shareAutomatically = shareAutomatically;
		this.shareLocation = shareLocation;
		this.keepDeleted = keepDeleted;
	}

	public User(String fName, String lName, String address, String email)
	{
		this.fName = fName;
		this.lName = lName;
		this.address = address;
		this.email = email;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public boolean isShareCheckInOut()
	{
		return shareCheckInOut;
	}

	public void setShareCheckInOut(boolean shareCheckInOut)
	{
		this.shareCheckInOut = shareCheckInOut;
	}

	public boolean isShareAutomatically()
	{
		return shareAutomatically;
	}

	public void setShareAutomatically(boolean shareAutomatically)
	{
		this.shareAutomatically = shareAutomatically;
	}

	public boolean isShareLocation()
	{
		return shareLocation;
	}

	public void setShareLocation(boolean shareLocation)
	{
		this.shareLocation = shareLocation;
	}

	public boolean isKeepDeleted()
	{
		return keepDeleted;
	}

	public void setKeepDeleted(boolean keepDeleted)
	{
		this.keepDeleted = keepDeleted;
	}

	public String getfName()
	{
		return fName;
	}

	public void setfName(String fName)
	{
		this.fName = fName;
	}

	public String getlName()
	{
		return lName;
	}

	public void setlName(String lName)
	{
		this.lName = lName;
	}
}