package com.penbase.dma.Dalyo.Component.Custom.PictureBox;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

public class PictureBox extends Activity implements SurfaceHolder.Callback {
	private Camera camera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	
	 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSurfaceView = new SurfaceView(this);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setContentView(mSurfaceView);
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i("info", "surfaceChanged "+width+" "+height);

        Camera.Parameters parameters = camera.getParameters();
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
		Log.i("info", "surfaceDestroyed");
		camera.stopPreview();
		camera = null;
	}
	
	//Need to call takePicture to save the picture, but this function couldn't be called in an emulator
}

