package com.example.hi.ariz;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hi.ariz.adapter.MyCustomAdapter;
import com.example.hi.ariz.model.Album;
import com.example.hi.ariz.util.AppInfoInPreference;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.example.hi.ariz.R.id.okButton;

public class MainActivity extends AppCompatActivity
{
	RecyclerView recycle;
	ArrayList<Album> albumList;
	MyCustomAdapter adapter;
	TextView emptyTv;
	SharedPreferences app_preferences;
	SharedPreferences.Editor editor;
	int firstTime=0;
	SearchView searchView;
	public static boolean storagePermission=false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar=getSupportActionBar();
		actionBar.setTitle(Html.fromHtml("<font color = '#FFFFFF'>iOrganizer</font>"));

		recycle= (RecyclerView) findViewById(R.id.recycleerview);
		emptyTv= (TextView) findViewById(R.id.noAlbums);

		app_preferences = PreferenceManager.getDefaultSharedPreferences(this);

		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(800);
					new Handler(getMainLooper()).post(new Runnable()
					{
						@Override
						public void run()
						{
							checkTapTarget();
						}
					});
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		});
		thread.start();

		albumList= AppInfoInPreference.getFromPrefAlbumList(MainActivity.this);
		if(albumList != null)
		{
			if(albumList.size()>0)
			{
				emptyTv.setVisibility(View.GONE);
				recycle.setVisibility(View.VISIBLE);
				adapter=new MyCustomAdapter(this,albumList);
				recycle.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
				recycle.setAdapter(adapter);
			}
			else
				albumList=new ArrayList<>();
		}
		else
			albumList=new ArrayList<>();





		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if(firstTime != 0)
					showAlbumCreateDialog();
			}
		});
	}


	private void showAlbumCreateDialog()
	{
		final Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				final Dialog dialog = new Dialog(MainActivity.this);
				dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.custom_dialog_box);
				dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
				dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
				dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
				dialog.setCancelable(true);
				dialog.show();
				final TextView titleTV = (TextView) dialog.findViewById(R.id.titleTV);
				titleTV.setText("Album Name");
				final EditText albumNameEt = (EditText) dialog.findViewById(R.id.albumNameEt);
				final TextView okButton = (TextView) dialog.findViewById(R.id.okButton);
				okButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if(albumNameEt.getText().toString().equals(""))
						{

							Snackbar.make(v,"enter name first", Snackbar.LENGTH_LONG)
									.setAction("Action", null).show();
						}
						else
						{
							Toast.makeText(MainActivity.this,albumNameEt.getText().toString(),Toast.LENGTH_SHORT).show();
							Album album=new Album();
							album.setAlbumName(albumNameEt.getText().toString());

							Calendar c = Calendar.getInstance();
							SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
							String formattedDate = df.format(c.getTime());

							album.setDate(formattedDate);
							albumList.add(album);

							if(albumList.size()==1)
							{
								emptyTv.setVisibility(View.GONE);
								recycle.setVisibility(View.VISIBLE);
								adapter=new MyCustomAdapter(MainActivity.this,albumList);
								recycle.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
								recycle.setAdapter(adapter);
							}
							else
								adapter.addAlbum(albumList);

							AppInfoInPreference.saveToPrefAlbumList(albumList,MainActivity.this);
						}

						dialog.dismiss();

					}
				});
			}
		};
		new Handler(getMainLooper()).post(r);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		searchView = (SearchView) menu.findItem(R.id.searchbtn).getActionView();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(String query)
			{
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText)
			{
				if(adapter != null)
				{
					newText = newText.toLowerCase();
					ArrayList<Album> newList = new ArrayList<>();
					for (Album album : albumList)
					{
						if (album.getAlbumName().contains(newText))
							newList.add(album);
					}
					adapter.filterList(newList);
				}
				return false;
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.exit)
		{
			finish();
			return true;
		}
		if( id == R.id.about)
		{
			final Runnable r = new Runnable()
			{
				@Override
				public void run()
				{
					final Dialog dialog = new Dialog(MainActivity.this);
					dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.about_dialog_box);
					dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
					dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
					dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
					dialog.setCancelable(true);
					dialog.show();
					Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
					okBtn.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();

						}
					});
				}
			};
			new Handler(getMainLooper()).post(r);

		}

		return super.onOptionsItemSelected(item);
	}

	private void checkTapTarget()
	{
		 firstTime = app_preferences.getInt("first_time", 0);
		if(firstTime == 0)
		{
			showTapTargetAlbumView();
			editor = app_preferences.edit();
			editor.putInt("first_time", 1);
			editor.commit();

		}
		
	}

	private void showTapTargetAlbumView()
	{
		MaterialTapTargetPrompt prompt;
		prompt = new MaterialTapTargetPrompt.Builder(MainActivity.this)
				.setTarget(findViewById(R.id.fab))
				.setPrimaryText("Create your first Album")
				.setSecondaryText("Tap the fab button to create your first album")
				.setBackgroundColour(Color.parseColor("#FFB300"))
				.setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
				{
					@Override
					public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
					{
						if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_DISMISSED )
						{
							// User has pressed the prompt target
							prompt.finish();
							prompt = null;
							showTapTargetSearchView();
						}
					}
				})
				.show();
	}

	private void showTapTargetSearchView()
	{
		MaterialTapTargetPrompt prompt;
		prompt = new MaterialTapTargetPrompt.Builder(MainActivity.this)
				.setTarget(findViewById(R.id.searchbtn))
				.setPrimaryText("Search Albums")
				.setSecondaryText("Tap the search icon to search albums")
				.setBackgroundColour(Color.parseColor("#FFB300"))
				.setFocalColour(Color.BLACK)
				.setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
				{
					@Override
					public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
					{
						if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED  || state == MaterialTapTargetPrompt.STATE_DISMISSED )
						{
							// User has pressed the prompt target
							prompt.finish();
							prompt = null;
							firstTime=1;

						}
					}
				})
				.show();
	}
}
