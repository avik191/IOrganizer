package com.example.hi.ariz.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hi.ariz.MainActivity;
import com.example.hi.ariz.R;
import com.example.hi.ariz.ShowAlbum;
import com.example.hi.ariz.model.Album;
import com.example.hi.ariz.model.Pictures;
import com.example.hi.ariz.util.AppInfoInPreference;

import java.io.File;
import java.util.ArrayList;

import static android.os.Looper.getMainLooper;

/**
 * Created by HI on 18-July-17.
 */
public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.myviewholder> {

    Context context;
    ArrayList<Album> list;
    LayoutInflater inflator;
    int prepos=0;
	DBAdapter db;

	public MyCustomAdapter(Context context, ArrayList<Album> list) {

        this.context=context;
        this.list=list;
		db=new DBAdapter(context);
		inflator=LayoutInflater.from(context);
    }

    @Override
    public myviewholder onCreateViewHolder(ViewGroup parent, int position) {
        View v=inflator.inflate(R.layout.row_item,parent,false);//this view will contain appearance of each layout i.e each row..
        myviewholder holder=new myviewholder(v);// we are passing the view of each row to the myviewholder class
        return holder;
    }

    @Override
    public void onBindViewHolder(myviewholder holder, int position) {//here we will inflate datas in the widgets i.e image and title..
        //It is called for each row..so every row is inflated here..
       Album album=list.get(position);
        holder.albumName.setText(album.getAlbumName());
        holder.albumDate.setText(album.getDate());
	    String coverName="";
	    ////////////////////
	    db.open();
	    Cursor c = db.getPicsInAlbum(album.getAlbumName());
	    if(c!=null) {
		    if (c.moveToFirst()) {
			    do {
				    coverName=c.getString(2);
				    break;
			    } while (c.moveToNext());
		    }
		    db.close();
	    }

	    if(coverName.equals(""))
		    holder.imageview.setBackgroundResource(R.drawable.pic);
		else
		    holder.imageview.setImageBitmap(getPreview(Environment.getExternalStorageDirectory() + "/iOrganizer_Pictures/"+album.getAlbumName()+"/"+coverName+".jpg"));

	    ///////////////////
    }

	Bitmap getPreview(String uri) {
		File image = new File(uri);

		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image.getPath(), bounds);
		if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
			return null;

		int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
				: bounds.outWidth;

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = originalSize / 300;
		return BitmapFactory.decodeFile(image.getPath(), opts);
	}

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
    {
	    // It contains the elements in each row that we will inflate in the recyclerView..
	    TextView albumName, albumDate;
	    ImageView imageview;

	    public myviewholder(View itemView)
	    {
		    super(itemView);
		    albumName = (TextView) itemView.findViewById(R.id.row_albumName);//here we link with the elements of each view i.e each row and
		    albumDate = (TextView) itemView.findViewById(R.id.row_albumDate);//here we link with the elements of each view i.e each row and
		    imageview = (ImageView) itemView.findViewById(R.id.row_albumImage);// finally in onbind method we put the datas in each view..
	        itemView.setOnClickListener(this);
		    itemView.setOnLongClickListener(this);
	    }

	    @Override
	    public void onClick(View view)
	    {
		    int pos = getAdapterPosition();
		   // Toast.makeText(context,pos+" ",Toast.LENGTH_SHORT).show();
		    String albumName=list.get(pos).getAlbumName();
		    Intent i=new Intent(context,ShowAlbum.class);
		    Bundle b=new Bundle();
		    b.putString("albumName",albumName);
		    i.putExtras(b);
		    context.startActivity(i);
	    }

	    @Override
	    public boolean onLongClick(View view)
	    {
		    final int pos = getAdapterPosition();
		    final String albumName=list.get(pos).getAlbumName();
		    final CharSequence[] items = {"Delete folder"};

		    AlertDialog.Builder builder = new AlertDialog.Builder(context);
		    builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {

				    if(item==0)
				    {
					    showConfirmDeleteDialog(albumName,pos);
				    }
				    //Toast.makeText(context, items[item], Toast.LENGTH_SHORT).show();
			    }
		    });
		    AlertDialog alert = builder.create();
		    alert.show();

		    return true;
	    }
    }

	private void showConfirmDeleteDialog(final String albumName,final int pos)
	{
		final Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				final Dialog dialog = new Dialog(context);
				dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.confirm_delete_dialog);
				dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
				dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
				dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
				dialog.setCancelable(true);
				dialog.show();
				final TextView titleTV = (TextView) dialog.findViewById(R.id.titleTV);
				titleTV.setText("Confirm Delete?");
				final TextView picNameTV = (TextView) dialog.findViewById(R.id.picNameTV);
				picNameTV.setText(albumName);
				final TextView yesButton = (TextView) dialog.findViewById(R.id.yesButton);
				yesButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{

						File path=new File(Environment.getExternalStorageDirectory() + "/iOrganizer_Pictures/"+albumName);
						if( path.exists() ) {
							File[] files = path.listFiles();
							for(int i=0; i<files.length; i++) {
									files[i].delete();
							}
							path.delete();
							list.remove(pos);
							notifyDataSetChanged();
							Toast.makeText(context, "folder deleted", Toast.LENGTH_SHORT).show();

							AppInfoInPreference.saveToPrefAlbumList(list,context);

						}
						dialog.dismiss();
					}
				});

				final TextView noButton = (TextView) dialog.findViewById(R.id.nobtn);
				noButton.setOnClickListener(new View.OnClickListener()
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

    public void addAlbum(ArrayList<Album> albumList)
    {
	    ArrayList<Album> newList=new ArrayList<>();
	    newList.addAll(albumList);
	    list=newList;
	    notifyDataSetChanged();
    }

    public void filterList(ArrayList<Album> albumList)
    {
	    ArrayList<Album> newList=new ArrayList<>();
	    newList.addAll(albumList);
	    list=newList;
	    notifyDataSetChanged();
    }


}
