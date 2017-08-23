package com.example.hi.ariz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;


public class SplashActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		StartAnimations();
	}

	private void StartAnimations() {
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
		anim.reset();
		Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.translate);
		anim2.reset();

		RelativeLayout l = (RelativeLayout) findViewById(R.id.lLayout);
		l.clearAnimation();


		anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
		anim.reset();
		anim2 = AnimationUtils.loadAnimation(this, R.anim.translate);
		anim2.reset();

		ImageView iv = (ImageView) findViewById(R.id.imgView);
		TextView tv= (TextView) findViewById(R.id.stv);
	//	GifImageView gifImageView= (GifImageView) findViewById(R.id.gifview);
		iv.clearAnimation();
		iv.startAnimation(anim);
		tv.clearAnimation();
		tv.startAnimation(anim2);
//		gifImageView.clearAnimation();
//		gifImageView.startAnimation(anim);



		Thread th=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					Intent i=new Intent(SplashActivity.this,MainActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(i);
					SplashActivity.this.finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		th.start();
	}
}
