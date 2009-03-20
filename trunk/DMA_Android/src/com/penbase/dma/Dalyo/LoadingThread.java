package com.penbase.dma.Dalyo;

import android.os.Handler;

/**
 * Displays a progress dialog during construction of an application
 */
public class LoadingThread implements Runnable {
	Thread mThread = null;
	Handler mHandler = null;
	
	public LoadingThread(Handler h) {
		this.mHandler = h;
	}
	
	public void Start() {
		mThread = new Thread(null, this, "LoadingThread");
		mThread.start();
	}
	
	public void run() {
		try{ 
			Thread.sleep(3000); 
		} catch (InterruptedException e) {
			
		}
	
		if (mHandler != null) {
			mHandler.sendEmptyMessage(0);
		}
	}
	
	public void Stop() {
		if (mThread != null) {
			try{ 
				mThread.join(); 
			} catch (InterruptedException e) {
				
			}
		}
		mThread = null;
	}
}
