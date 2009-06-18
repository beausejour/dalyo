package com.penbase.dma.Dalyo.BarcodeReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.penbase.dma.R;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {
	  private static final int MAX_RESULT_IMAGE_SIZE = 200;
	  private static final float BEEP_VOLUME = 0.15f;
	  private static final long VIBRATE_DURATION = 200;

	  public CaptureActivityHandler mHandler;

	  private ViewfinderView mViewfinderView;
	  private View mResultView;
	  private View mResultButtonView;
	  private MediaPlayer mMediaPlayer;
	  private Result mLastResult;
	  private boolean mHasSurface;
	  private boolean mPlayBeep;
	  private boolean mVibrate;

	  private boolean mScanIntent;
	  private String mDecodeMode;

	  private final OnCompletionListener mBeepListener = new BeepListener();
	  
	  private Button mCompleteButton;
	  private Button mCancelButton;
	  private static String mBarCodeContent = null;
	  private String mId;
	  private ImageView mBarcodeImageView;

	  @Override
	  public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);

	    Window window = getWindow();
	    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    setContentView(R.layout.capture);
	    mId = getIntent().getStringExtra("ID");
	    CameraManager.init(getApplication());
	    mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
	    mResultView = findViewById(R.id.result_view);
	    mResultButtonView = findViewById(R.id.result_button_view);
	    mCompleteButton = (Button)findViewById(R.id.ok);
	    mCompleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				savePreview();
				((Barcode)ApplicationView.getComponents().get(mId).getView()).setContent(mBarCodeContent);
				finish();
			}
	    });
	    mCancelButton = (Button)findViewById(R.id.cancel);
	    mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetStatusView();
				mHandler.sendEmptyMessage(R.id.restart_preview);
			}
	    });
	    mHandler = null;
	    mLastResult = null;
	    mHasSurface = false;
	  }

	  private void savePreview() {
		  StringBuffer photoName = new StringBuffer(Constant.BARCODEFILE);
		  photoName.append(mId).append("_tmp.jpg");
		  StringBuffer filePath = new StringBuffer(Constant.PACKAGENAME);
		  filePath.append(ApplicationListView.getApplicationName()).append("/").append(photoName);
		  File file = new File(filePath.toString());
		  if (file.exists()) {
			  file.delete();
		  }
		  FileOutputStream fos = null;
		  try {
			fos = new FileOutputStream(file);
			((BitmapDrawable)mBarcodeImageView.getDrawable()).getBitmap().compress(Bitmap.CompressFormat .JPEG, 100, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	  }
	  
	  @Override
	  protected void onResume() {
	    super.onResume();

	    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
	    SurfaceHolder surfaceHolder = surfaceView.getHolder();
	    if (mHasSurface) {
	      // The activity was paused but not stopped, so the surface still exists. Therefore
	      // surfaceCreated() won't be called, so init the camera here.
	      initCamera(surfaceHolder);
	    } else {
	      // Install the callback and wait for surfaceCreated() to init the camera.
	      surfaceHolder.addCallback(this);
	      surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    }

	    Intent intent = getIntent();
	    String action = intent.getAction();
	    if (intent != null && action != null && (action.equals(Intents.Scan.ACTION) ||
	        action.equals(Intents.Scan.DEPRECATED_ACTION))) {
	      mScanIntent = true;
	      mDecodeMode = intent.getStringExtra(Intents.Scan.MODE);
	      resetStatusView();
	    } else {
	      mScanIntent = false;
	      mDecodeMode = null;
	      if (mLastResult == null) {
	        resetStatusView();
	      }
	    }

	    initBeepSound();
	  }

	  @Override
	  protected void onPause() {
	    super.onPause();
	    if (mHandler != null) {
	      mHandler.quitSynchronously();
	      mHandler = null;
	    }
	    CameraManager.get().closeDriver();
	  }

	  @Override
	protected void onDestroy() {
		super.onDestroy();
		CameraManager.get().closeDriver();
	}

	@Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	      if (mScanIntent) {
	        setResult(RESULT_CANCELED);
	        finish();
	        return true;
	      } else if (mLastResult != null) {
	        resetStatusView();
	        mHandler.sendEmptyMessage(R.id.restart_preview);
	        return true;
	      }
	    } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
	      // Handle these events so they don't launch the Camera app
	      return true;
	    }
	    return super.onKeyDown(keyCode, event);
	  }

	  @Override
	  public void onConfigurationChanged(Configuration config) {
	    // Do nothing, this is to prevent the activity from being restarted when the keyboard opens.
	    super.onConfigurationChanged(config);
	  }

	  public void surfaceCreated(SurfaceHolder holder) {
	    if (!mHasSurface) {
	      mHasSurface = true;
	      initCamera(holder);
	    }
	  }

	  public void surfaceDestroyed(SurfaceHolder holder) {
	    mHasSurface = false;
	  }

	  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	  }

	  public static String getBarcodeContent() {
		  return mBarCodeContent;
	  }
	  
	  /**
	   * A valid barcode has been found, so give an indication of success and show the results.
	   *
	   * @param rawResult The contents of the barcode.
	   * @param barcode   A greyscale bitmap of the camera data which was decoded.
	   * @param duration  How long the decoding took in milliseconds.
	   */
	  public void handleDecode(Result rawResult, Bitmap barcode, int duration) {
	    mLastResult = rawResult;
	    playBeepSoundAndVibrate();
	    drawResultPoints(barcode, rawResult);

	    mViewfinderView.setVisibility(View.GONE);
	    mResultView.setVisibility(View.VISIBLE);
	    mResultButtonView.setVisibility(View.VISIBLE);

	    mBarcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
	    mBarcodeImageView.setMaxWidth(MAX_RESULT_IMAGE_SIZE);
	    mBarcodeImageView.setMaxHeight(MAX_RESULT_IMAGE_SIZE);
	    mBarcodeImageView.setImageBitmap(barcode);
	      
	    TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
	    StringBuffer text = new StringBuffer("Information trouvée: \n\n");
	    ParsedResult result = ResultParser.parseResult(rawResult);
	    mBarCodeContent = result.getDisplayResult();
	    text.append(result.getDisplayResult());
	    contentsTextView.setText(text);
	  }

	  /**
	   * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
	   *
	   * @param barcode   A bitmap of the captured image.
	   * @param rawResult The decoded results which contains the points to draw.
	   */
	  private void drawResultPoints(Bitmap barcode, Result rawResult) {
	    ResultPoint[] points = rawResult.getResultPoints();
	    if (points != null && points.length > 0) {
	      Canvas canvas = new Canvas(barcode);
	      Paint paint = new Paint();
	      paint.setColor(getResources().getColor(R.color.result_image_border));
	      paint.setStrokeWidth(3);
	      paint.setStyle(Paint.Style.STROKE);
	      Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
	      canvas.drawRect(border, paint);

	      paint.setColor(getResources().getColor(R.color.result_points));
	      if (points.length == 2) {
	        paint.setStrokeWidth(4);
	        canvas.drawLine(points[0].getX(), points[0].getY(), points[1].getX(),
	            points[1].getY(), paint);
	      } else {
	        paint.setStrokeWidth(10);
	        for (int x = 0; x < points.length; x++) {
	          canvas.drawPoint(points[x].getX(), points[x].getY(), paint);
	        }
	      }
	    }
	  }

	  /**
	   * Creates the beep MediaPlayer in advance so that the sound can be triggered with the least
	   * latency possible.
	   */
	  private void initBeepSound() {
	    if (mPlayBeep && mMediaPlayer == null) {
	      mMediaPlayer = new MediaPlayer();
	      mMediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
	      mMediaPlayer.setOnCompletionListener(mBeepListener);

	      AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
	      try {
	        mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
	            file.getLength());
	        file.close();
	        mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
	        mMediaPlayer.prepare();
	      } catch (IOException e) {
	        mMediaPlayer = null;
	      }
	    }
	  }

	  private void playBeepSoundAndVibrate() {
	    if (mPlayBeep && mMediaPlayer != null) {
	      mMediaPlayer.start();
	    }
	    if (mVibrate) {
	      Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	      vibrator.vibrate(VIBRATE_DURATION);
	    }
	  }

	  private void initCamera(SurfaceHolder surfaceHolder) {
	    CameraManager.get().openDriver(surfaceHolder);
	    if (mHandler == null) {
	      boolean beginScanning = mLastResult == null;
	      mHandler = new CaptureActivityHandler(this, mDecodeMode, beginScanning);
	    }
	  }

	  private void resetStatusView() {
	    mResultView.setVisibility(View.GONE);
	    mResultButtonView.setVisibility(View.GONE);
	    mViewfinderView.setVisibility(View.VISIBLE);
	    mLastResult = null;
	  }

	  public void drawViewfinder() {
	    mViewfinderView.drawViewfinder();
	  }

	  /**
	   * When the beep has finished playing, rewind to queue up another one.
	   */
	  private static class BeepListener implements OnCompletionListener {
	    public void onCompletion(MediaPlayer mediaPlayer) {
	      mediaPlayer.seekTo(0);
	    }
	  }

	}