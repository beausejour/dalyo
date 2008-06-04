package com.penbase.dma.Dalyo.Function.VoidType;

import java.io.IOException;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.util.Log;
import com.penbase.dma.View.ApplicationListView;
import com.penbase.dma.View.ApplicationView;
import com.penbase.dma.XmlElement.ScriptTag;
import com.penbase.dma.XmlElement.ScriptAttribute;

public class Synchronize {
	public Synchronize(NodeList items, NodeList globalParams)
	{
		int itemLen = items.getLength();
		for (int i=0; i<itemLen; i++)
		{
			Element element = (Element)items.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) &&
					(element.hasAttribute(ScriptTag.NAME)))
			{
				if (element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_FACELESS))
				{
					NodeList params = element.getChildNodes();
					int paramsLen = params.getLength();
					for (int j=0; j<paramsLen; j++)
					{
						Element keyword = (Element) params.item(j);
						if (keyword.getNodeName().equals(ScriptTag.KEYWOED))
						{
							Log.i("info", "call sync function");
							sync(keyword.getChildNodes().item(0).getNodeValue());
						}
					}
				}
			}
		}
	}
	
	public void sync(String type)
	{
		if (!type.equals(ScriptAttribute.CONST_FALSE))
		{
			try 
			{
				byte[] result = ApplicationView.getCurrentClient().launchImport(
						ApplicationListView.getApplicationsInfo().get("AppId"),
						ApplicationListView.getApplicationsInfo().get("DbId"), 
						ApplicationListView.getApplicationsInfo().get("Username"),
						ApplicationListView.getApplicationsInfo().get("Userpassword"));
				ApplicationView.getDataBase().syncTable(result);
				/*Set<String> keys = ApplicationView.getComponents().keySet();				
				for (String s : keys)
				{
					Log.i("info", "refresh component");
					ApplicationView.getComponents().get(s).refreshComponentContent(null);
				}*/
			}
			catch (IOException e) 
			{e.printStackTrace();}
			catch (InterruptedException e) 
			{e.printStackTrace();}
		}
	}
}
