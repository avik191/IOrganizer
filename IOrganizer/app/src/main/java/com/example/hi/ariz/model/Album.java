package com.example.hi.ariz.model;

import java.io.Serializable;

/**
 * Created by HI on 18-Jul-17.
 */

public class Album implements Serializable
{
	String albumName;
	String date;

	public String getAlbumName()
	{
		return albumName;
	}

	public void setAlbumName(String albumName)
	{
		this.albumName = albumName;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

}
