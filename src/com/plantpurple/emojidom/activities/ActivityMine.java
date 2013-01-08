package com.plantpurple.emojidom.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.adapters.MineAdapter;
import com.plantpurple.emojidom.models.MineImage;
import com.plantpurple.emojidom.xml_base.XmlBase;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMine extends RoboActivity {
	@InjectView(R.id.tvEmptyFavourite) TextView mEmptyFavouritesLabel;
	@InjectView(R.id.gridSmiles) GridView mSmileysGrid;
	
	MineAdapter mAdapter;
	
	// Dialog identifier for loading image from gallery and getting
	// from photo
	private static final int DIALOG_GET_IMAGE = 1;
	
	public static int REQUEST_GET_FROM_CAMERA = 1000;
	public static int RESULT_CAMERA_OK = 100;
	public static int RESULT_CAMERA_FAIL = 101;
	public static String PARAM_CAMERA_FILE_PATH = "filePath";
	
	public static int REQUEST_GET_FROM_GALLERY = 1001;
	
	public static String MINE_IMAGE_DIRECTORY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_mine_tab);
		
		MINE_IMAGE_DIRECTORY = Environment.getExternalStorageDirectory() + 
				getString(R.string.xml_file_directory) + getString(R.string.xml_file_mine_directory);
		
		initUI();
		
		mSmileysGrid.setOnItemClickListener(mGridItemClickListener);
		mSmileysGrid.setOnItemLongClickListener(mGridItemLongClickListener);
		
		new LoadBitmapsTask().execute();
	}

	private void initUI() {
		mAdapter = new MineAdapter(this);
		mSmileysGrid.setAdapter(mAdapter);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		
		final String[] data = {getString(R.string.take_photo), getString(R.string.choose_photos)};
		
		switch (id) {
		case DIALOG_GET_IMAGE:
			adb.setTitle(R.string.add_photo_icon);
			adb.setItems(data, mDialogItemClickListener);
			break;

		default:
			break;
		}
		
		return adb.create();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.d("My", "request = " + requestCode);
		Log.d("My", "result = " + resultCode);
		
		if (data == null)
			return;
		
		// prepare xml for writing image information...
		XmlBase xmlBase = null;
		try {
			xmlBase = new XmlBase(Environment.getExternalStorageDirectory() + getString(R.string.xml_file_directory));
		} catch (IOException e) { return; }
		
		// check for directory exist
		File mineDirectory = new File(MINE_IMAGE_DIRECTORY);
		if (!mineDirectory.exists())
			mineDirectory.mkdirs();
		
		// prepare file name for image in mine storage..
		String fileName = "smile" + String.valueOf(System.currentTimeMillis()) + ".png";
		File fullPath = new File(mineDirectory, fileName);
		
		// work with camera results
		if (requestCode == REQUEST_GET_FROM_CAMERA) {
			if (resultCode == RESULT_CAMERA_OK) {		
				
				Log.d("My", "Get path: " + data.getStringExtra(PARAM_CAMERA_FILE_PATH));
				
				// get bitmap 
				Bitmap photo = BitmapFactory.decodeFile(data.getStringExtra(PARAM_CAMERA_FILE_PATH));
				
				// Use pre-scaling bitmap before write to file.
				// Width and height equals size of ImageView
				photo = Bitmap.createScaledBitmap(photo, (int)getResources().getDimension(R.dimen.smile_width), 
						(int)getResources().getDimension(R.dimen.smile_height), true);
				
				Log.d("My", "Full path: " + fullPath.getPath());
				if (writeFile(photo, fullPath)) {
					try {
						xmlBase.insertMine(fullPath.getPath());
						Log.d("My", "Insert from camera in: " + fullPath.getPath());
					} catch (IOException e) {
						
					} finally {
						Log.d("My", "FINALLY work");
						File temp = new File(data.getStringExtra(PARAM_CAMERA_FILE_PATH));
						temp.delete();
					}
				}
				
				MineImage image = new MineImage(photo, fullPath.getPath());
				
				mAdapter.addMineImage(image);
				mAdapter.notifyDataSetChanged();
				
			} else if (resultCode == RESULT_CAMERA_FAIL) {
				Toast.makeText(ActivityMine.this, R.string.error_camera_access, Toast.LENGTH_SHORT).show();
			}
		}
			
		// Work with image from gallery...
		if (requestCode == REQUEST_GET_FROM_GALLERY) {
			if (resultCode != 0) {
					
				// get file from data..
				Bitmap bitmap = null;
				try {
					bitmap = (Bitmap)data.getExtras().get("data");
				} catch (Exception e) {
					Toast.makeText(ActivityMine.this, R.string.error_image_loading_failed, Toast.LENGTH_SHORT).show();
					return;
				}
			        
				// Use pre-scaling bitmap before write to file.
				// Width and height equals size of ImageView
				bitmap = Bitmap.createScaledBitmap(bitmap, (int)getResources().getDimension(R.dimen.smile_width), 
						(int)getResources().getDimension(R.dimen.smile_height), true);
					
			    if (writeFile(bitmap, fullPath)) {
					try {
						xmlBase.insertMine(fullPath.getPath());
						Log.d("My", "Insert from gallery in: " + fullPath.getPath());
					} catch (IOException e) { Log.e("My", "Error: when trying insert gallery image. " + e.getMessage()); }
				} else {
					Log.d("My", "File from gallery not writed!");
				}
			    
			    MineImage image = new MineImage(bitmap, fullPath.getPath());
					
				mAdapter.addMineImage(image);
				mAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private OnClickListener mDialogItemClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			
			switch (which) {
			
			// Make photo
			case 0:
				Intent cameraIntent = new Intent(ActivityMine.this, ActivityCamera.class);
				startActivityForResult(cameraIntent, REQUEST_GET_FROM_CAMERA);
				
				break;
				
			// Get from gallery
			case 1:
				try {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		            intent.setType("image/*");
		            intent.putExtra("crop", "true");
		            intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					intent.putExtra("outputX", 256);
					intent.putExtra("outputY", 256);
					intent.putExtra("return-data", true);
					//intent.putExtra("output", selectedImageCropped);
		            startActivityForResult(Intent.createChooser(intent,
		                    "Select image"), REQUEST_GET_FROM_GALLERY);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(ActivityMine.this, 
							getString(R.string.error_cropping_not_supported), 
							Toast.LENGTH_SHORT).show();
					
					// get image from gallery without cropping
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		            intent.setType("image/*");
		            startActivityForResult(Intent.createChooser(intent,
		                    "Select image"), REQUEST_GET_FROM_GALLERY);
				}
				break;

			default:
				break;
			}
		}
	};
	
	private OnItemClickListener mGridItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
			
			// If click position is 0 then we open dialog for photo or
			// loading image from gallery
			if (position == 0) {
				showDialog(DIALOG_GET_IMAGE);
			} else {
				// else just add image on typing area
				
				final MineImage mineImage = (MineImage)adapterView.getItemAtPosition(position);
				
				Drawable d = new BitmapDrawable( mineImage.getBitmap() );
				MainActivity.appendMineSmileToEdit(d);
			}
		}
	};
	
	private OnItemLongClickListener mGridItemLongClickListener = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> adapterView, View v, int position, long id) {
			
			// if clicking on Add button then return
			if (position == 0)
				return false;
			
			final MineImage mineImage = (MineImage)adapterView.getItemAtPosition(position);
			Log.d("My", "	Delete file from Path: " + mineImage.getImagePath());
			Log.d("My", "	Delete file from Position: " + position);
			
			final int clickPosition = position;

			AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMine.this);
			builder.setMessage(getString(R.string.remove_from_mine))
					.setCancelable(false)
					.setIcon(0)
					.setPositiveButton(getString(R.string.request_new_dialog_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							XmlBase xmlBase = null;
							try {
								xmlBase = new XmlBase(Environment.getExternalStorageDirectory() +
										getString(R.string.xml_file_directory));
							} catch (IOException e) {return;}
							
							// get file path from xml and delete file from Mine directory..
							
							File file = new File(mineImage.getImagePath());
							boolean isDeleted = file.delete();
							Log.d("My", "Delete file from Mine is " + isDeleted + ". Path: " + mineImage.getImagePath());
							
							// remove information about smile from xml...
							try {
								xmlBase.deleteMine(mineImage.getImagePath());
							} catch (IOException e) {}

							mAdapter.removeItem(clickPosition);
							mAdapter.notifyDataSetChanged();

							dialog.cancel();
						}
					})
					.setNegativeButton(getString(R.string.request_new_dialog_cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
			});

			AlertDialog dialogDeleteSmile = builder.create();
			dialogDeleteSmile.show();

			return false;
		}
	};
	
	private class LoadBitmapsTask extends AsyncTask<Void, Void, ArrayList<MineImage>> {
		ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			dialog = new ProgressDialog(ActivityMine.this);
		}

		@Override
		protected ArrayList<MineImage> doInBackground(Void... params) {
			publishProgress();
			
			ArrayList<MineImage> bitmaps = new ArrayList<MineImage>();
			XmlBase base = null;
			try {
				base = new XmlBase(Environment.getExternalStorageDirectory() + getString(R.string.xml_file_directory));
			} catch (IOException e) {}
			
			ArrayList<String> items = new ArrayList<String>();
			items = base.getEmojiMine();
			for (int i = items.size()-1; i != -1; --i) {
				Log.i("My", "Item: " + items.get(i));
				//Bitmap b = BitmapFactory.decodeFile(items.get(i));
				
				MineImage image = new MineImage(items.get(i));

				if (image != null) {
					Log.i("My", "Bitmap added");
					bitmaps.add(image);
				}
			}
			
			return bitmaps;
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			
			dialog.setMessage(getString(R.string.loading));
			dialog.show();
		}
		
		@Override
		protected void onPostExecute(ArrayList<MineImage> result) {
			super.onPostExecute(result);
			
			dialog.dismiss();
			
			if (result != null) {
				mAdapter.addMineImages(result);
				mAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private boolean writeFile(Bitmap bmp, File f) {
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream(f);
			
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			Log.e("My", "Exception in writeFile(): " + e.getMessage());
			return false;
		}
		finally { 
			try { if (out != null ) out.close(); }
			catch(Exception ex) {} 
		}
		
		return true;
	}
}
