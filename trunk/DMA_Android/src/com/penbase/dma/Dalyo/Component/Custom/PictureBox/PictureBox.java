package com.penbase.dma.Dalyo.Component.Custom.PictureBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.penbase.dma.R;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.View.ApplicationView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PictureBox extends Activity implements SurfaceHolder.Callback {
	private Camera camera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private byte[] photoBytes = null;
	private String id;
	private ProgressDialog saveProgressDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("info", "oncreate");
		super.onCreate(savedInstanceState);
		mSurfaceView = new SurfaceView(this);
		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		id = this.getIntent().getStringExtra("ID");
		setContentView(mSurfaceView);
		setTitle("Camera");
	}

	Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {
			photoBytes = data;
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
				MediaPlayer mp = MediaPlayer.create(PictureBox.this, R.raw.camera_click);
				mp.start();
				camera.takePicture(null, null, pictureCallback);
				break;
			case KeyEvent.KEYCODE_FOCUS:
				//auto focus
				if (event.getRepeatCount() == 0) {
					camera.autoFocus(autoFocusCallBack);
				}
				break;
			case KeyEvent.KEYCODE_BACK:
				PictureBox.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
       Camera.Parameters parameters = camera.getParameters();
       parameters.set("rotation", 0); 
       parameters.setPreviewSize(width, height);
       camera.setParameters(parameters);
       camera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		camera.setPreviewDisplay(mHolder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
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
				if (photoBytes != null) {
					saveProgressDialog = ProgressDialog.show(this, "Please wait...", "Saving photo...", true, false);
					new Thread() {
						public void run() {
							String photoName = System.currentTimeMillis()+".jpg";
							File file = new File(Constant.PACKAGENAME+photoName);
							FileOutputStream fos = null;
							try{
								fos = new FileOutputStream(file);
								try {
									fos.write(photoBytes);
								}
								catch (IOException e) {
									e.printStackTrace();
								}
							}
							catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							try {
								photoBytes = null;
								fos.close();
							} 
							catch (IOException e) {
								e.printStackTrace();
							}
							((PictureBoxView)ApplicationView.getComponents().get(id).getView()).setPhotoName(photoName);
							PictureBox.this.finish();
						}
					}.start();
				}
				break;
			case 1:
				//Cancel the current picture and take another one
				if (photoBytes != null) {
					camera.startPreview();
				}
				break;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		if (saveProgressDialog != null) {
			saveProgressDialog.dismiss();
			saveProgressDialog = null;
		}
		super.onDestroy();
	}
}
