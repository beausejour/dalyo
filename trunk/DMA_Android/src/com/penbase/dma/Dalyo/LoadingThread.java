package com.penbase.dma.Dalyo;

import android.os.Handler;

public class LoadingThread implements Runnable {
	Thread m_thread = null;
	Handler handler = null;
	
	public LoadingThread(Handler h) {
		this.handler = h;
	}
	
	public void Start() {
		m_thread = new Thread(null, this, "LoadingThread");
		m_thread.start();
	}
	
	public void run() {
		try{ 
			Thread.sleep(3000); 
		} 
		catch (InterruptedException e) {}
	
		if (handler != null) {
			handler.sendEmptyMessage(0);
		}
	}
	
	public void Stop() {
		if (m_thread != null) {
			try{ 
				m_thread.join(); 
			} 
			catch (InterruptedException e) {}
		}
		m_thread = null;
	}
}
