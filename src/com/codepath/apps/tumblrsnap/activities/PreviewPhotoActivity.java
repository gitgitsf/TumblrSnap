package com.codepath.apps.tumblrsnap.activities;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.apps.tumblrsnap.ImageFilterProcessor;
import com.codepath.apps.tumblrsnap.R;
import com.codepath.apps.tumblrsnap.TumblrClient;
import com.codepath.apps.tumblrsnap.models.User;
import com.codepath.libraries.androidviewhelpers.SimpleProgressDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class PreviewPhotoActivity extends FragmentActivity {
	private static final String TAG = "PreviewPhotoActivity";
	private Bitmap photoBitmap;
	private Bitmap processedBitmap;
	private SimpleProgressDialog dialog;
	private ImageView ivPreview;
	private ImageFilterProcessor filterProcessor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("debug", " onCreate " + TAG);
		setContentView(R.layout.activity_preview_photo);
		ivPreview = (ImageView) findViewById(R.id.ivPreview);
		photoBitmap = getIntent().getParcelableExtra("photo_bitmap");
		filterProcessor = new ImageFilterProcessor(photoBitmap);
		redisplayPreview(ImageFilterProcessor.NONE);
	}
	
	private void redisplayPreview(int effectId) {
        processedBitmap = filterProcessor.applyFilter(effectId);
        ivPreview.setImageBitmap(processedBitmap);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("debug", " onCreateOptionsMenu() " + TAG);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preview_photo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("debug", " onOptionsItemSelected " + TAG);
		int itemId = item.getItemId();
		if (itemId == R.id.more || itemId == R.id.action_save)
			return true;
		
		int effectId = 0;
		
		switch (itemId) {
		case R.id.filter_none:
			effectId = ImageFilterProcessor.NONE;
			break;
		case R.id.filter_blur:
			effectId = ImageFilterProcessor.BLUR;
			break;
		case R.id.filter_grayscale:
			effectId = ImageFilterProcessor.GRAYSCALE;
			break;
		case R.id.filter_crystallize:
			effectId = ImageFilterProcessor.CRYSTALLIZE;
			break;
		case R.id.filter_solarize:
			effectId = ImageFilterProcessor.SOLARIZE;
			break;
		case R.id.filter_glow:
			effectId = ImageFilterProcessor.GLOW;
			break;
		default:
			effectId = ImageFilterProcessor.NONE;
			break;
		}
		redisplayPreview(effectId);
		return true;
	}

	
	
	public void onSaveButton(MenuItem menuItem) {
		dialog = SimpleProgressDialog.build(this);
		dialog.show();
		
		TumblrClient client = ((TumblrClient) TumblrClient.getInstance(TumblrClient.class, this));
		client.createPhotoPost(User.currentUser().getBlogHostname(), processedBitmap, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, String arg1) {
				dialog.dismiss();
				PreviewPhotoActivity.this.finish();
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				dialog.dismiss();
			}
		});
	}
}
