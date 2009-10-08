package com.penbase.dma.Dalyo;

import android.graphics.drawable.Drawable;

public class Application {
	private int mIconRes;
	private String mName;
	private String mAppId;
	private String mAppVer;
	private String mAppBuild;
	private String mSubId;
	private String mDbId;
	private String mIconPath;

	public Application() {}

	public String getAppId() {
		return mAppId;
	}
	
	public void setAppId(String dalyoId) {
		mAppId = dalyoId;
	}
	
	public int getIconRes() {
		return mIconRes;
	}
	
	public void setIconRes(int iconRes) {
		mIconRes = iconRes;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getAppVer() {
		return mAppVer;
	}
	
	public void setAppVer(String appver) {
		this.mAppVer = appver;
	}
	
	public String getAppBuild() {
		return mAppBuild;
	}
	
	public void setAppBuild(String appbuild) {
		this.mAppBuild = appbuild;
	}
	
	public String getSubId() {
		return mSubId;
	}
	
	public void setSubId(String subid) {
		this.mSubId = subid;
	}
	
	public String getDbId() {
		return mDbId;
	}
	
	public void setDbId(String dbid) {
		this.mDbId = dbid;
	}
	
	public Drawable getIcon() {
		if (mIconPath != null) {
			return Drawable.createFromPath(mIconPath);
		} else {
			return null;
		}
	}

	public void setIconPath(String path) {
		mIconPath = path;
	}
}
