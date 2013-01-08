package com.plantpurple.emojidom.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.activities.ActivityCamera;
import com.plantpurple.emojidom.activities.ActivityMine;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class SaveBitmapTask extends AsyncTask<Bitmap, Void, Void> {
	
	private Context context;
	private Camera camera;
	
	private String directory;
	private static final String FILE_NAME = "temp.jpg";
	
	private ProgressDialog dialog;
	
	public SaveBitmapTask(Context c, Camera cam) {
		context = c;
		camera = cam;
		
		directory = Environment.getExternalStorageDirectory() + 
				context.getString(R.string.xml_file_directory) + context.getString(R.string.xml_file_mine_directory);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		dialog = new ProgressDialog(context);
	}

	@Override
	protected Void doInBackground(Bitmap... bitmaps) {
		publishProgress();
		
		// counting for center position and create bitmap for this
    	int top = (bitmaps[0].getHeight() - bitmaps[0].getWidth()) / 2;

    	Log.d("My", "Photo params:");
    	Log.d("My", "	x: " + 0);
    	Log.d("My", "	y: " + top);
    	Log.d("My", "	width: " + bitmaps[0].getWidth());
    	Log.d("My", "	height: " + bitmaps[0].getHeight());
    	Bitmap destBitmap = Bitmap.createBitmap(bitmaps[0], 0, 0, bitmaps[0].getWidth(), bitmaps[0].getWidth());
    	
    	bitmaps[0] = null;
    	
    	File folder = new File(directory);
    	if (!folder.exists())
    		folder.mkdirs();
    	
    	final String fullFilePath = directory +  FILE_NAME;
    	try {
			destBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(fullFilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
			
		destBitmap = null;
    	
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		
		dialog.setMessage(context.getString(R.string.loading));
		dialog.show();
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		dialog.dismiss();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.save)
				.setCancelable(false)
				.setIcon(0)
				.setPositiveButton(context.getString(R.string.request_new_dialog_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent();
						intent.putExtra(ActivityMine.PARAM_CAMERA_FILE_PATH, directory + FILE_NAME);
						((ActivityCamera)context).setResult(ActivityMine.RESULT_CAMERA_OK, intent);
						((ActivityCamera)context).finish();
						
						dialog.cancel();
					}
				})
				.setNegativeButton(context.getString(R.string.request_new_dialog_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						
						camera.startPreview();
						File file = new File(directory + FILE_NAME);
						file.delete();
					}
		});

		AlertDialog dialogDeleteSmile = builder.create();
		dialogDeleteSmile.show();
	}
}
