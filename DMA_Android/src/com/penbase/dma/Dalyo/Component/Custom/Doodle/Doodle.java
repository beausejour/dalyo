package com.penbase.dma.Dalyo.Component.Custom.Doodle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.View.ApplicationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Doodle extends Activity implements ColorPickerDialog.OnColorChangedListener {
    private Paint mPaint;
    private DoodlePanelView	mDoodleView;
    private String mId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);	
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1);
        mDoodleView = new DoodlePanelView(this);
        mId = this.getIntent().getStringExtra("ID");
        setContentView(mDoodleView);
        setTitle("Doodle");
    }
    
   public class DoodlePanelView extends View {
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private float mX;
        private float mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        public DoodlePanelView(Context c) {
            super(c);
            mBitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(Color.WHITE);
            mPath = new Path();
        }
        
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
        		canvas.drawBitmap(mBitmap, 0, 0, null);
        		canvas.drawPath(mPath, mPaint);
            invalidate(); 
        }
        
        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        
        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);
       menu.add(Menu.NONE, 0, Menu.NONE, "Save");
       menu.add(Menu.NONE, 1, Menu.NONE, "Clear");
       menu.add(Menu.NONE, 2, Menu.NONE, "Add width");
       menu.add(Menu.NONE, 3, Menu.NONE, "Change color");
       return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
	   switch (item.getItemId()) {
	   		case 0:
	   			saveImage();
	   			break;
	   		case 1:
	   			clearImage();
	   			break;
	   		case 2:
	   			addWidth();
	   			break;
	   		case 3:
	   			changeColor();
	   			break;
	   }
	   return true;
   }
   
   /**
    * Saves bitmap in a file
    */
   private void saveImage() {
	   String imageName = System.currentTimeMillis()+".jpg";
	   StringBuffer filePath = new StringBuffer(Constant.APPPACKAGE);
	   filePath.append(Constant.USERDIRECTORY);
	   filePath.append(ApplicationView.getUsername()).append("/");
	   filePath.append(ApplicationView.getApplicationId()).append("/");
	   filePath.append(imageName);
       File file = new File(filePath.toString());
       FileOutputStream fos;
       try{
    	   fos = new FileOutputStream(file);
    	   if (mDoodleView.mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
    		   //Log.i("info", "image compressed ok");
    		   ((DoodleView)ApplicationView.getComponents().get(mId).getView()).setImageName(imageName);
    		   finish();
    	   } else {
    		   //Log.i("info", "image compressed failed");
    	   }
       } catch (FileNotFoundException e) {
    	   e.printStackTrace();
       }
   }
   
   private void clearImage() {
	   mDoodleView.mBitmap.eraseColor(0xFFFFFFFF);
	   mDoodleView.invalidate();
   }
   
   private void addWidth() {
	   if (mPaint.getStrokeWidth() < 10) {
		   mPaint.setStrokeWidth(mPaint.getStrokeWidth() + 2);
	   } else {
		   mPaint.setStrokeWidth(1);
	   }
   }
   
   private void changeColor() {
	   new ColorPickerDialog(this, this, mPaint.getColor()).show();
   }
   
   public void colorChanged(int color) {
       mPaint.setColor(color);
   }
}