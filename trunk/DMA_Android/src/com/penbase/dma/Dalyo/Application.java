package com.penbase.dma.Dalyo;

public class Application {
	private int IconRes;
	private String Name;
	private String AppId;
	private String AppVer;
	private String AppBuild;
	private String SubId;
	private String DbId;

	public Application() {}

	public String getAppId() {
		return AppId;
	}
	
	public void setAppId(String dalyoId) {
		AppId = dalyoId;
	}
	
	public int getIconRes() {
		return IconRes;
	}
	
	public void setIconRes(int iconRes) {
		IconRes = iconRes;
	}
	
	public String getName() {
		return Name;
	}
	
	public void setName(String name) {
		Name = name;
	}
	
	public String getAppVer() {
		return AppVer;
	}
	
	public void setAppVer(String appver) {
		this.AppVer = appver;
	}
	
	public String getAppBuild() {
		return AppBuild;
	}
	
	public void setAppBuild(String appbuild) {
		this.AppBuild = appbuild;
	}
	
	public String getSubId() {
		return SubId;
	}
	
	public void setSubId(String subid) {
		this.SubId = subid;
	}
	
	public String getDbId() {
		return DbId;
	}
	
	public void setDbId(String dbid) {
		this.DbId = dbid;
	}
}
