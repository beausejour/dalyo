package com.penbase.dma.Dalyo.Component.Custom.PictureBox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.penbase.dma.R;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.View.ApplicationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Launches camera to take a picture
 */
public class PictureBoxActivity extends Activity implements SurfaceHolder.Callback {
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private byte[] mPhotoBytes = null;
	private String mId;
	private ProgressDialog mSaveProgressDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mSurfaceView = new SurfaceView(this);
		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mId = this.getIntent().getStringExtra("ID");
		setContentView(mSurfaceView);
	}

	Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {
			mPhotoBytes = data;
		}
	};
	
	Camera.AutoFocusCallback autoFocusCallBack = new Camera.AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_CAMERA:
				//take picture
				//play sound
				MediaPlayer mp = MediaPlayer.create(PictureBoxActivity.this, R.raw.camera_click);
				mp.start();
				mCamera.takePicture(null, null, pictureCallback);
				break;
			case KeyEvent.KEYCODE_FOCUS:
				//auto focus
				if (event.getRepeatCount() == 0) {
					mCamera.autoFocus(autoFocusCallBack);
				}
				break;
			case KeyEvent.KEYCODE_BACK:
				PictureBoxActivity.this.finish();
				break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
       Camera.Parameters parameters = mCamera.getParameters();
       parameters.set("rotation", 0); 
       parameters.setPreviewSize(width, height);
       mCamera.setParameters(parameters);
       mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 0, Menu.NONE, "Save");
		menu.add(Menu.NONE, 1, Menu.NONE, "Cancel");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				//Save picture
				if (mPhotoBytes != null) {
					mSaveProgressDialog = ProgressDialog.show(this, "Please wait...", "Saving photo...", true, false);
					new Thread(new Runnable() {
						@Override
						public void run() {
							String photoName = System.currentTimeMillis()+".jpg";
							StringBuffer filePath = new StringBuffer(Constant.APPPACKAGE);
							filePath.append(Constant.USERDIRECTORY);
							filePath.append(ApplicationView.getUsername()).append("/");
							filePath.append(ApplicationView.getApplicationId()).append("/");
							filePath.append(Constant.TEMPDIRECTORY);
							File file = new File(filePath.toString());
							if (!file.exists()) {
								file.mkdir();
							}
							filePath.append(photoName);
							file = new File(filePath.toString());
							FileOutputStream fos = null;
							try{
								fos = new FileOutputStream(file);
								try {
									fos.write(mPhotoBytes);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							try {
								mPhotoBytes = null;
								fos.close();
								ApplicationView.getDataBase().saveBlobData(photoName, file);
							}  catch (IOException e) {
								e.printStackTrace();
							}
							((DalyoPictureBox)ApplicationView.getComponents().get(mId).getDalyoComponent()).setPhotoName(photoName);
							PictureBoxActivity.this.finish();
						}
					}).start();
				}
				break;
			case 1:
				//Cancel the current picture and take another one
				if (mPhotoBytes != null) {
					mCamera.startPreview();
				}
				break;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		if (mSaveProgressDialog != null) {
			mSaveProgressDialog.dismiss();
			mSaveProgressDialog = null;
		}
		super.onDestroy();
	}
}
