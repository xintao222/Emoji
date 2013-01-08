package com.plantpurple.emojidom.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import roboguice.activity.RoboTabActivity;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.activities.MainActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class ShareMessageTask extends AsyncTask<Bitmap, Void, File> {
	
	//private Context context;
	private RoboTabActivity activity;
	
	private ProgressDialog dialog;
	
	public ShareMessageTask(RoboTabActivity a) {
		activity = a;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		dialog = new ProgressDialog(activity);
	}

	@Override
	protected File doInBackground(Bitmap... bitmaps) {
		publishProgress();
		
		// prepare current date for file name...
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String currentTime = df.format(new java.util.Date());

		// create paths..
		String filePath = Environment.getExternalStorageDirectory()
				+ activity.getString(R.string.xml_file_directory) + "Share/";
		String fileName = "Screenshot_" + currentTime + ".jpg";

		// if path not exist then create it. Delete existing directory with
		// previous shared image
		File path = new File(filePath);
		deleteDirectory(path);

		if (!path.exists())
			path.mkdirs();

		// create file for sending
		File file = new File(filePath + fileName);

		try {
			bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 100,
					new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			file = null;
		}
		
		return file;
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		
		dialog.setMessage(activity.getString(R.string.loading));
		dialog.show();
	}

	@Override
	protected void onPostExecute(File file) {
		super.onPostExecute(file);
		
		dialog.dismiss();
		
		if (file == null)
			return;
		
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		//sendIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.share_text));
		sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		sendIntent.setType("image/png");
		
		activity.startActivityForResult(Intent.createChooser(sendIntent,
				activity.getResources().getString(R.string.share_via)),
				MainActivity.REQUEST_SHARE_RESULT);
	}
	
	private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
}
