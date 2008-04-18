package com.penbase.dma.Dalyo;

public class Application {
	private int mDalyoId;
	private int mIconRes;
	private String mName;
	
	public Application() {
		
	}
	public Application(int dalyoId, int iconRes, String name) {
		mDalyoId = dalyoId;
		mIconRes = iconRes;
		mName = name;
	}
	public int getMDalyoId() {
		return mDalyoId;
	}
	public void setMDalyoId(int dalyoId) {
		mDalyoId = dalyoId;
	}
	public int getMIconRes() {
		return mIconRes;
	}
	public void setMIconRes(int iconRes) {
		mIconRes = iconRes;
	}
	public String getMName() {
		return mName;
	}
	public void setMName(String name) {
		mName = name;
	}
	
}
