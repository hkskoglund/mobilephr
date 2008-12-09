package hks.itprojects.healthcollector.PHR;

import java.util.Vector;

public class Wound {
	
	public Wound(String name, String location, java.util.Date date, Vector thumbnails)
	{
		this.name = name;
		this.location = location;
	    this.setThumbnails(thumbnails);
	    this.setDate(date);
	}
	
	
	public Wound()
		{
			// TODO Auto-generated constructor stub
		}


	private String name;
	private String location;
	private Vector thumbnails;
	private String id;
	private java.util.Date date;
	private boolean hasWoundContainer = false;
	
	public void setId(String id)
		{
				this.id = id;
		}
	public String getId()
		{
				return id;
		}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocation() {
		return location;
	}
	
	public void setThumbnails(Vector thumbnails)
		{
				this.thumbnails = thumbnails;
		}
	
	public Vector getThumbnails()
		{
				return thumbnails;
		}
	public void setDate(java.util.Date date)
		{
				this.date = date;
		}
	public java.util.Date getDate()
		{
				return date;
		}
	
	public String toString(){
		if (date != null)
		   return date.toString();
		else
		   return this.name;
		
	}


	public void setHasWoundContainer(boolean hasWoundContainer)
		{
				this.hasWoundContainer = hasWoundContainer;
		}


	public boolean hasWoundContainer()
		{
				return hasWoundContainer;
		}

}

