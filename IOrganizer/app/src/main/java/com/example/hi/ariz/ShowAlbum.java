package com.example.hi.ariz;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hi.ariz.adapter.DBAdapter;
import com.example.hi.ariz.adapter.MyCustomAdapter;
import com.example.hi.ariz.adapter.PicsCustomAdapter;
import com.example.hi.ariz.model.Album;
import com.example.hi.ariz.model.Pictures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ShowAlbum extends AppCompatActivity
{
	DBAdapter db;
	RecyclerView recycle;
	//ListView recycle;
	PicsCustomAdapter adapter;
	TextView emptyTv;
	String albumNAme;
	ArrayList<Pictures> pictureList;
	SharedPreferences app_preferences;
	SharedPreferences.Editor editor;
	int CAMERA_PIC_REQUEST=1;
	int picName=0;
	String albumPicName="";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_album);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		recycle= (RecyclerView) findViewById(R.id.recycleerview2);
		emptyTv= (TextView) findViewById(R.id.noAlbumPics);

		Bundle bundle=getIntent().getExtras();
		albumNAme=bundle.getString("albumName");
		app_preferences = PreferenceManager.getDefaultSharedPreferences(this);


		ActionBar actionBar=getSupportActionBar();
		actionBar.setTitle(Html.fromHtml("<font color = '#FFFFFF'>"+albumNAme+"</font>"));

		db=new DBAdapter(ShowAlbum.this);
		pictureList=new ArrayList<>();
		getPictureList();

		if(pictureList.size()>0)
		{
			emptyTv.setVisibility(View.GONE);
			recycle.setVisibility(View.VISIBLE);
			adapter=new PicsCustomAdapter(this,pictureList);
			recycle.setLayoutManager(new GridLayoutManager(ShowAlbum.this,2));
			recycle.setAdapter(adapter);
		}

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabcamera);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{

				takePicture();
			}
		});
	}

	private void takePicture()
	{
		if ((ContextCompat.checkSelfPermission(ShowAlbum.this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(ShowAlbum.this,
			Manifest.permission.CAMERA)
			!= PackageManager.PERMISSION_GRANTED)) {

				ActivityCompat.requestPermissions(ShowAlbum.this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},10);

		}
		//+Avik - 23-JUNE-2017 - VM#913 - Setting permission to true

		else
		{
			picName      = app_preferences.getInt("pic_number", 0);
			picName = picName+1;
			editor = app_preferences.edit();
			editor.putInt("pic_number", picName);
			editor.commit();



			String folderPath = Environment.getExternalStorageDirectory() + "/iOrganizer_Pictures/"+albumNAme;
			File folder = new File(folderPath);
			if (!folder.exists()) {
				File wallpaperDirectory = new File(folderPath);
				wallpaperDirectory.mkdirs();
			}
			String name=albumNAme+"_"+picName+".jpg";
			albumPicName=albumNAme+"_"+picName;
			//create a new file
			File newFile = new File(folderPath,name);
			String authorities=getApplicationContext().getPackageName()+".fileprovider";

			if (newFile != null) {

				Uri relativePath;
				if (Build.VERSION.SDK_INT <= 19) {
					// Call some material design APIs here
					relativePath = Uri.fromFile(newFile);
				} else {
					// Implement this feature without material design
					relativePath = FileProvider.getUriForFile(ShowAlbum.this,authorities,newFile);
				}
				// save image here
				//Uri relativePath = Uri.fromFile(newFile);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, relativePath);
				startActivityForResult(intent, CAMERA_PIC_REQUEST);
			}

			//Toast.makeText(ShowAlbum.this,picName+" ",Toast.LENGTH_LONG).show();
		}
	}

	public void getPictureList()
	{
		db.open();
		Cursor c = db.getPicsInAlbum(albumNAme);
		if(c!=null) {
			if (c.moveToFirst()) {
				do {
					Pictures ob = new Pictures();
					ob.setpictureId(c.getInt(0));
					ob.setAlbumName(c.getString(1));
					ob.setPicName(c.getString(2));
					pictureList.add(ob);

				} while (c.moveToNext());
			}
			db.close();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == CAMERA_PIC_REQUEST)
		{
			if (resultCode == RESULT_OK) {
				emptyTv.setVisibility(View.GONE);
				recycle.setVisibility(View.VISIBLE);
				adapter=new PicsCustomAdapter(this,pictureList);
				recycle.setLayoutManager(new GridLayoutManager(ShowAlbum.this,2));
				recycle.setAdapter(adapter);
				savePicInDataBase();

			} else if (resultCode == RESULT_CANCELED) {

				Toast.makeText(ShowAlbum.this,
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(ShowAlbum.this,
						"Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void savePicInDataBase()
	{
		Pictures p=new Pictures();
		db.open();
		if(db.insertPic(albumNAme,albumPicName)>0)
		{
			Cursor c = db.getPic(albumPicName);
			if(c!=null) {
				if (c.moveToFirst()) {
					do {
						p.setpictureId(c.getInt(0));
						break;
					} while (c.moveToNext());
				}
			}

			Toast.makeText(ShowAlbum.this,"Picture Saved",Toast.LENGTH_SHORT).show();

			p.setAlbumName(albumNAme);
			p.setPicName(albumPicName);
			pictureList.add(p);

			adapter.addAlbum(pictureList);
		}
		db.close();

	}


}
