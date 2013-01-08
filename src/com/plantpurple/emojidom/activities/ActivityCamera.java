package com.plantpurple.emojidom.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.plantpurple.emojidom.R;
import com.plantpurple.emojidom.tasks.SaveBitmapTask;
import com.plantpurple.emojidom.views.CameraPreview;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class ActivityCamera extends RoboActivity implements OnClickListener {
	private static final String TAG = "My";
	
	public static String MINE_IMAGE_DIRECTORY;
	
	@InjectView(R.id.rlBack) RelativeLayout mBacklayout;
	
	@InjectView(R.id.frameCameraPreviev) FrameLayout mCameraPreviewLayout;
	@InjectView(R.id.rlBottomBar) RelativeLayout mBottomBarLayout;
	@InjectView(R.id.btnMakePhoto) Button mMakePhotoBtn;
	
	private Camera mCamera;
    private CameraPreview mPreview;
    
    DrawOnTop mRectFrame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera);
		
		MINE_IMAGE_DIRECTORY = Environment.getExternalStorageDirectory() + 
				getString(R.string.xml_file_directory) + getString(R.string.xml_file_mine_directory);
		
		initUI();
		
		mMakePhotoBtn.setOnClickListener(this);
		mBacklayout.setOnClickListener(this);
		
		if (!checkCameraHardware(this)) {
			if (!checkFrontCameraHardware(this)) {
				setResult(ActivityMine.RESULT_CAMERA_FAIL);
				finish();
			
				return;
			}
		}
		
		// Create an instance of Camera
        mCamera = getCameraInstance();
        
        int cameraId = 0;
        
        if (mCamera == null) {
        	int cameraCount = Camera.getNumberOfCameras();
        	
        	if (cameraCount == 1) {
        		try {
        			Log.d(TAG, "Use front camera.");
        			mCamera = Camera.open(0);
        			cameraId = 0;

        		} catch (Exception e) {
        			
        		}
        		
        		if (mCamera == null) {
        			setResult(ActivityMine.RESULT_CAMERA_FAIL);
        			finish();
        		
        			return;
        		}
        	} else if (cameraCount > 1) {
        		try {
        			mCamera = Camera.open(1);
        			cameraId = 1;
        		} catch (Exception e) {
        			
        		}
        		
        		if (mCamera == null) {
        			setResult(ActivityMine.RESULT_CAMERA_FAIL);
        			finish();
        		
        			return;
        		}
        	} else {
        		setResult(ActivityMine.RESULT_CAMERA_FAIL);
    			finish();
    		
    			return;
        	}
        } else {
        	// get camera parameters
            Camera.Parameters params = mCamera.getParameters();
            
            // check for auto focus support..
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            	params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            
            mCamera.setParameters(params);
        }
        
        setCameraDisplayOrientation(cameraId, mCamera);
        
        // Create our Preview view and set it as the content of our activity.
        CameraInfo info = new android.hardware.Camera.CameraInfo();
	    android.hardware.Camera.getCameraInfo(cameraId, info);
        mPreview = new CameraPreview(this, mCamera, info);

        
        mCameraPreviewLayout.addView(mPreview);
        
        mRectFrame = new DrawOnTop(this, getResources().getDisplayMetrics()); 
        addContentView(mRectFrame, new LayoutParams 
        		(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
	}

	private void initUI() {
		
	}
	
	private void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
	     CameraInfo info = new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     
	     int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }
	
	@Override
	protected void onPause() {
		super.onPause();
		
		releaseCamera();
	}
	
	@Override
	public void onClick(View v) {
		if (v == mMakePhotoBtn) {
			mCamera.takePicture(null, null, mPicture);
		}
		
		if (v == mBacklayout) {
			finish();
		}
	}
	
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        return true;
	    } else {
	        return false;
	    }
	}
	
	private boolean checkFrontCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
	        return true;
	    } else {
	        return false;
	    }
	}
	
	private static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){

	    }
	    return c; 
	}
	
	private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();    
            mCamera = null;
        }
    }
	
	private PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	    	// get image from camera
	    	//Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
	    	
	    	File directory = new File(MINE_IMAGE_DIRECTORY);
	    	Log.d(TAG, "Directory for save temp: " + directory.getPath());
	    	if (!directory.exists())
	    		directory.mkdirs();
	    	
	    	try {
	            FileOutputStream fos = new FileOutputStream(directory.getPath() + File.separator + "temp.jpg");
	            fos.write(data);
	            fos.close();
	        } catch (FileNotFoundException e) {
	            Log.d(TAG, "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }
	    	
	    	Bitmap b = BitmapFactory.decodeFile(directory.getPath() + File.separator + "temp.jpg");
	    	
	    	new SaveBitmapTask(ActivityCamera.this, mCamera).execute(b);
	    }
	};
	
	class DrawOnTop extends View {
		private Rect rect;

		public DrawOnTop(Context context, DisplayMetrics metrics) {
			super(context);
			
			int top = (metrics.heightPixels - metrics.widthPixels) / 2;
			
			rect = new Rect(0, top, metrics.widthPixels, metrics.heightPixels-top);
		}

		@Override
		protected void onDraw(Canvas canvas) {

			Paint paint = new Paint();
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(4f);
			paint.setColor(Color.argb(230, 255, 255, 255));
			
			canvas.drawRect(rect, paint);

			super.onDraw(canvas);
		}
	}
}
