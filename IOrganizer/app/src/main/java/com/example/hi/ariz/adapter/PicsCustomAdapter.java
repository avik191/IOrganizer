package com.example.hi.ariz.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hi.ariz.MainActivity;
import com.example.hi.ariz.R;
import com.example.hi.ariz.ShowAlbum;
import com.example.hi.ariz.model.Album;
import com.example.hi.ariz.model.Pictures;
import com.example.hi.ariz.util.AppInfoInPreference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.os.Looper.getMainLooper;

/**
 * Created by HI on 18-July-17.
 */
public class PicsCustomAdapter extends RecyclerView.Adapter<PicsCustomAdapter.myviewholder> {

	Context context;
	ArrayList<Pictures> list;
	LayoutInflater inflator;
	int prepos=0;
	DBAdapter db;

	public PicsCustomAdapter(Context context, ArrayList<Pictures> list) {

		this.context=context;
		this.list=list;
		db=new DBAdapter(context);
		inflator=LayoutInflater.from(context);
	}

	@Override
	public myviewholder onCreateViewHolder(ViewGroup parent, int position) {
		View v=inflator.inflate(R.layout.row_item_album_picture,parent,false);//this view will contain appearance of each layout i.e each row..
		myviewholder holder=new myviewholder(v);// we are passing the view of each row to the myviewholder class
		return holder;
	}

	@Override
	public void onBindViewHolder(myviewholder holder, int position) {//here we will inflate datas in the widgets i.e image and title..
		//It is called for each row..so every row is inflated here..
		Pictures pictures=list.get(position);
		holder.PictureName.setText(pictures.getPicName());
		File imgFile = new  File(Environment.getExternalStorageDirectory() + "/iOrganizer_Pictures/"+pictures.getAlbumName()+"/"+pictures.getPicName()+".jpg");

		holder.imageview.setImageBitmap(getPreview(Environment.getExternalStorageDirectory() + "/iOrganizer_Pictures/"+pictures.getAlbumName()+"/"+pictures.getPicName()+".jpg"));

	//	Picasso.with(context).load(imgFile).into(holder.imageview);

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
		TextView PictureName;
		ImageView imageview;


		public myviewholder(View itemView)
		{
			super(itemView);
			PictureName = (TextView) itemView.findViewById(R.id.row_PictureName);//here we link with the elements of each view i.e each row and
			imageview = (ImageView) itemView.findViewById(R.id.row_PictureImage);// finally in onbind method we put the datas in each view..
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
		}

		@Override
		public void onClick(View view)
		{
			int pos = getAdapterPosition();
			String albumName=list.get(pos).getAlbumName();
			String picName=list.get(pos).getPicName();

			String fileName=Environment.getExternalStorageDirectory() + "/iOrganizer_Pictures/"+albumName+"/"+picName+".jpg";

			File imageFile = new File(fileName);
			Uri imageUri;
			String authorities=context.getPackageName()+".fileprovider";

			if (Build.VERSION.SDK_INT <= 19) {
				// Call some material design APIs here
				imageUri = Uri.fromFile(imageFile);
			} else {
				// Implement this feature without material design
				imageUri = FileProvider.getUriForFile(context,authorities,imageFile);
			}

			// - AVIK - intent for opening the  image on clicking. //
			Intent notificationIntent = new Intent( Intent.ACTION_VIEW);
			notificationIntent.setDataAndType(imageUri, "image/*");
			notificationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

			context.startActivity(notificationIntent);
		}

		@Override
		public boolean onLongClick(View view)
		{
			final int pos = getAdapterPosition();
			final String picName=list.get(pos).getPicName();
			final String albumName=list.get(pos).getAlbumName();
			final int id=list.get(pos).getpictureId();

			final CharSequence[] items = {"Delete image"};

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					if(item==0)
					{
							showConfirmDeleteDialog(picName,albumName,id,pos);
					}
					//Toast.makeText(context, items[item], Toast.LENGTH_SHORT).show();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();

			return true;
		}
	}

	private void showConfirmDeleteDialog(final String picName, final String albumName,final int id,final int pos)
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
				picNameTV.setText(picName+".jpg");
				final TextView yesButton = (TextView) dialog.findViewById(R.id.yesButton);
				yesButton.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						db.open();
						if(db.deletepic(id))
						{
							Toast.makeText(context, "picture deleted", Toast.LENGTH_SHORT).show();
							String fileName=Environment.getExternalStorageDirectory() + "/iOrganizer_Pictures/"+albumName+"/"+picName+".jpg";
							File file=new File(fileName);
							file.delete();
							list.remove(pos);
							notifyDataSetChanged();
						}
						db.close();
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

	public void addAlbum(ArrayList<Pictures> albumList)
	{
		ArrayList<Pictures> newList=new ArrayList<>();
		newList.addAll(albumList);
		list=newList;
		notifyDataSetChanged();
	}
}
