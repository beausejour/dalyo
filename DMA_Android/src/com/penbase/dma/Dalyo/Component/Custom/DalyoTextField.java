package com.penbase.dma.Dalyo.Component.Custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.penbase.dma.R;
import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DatabaseAttribute;
import com.penbase.dma.Dalyo.Component.DalyoComponent;
import com.penbase.dma.Dalyo.Function.Function;

import java.util.HashMap;

public class DalyoTextField extends AutoCompleteTextView implements DalyoComponent {
	private String mTableId = "";
	private String mFieldId = "";
	private Context mContext;
	
	public DalyoTextField(Context context, Typeface tf, float fs) {
		super(context);
		mContext = context;
		this.setTypeface(tf);
		this.setTextSize(fs);
	}
	
	public void setTableId(String tid) {
		this.mTableId = tid;
	}
	
	public void setFieldId(String fid) {
		this.mFieldId = fid;
	}
	
	public String getTableId() {
		return mTableId;
	}
	
	public String getFieldId() {
		return mFieldId;
	}
	
	public void setTrigger(final String trigger) {
		int resourceId = 0;
		if (trigger.equals(Constant.TRIGGERMAIL)) {
			resourceId = R.drawable.ico_mail;
		} else if (trigger.equals(Constant.TRIGGERPHONE)) {
			resourceId = R.drawable.ico_phone;
		} else if (trigger.equals(Constant.TRIGGERURL)) {
			resourceId = R.drawable.ico_url;
		}
		setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(resourceId), null, null, null);
		setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				String text = getText().toString().trim();
				if (text.length() > 0) {
					if (trigger.equals(Constant.TRIGGERMAIL)) {
						Intent emailIntent = new Intent (Intent.ACTION_SENDTO , Uri.parse("mailto:" + text));
						mContext.startActivity(emailIntent);
					} else if (trigger.equals(Constant.TRIGGERPHONE)) {
						Intent phoneIntent = new Intent (Intent.ACTION_DIAL, Uri.parse("tel:" + text)); 
						mContext.startActivity(phoneIntent);
					} else if (trigger.equals(Constant.TRIGGERURL)) {
						Intent urlIntent = null;
						if (text.startsWith("http://")) {
							urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
						} else {
							urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + text));
						}
						urlIntent.addCategory(Intent.CATEGORY_BROWSABLE);
				        mContext.startActivity(urlIntent);
					}
					return true;
				} else {
					return false;
				}
			}
		});
	}
	
	public void setTextFilter(String textFilter) {
		if (textFilter.equals(Constant.POSITIVENUMERIC)) {
			DigitsKeyListener numericOnlyListener = new DigitsKeyListener(
					false, true);
			setKeyListener(numericOnlyListener);
		}
	}
	
	public void refresh(HashMap<Object, Object> record) {
		if (!getFieldId().equals("")) {
			DalyoTextField.this.setText((String)record.get(DatabaseAttribute.FIELD+getFieldId()));
		}
	}
	
	public void clear() {
		this.setText("");
	}
	
	public String getValue() {
		return this.getText().toString();
	}
	
	public boolean isEmpty() {
		boolean result = false;
		if (this.getValue().trim().length() == 0) {
			result = true;
		}
		return result;
	}
	
	@Override
	public String getComponentLabel() {
		return getValue();
	}

	@Override
	public Object getComponentValue() {
		return getValue();
	}

	@Override
	public boolean isComponentEnabled() {
		return isEnabled();
	}

	@Override
	public boolean isComponentVisible() {
		if (getVisibility() == View.VISIBLE) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void resetComponent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComponentEnabled(boolean enable) {
		setEnabled(enable);
	}

	@Override
	public void setComponentFocus() {
		requestFocus();
	}

	@Override
	public void setComponentLabel(String label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComponentText(String text) {
		setText(text);
	}

	@Override
	public void setComponentValue(Object value) {
		setText(value.toString());
	}

	@Override
	public void setComponentVisible(boolean visible) {
		if (visible) {
			setVisibility(View.VISIBLE);
		} else {
			setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void setOnChangeEvent(final String functionName) {
		addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Function.createFunction(functionName);	
			}
		});
	}

	@Override
	public void setOnClickEvent(final String functionName) {
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Function.createFunction(functionName);
			}
		});
	}

	@Override
	public int getMinimumHeight() {
		return getSuggestedMinimumHeight();
	}

	@Override
	public int getMinimumWidth() {
		return getSuggestedMinimumWidth();
	}
}