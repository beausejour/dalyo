package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class ConfirmDialog extends Thread{
	Object value = null;
	String title = null;
	String message = null;
	Context context;
	Builder builder;
	Function function;
    public Handler mHandler;
	
	public ConfirmDialog(NodeList params, Context context) {
		this.context = context;
		int paramsLen = params.getLength();
		for (int i=0; i<paramsLen; i++) {
			Element element = (Element) params.item(i);
			if ((element.getNodeName().equals(ScriptTag.PARAMETER)) && 
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_TEXT))) {
				Log.i("info", "find message node type "+element.getNodeType());
				if (element.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
					message = element.getChildNodes().item(0).getNodeValue();
					Log.i("info", "message "+message);
				}
			}
			else if ((element.getNodeName().equals(ScriptTag.PARAMETER)) && 
					(element.getAttribute(ScriptTag.NAME).equals(ScriptAttribute.PARAMETER_NAME_CAPTION))) {
				if (element.getNodeType() != Node.TEXT_NODE) {
					NodeList children = element.getChildNodes();
					int childrenLen = children.getLength();
					for (int j=0; j<childrenLen; j++) {
						Element child = (Element)children.item(j);
						if (child.getNodeName().equals(ScriptTag.KEYWORD)) {
							title = (String) Function.getKeyWord(child);
						}
					}
				}
			}
		}
	}
	
	public void displayDialog() {
		Log.i("info", "display dialog");
		builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);		
		//.setIcon(R.drawable.star_big_on)
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				value = true;
				Log.i("info", "end dialog");
				Looper.myLooper().quit();
			}
		});
		
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				value = false;
				Log.i("info", "end dialog");
				Looper.myLooper().quit();
			}
		});
		builder.show();
	}
	
	public void run() {
		Log.i("info", "prepare");
		Looper.prepare();
		Log.i("info", "displaydialog");
		displayDialog();
		Log.i("info", "loop");
		Looper.loop();
	}
	
	public Object getValue() {
		while (value == null) {
			builder.show();
		}
		return value;
	}
}
