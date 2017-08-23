package com.example.hi.ariz.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.hi.ariz.MainActivity;
import com.example.hi.ariz.model.Album;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by HI on 18-Jul-17.
 */

public class AppInfoInPreference
{

	/**
	 * Purpose to Fetch login Details of User from Preference
	 */
	public static ArrayList<Album> getFromPrefAlbumList(Context context)
	{
		try
		{
			SharedPreferences appPref = context.getSharedPreferences("PrerferenceAlbumList", Context.MODE_PRIVATE);
			ArrayList<Album> object = (ArrayList<Album>) ObjectSerializer.deserialize(appPref.getString("album_list", ""));
			return object;
		}
		catch (Exception e)
		{
			Log.e("album_list", "Error while fetching album list from pref");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Purpose to Save Profile Details of User to Preference
	 */
	public static void saveToPrefAlbumList(ArrayList<Album> obj, Context context)
	{
		try
		{
			SharedPreferences appPref = context.getSharedPreferences("PrerferenceAlbumList", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = appPref.edit();
			editor.putString("album_list", ObjectSerializer.serialize(obj));
			editor.commit();

		}
		catch (Exception e)
		{
			Log.e("album_list", "Failed To Save album list Information");
			e.printStackTrace();

		}
	}


}
