package com.penbase.dma.Dalyo.Function.Namespace;

import org.w3c.dom.Element;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.View.ApplicationView;

import android.content.Intent;
import android.net.Uri;

public class NS_Messaging {
	public static boolean Mail(Element element) {
		boolean result = false;
		Object mailTo = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_TO,
				ScriptAttribute.STRING);
		Object subject = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_SUBJECT,
				ScriptAttribute.STRING);
		Object message = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_MESSAGE,
				ScriptAttribute.STRING);
		if (mailTo != null) {
			Intent sendIntent = new Intent(Intent.ACTION_SENDTO , Uri.parse("mailto:" + mailTo.toString()));
			if (subject != null) {
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject.toString());
			}
			if (message != null) {
				sendIntent.putExtra(Intent.EXTRA_TEXT, message.toString());
			}
			result = ApplicationView.getCurrentView().startActivityIfNeeded(sendIntent, 1);
		}
		return result;
	}
}
