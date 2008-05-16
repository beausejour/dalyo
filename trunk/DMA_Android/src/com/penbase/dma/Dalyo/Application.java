package com.penbase.dma.Dalyo;

public class Application {	
	private int IconRes;
	private String Name;	
	private int AppId;
	private int AppVer;
	private int AppBuild;
	private int SubId;
	private int DbId;

	public Application() {		
	}
	
	/*public Application(int dalyoId, int iconRes, String name) {
		DalyoId = dalyoId;
		IconRes = iconRes;
		Name = name;
	}*/
	public int getAppId() 
	{
		return AppId;
	}
	
	public void setAppId(int dalyoId) 
	{
		AppId = dalyoId;
	}
	
	public int getIconRes() 
	{
		return IconRes;
	}
	
	public void setIconRes(int iconRes) 
	{
		IconRes = iconRes;
	}
	
	public String getName() 
	{
		return Name;
	}
	
	public void setName(String name) 
	{
		Name = name;
	}
	
	public int getAppVer()
	{
		return AppVer;
	}
	
	public void setAppVer(int appver)
	{
		this.AppVer = appver;
	}
	
	public int getAppBuild()
	{
		return AppBuild;
	}
	
	public void setAppBuild(int appbuild)
	{
		this.AppBuild = appbuild;
	}
	
	public int getSubId()
	{
		return SubId;
	}
	
	public void setSubId(int subid)
	{
		this.SubId = subid;
	}
	
	public int getDbId()
	{
		return DbId;
	}
	
	public void setDbId(int dbid)
	{
		this.DbId = dbid;
	}
}
