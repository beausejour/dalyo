package com.penbase.dma.Dalyo.Component;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DesignTag;
import com.penbase.dma.Dalyo.BarcodeReader.Barcode;
import com.penbase.dma.Dalyo.Component.Custom.*;
import com.penbase.dma.Dalyo.Component.Custom.Dataview.DataView;
import com.penbase.dma.Dalyo.Component.Custom.Doodle.DoodleView;
import com.penbase.dma.Dalyo.Component.Custom.PictureBox.PictureBoxView;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Generic graphic component
 */
public class Component {
	private Context mContext;
	private String mId;
	private String mType;
	private String mLabel;
	private String mFontSize;
	private String mFontType;
	private View mView = null;
	private String mTableID = null;
	private String mFieldID = null;

	// Variables for checkbox
	private String mChecked = null;

	// Variables for combobox
	private ArrayList<String> mLabelList = null;
	private ArrayList<String> mValueList = null;

	// Variable for image
	private String mBackground = null;

	// Variable for Label
	private String mAlign = null;

	// Variable for textfield
	private boolean mEditable;
	private String mMultiLine;
	private String mTextFilter;

	// Variable for TimeField/DateField
	private String mDateTimeValue = null;

	// Variable for Gauge
	private int mMinValue;
	private int mMaxValue;
	private int mInitialValue;

	public Component(Context c, String t) {
		this.mContext = c;
		this.mType = t;
	}

	public void setFontSize(String fs) {
		this.mFontSize = fs;
	}

	public void setFontType(String ft) {
		this.mFontType = ft;
	}

	public void setId(String i) {
		this.mId = i;
	}

	public void setBackGround(String bg) {
		this.mBackground = bg;
	}

	public void setAlign(String align) {
		this.mAlign = align;
	}

	public void setLabel(String l) {
		this.mLabel = l;
	}

	public void setTableId(String tid) {
		this.mTableID = tid;
	}

	public void setFieldId(String fid) {
		this.mFieldID = fid;
	}

	public void setChecked(String check) {
		this.mChecked = check;
	}

	public void setItemList(ArrayList<String> l) {
		((ComboBox) getView()).setItemList(l);
	}

	public void setLabelList(ArrayList<String> l) {
		this.mLabelList = l;
	}

	public void setValueList(ArrayList<String> v) {
		this.mValueList = v;
	}

	public void setDataviewColumns(ArrayList<ArrayList<String>> l) {
		((DataView) getView()).setColumnInfo(l);
	}

	public void setDataviewOncalculate(HashMap<Integer, String> onc) {
		((DataView) getView()).setOncalculate(onc);
	}

	public void setMultiLine(String ml) {
		this.mMultiLine = ml;
	}

	public void setEditable(boolean editable) {
		this.mEditable = editable;
	}

	public void setTextFilter(String tf) {
		this.mTextFilter = tf;
	}

	public void setDateTimeValue(String value) {
		this.mDateTimeValue = value;
	}

	public void setInitValue(int i) {
		this.mInitialValue = i;
	}

	public void setMinValue(int min) {
		this.mMinValue = min;
	}

	public void setMaxValue(int max) {
		this.mMaxValue = max;
	}

	public View getView() {
		return mView;
	}

	public String getId() {
		return mId;
	}

	/**
	 * Instantiates a View object by its type
	 */
	public void setView() {
		if (mType.equals(DesignTag.COMPONENT_BARCODECOMPONENT)) {
			Barcode barcode = new Barcode(mContext, mId);
			mView = barcode;
		} else if (mType.equals(DesignTag.COMPONENT_BUTTON)) {
			Button button = new Button(mContext);
			button.setText(mLabel);
			button.setTypeface(getFontType(mFontType));
			button.setTextSize(getFontSize(mFontSize));
			if (mBackground != null) {
				String path = findResourceFile(mBackground);
				if (path.length() > 0) {
					Drawable d = Drawable.createFromPath(path);
					button.setBackgroundDrawable(d);
				}
			}
			mView = button;
		} else if (mType.equals(DesignTag.COMPONENT_CHECKBOX)) {
			CheckBox checkbox = new CheckBox(mContext);
			checkbox.setText(mLabel);
			checkbox.setTypeface(getFontType(mFontType));
			checkbox.setTextSize(getFontSize(mFontSize));
			if (mChecked.equals(true)) {
				checkbox.setChecked(true);
			}
			mView = checkbox;
		} else if (mType.equals(DesignTag.COMPONENT_COMBOBOX)) {
			ComboBox combobox;
			if ((mValueList != null) && (mLabelList != null)) {
				combobox = new ComboBox(mContext, mLabelList, mValueList);
			} else {
				combobox = new ComboBox(mContext);
			}
			mView = combobox;
		} else if (mType.equals(DesignTag.COMPONENT_LABEL)) {
			Label labelObject = new Label(mContext, getFontType(mFontType),
					getFontSize(mFontSize));
			labelObject.setText(mLabel);
			if (mAlign != null) {
				labelObject.setGravity(getGravity(mAlign));
			}
			mView = labelObject;
		} else if (mType.equals(DesignTag.COMPONENT_DATEFIELD)) {
			DateField datefield = new DateField(mContext,
					getFontType(mFontType), getFontSize(mFontSize),
					mDateTimeValue);
			mView = datefield;
		} else if (mType.equals(DesignTag.COMPONENT_TIMEFIELD)) {
			TimeField timefield = new TimeField(mContext,
					getFontType(mFontType), getFontSize(mFontSize),
					mDateTimeValue);
			mView = timefield;
		} else if (mType.equals(DesignTag.COMPONENT_TEXTFIELD)) {
			if (mMultiLine.equals("true")) {
				TextZone textzone = new TextZone(mContext,
						getFontType(mFontType), getFontSize(mFontSize));
				if ((mTableID != null) && (mFieldID != null)) {
					textzone.setTableId(mTableID);
					textzone.setFieldId(mFieldID);
				}
				mView = textzone;
			} else {
				TextField textfield = new TextField(mContext,
						getFontType(mFontType), getFontSize(mFontSize));
				if ((mTableID != null) && (mFieldID != null)) {
					textfield.setTableId(mTableID);
					textfield.setFieldId(mFieldID);
				}
				mView = textfield;
			}
			if (mAlign != null) {
				((TextView) mView).setGravity(getGravity(mAlign));
			}
			if (mEditable) {
				((TextView) mView).setEnabled(!mEditable);
			}
			if (!mTextFilter.equals(Constant.NONE)) {
				if (mTextFilter.equals(Constant.POSITIVENUMERIC)) {
					DigitsKeyListener numericOnlyListener = new DigitsKeyListener(
							false, true);
					((TextView) mView).setKeyListener(numericOnlyListener);
				}
			}
		} else if (mType.equals(DesignTag.COMPONENT_TEXTZONE)) {
			TextZone textzone = new TextZone(mContext, getFontType(mFontType),
					getFontSize(mFontSize));
			mView = textzone;
		} else if (mType.equals(DesignTag.COMPONENT_RADIOBUTTON)) {
			Radiobutton radiobutton = new Radiobutton(mContext);
			radiobutton.getTextView().setText(mLabel);
			radiobutton.getTextView().setTypeface(getFontType(mFontType));
			radiobutton.getTextView().setTextSize(getFontSize(mFontSize));
			mView = radiobutton;
		} else if (mType.equals(DesignTag.COMPONENT_NUMBERBOX)) {
			NumberBox numberbox = new NumberBox(mContext);
			numberbox.setInitialValue(mInitialValue);
			numberbox.setMaxValue(mMaxValue);
			numberbox.setMinValue(mMinValue);
			mView = numberbox;
		} else if (mType.equals(DesignTag.COMPONENT_PICTUREBOX)) {
			PictureBoxView pictureBox = new PictureBoxView(mContext, mId);
			mView = pictureBox;
		} else if (mType.equals(DesignTag.COMPONENT_IMAGE)) {
			ImageView imageview = new ImageView(mContext);
			if (mBackground != null) {
				String path = findResourceFile(mBackground);
				if (path.length() > 0) {
					Drawable d = Drawable.createFromPath(path);
					imageview.setBackgroundDrawable(d);
				}
			}
			mView = imageview;
		} else if (mType.equals(DesignTag.COMPONENT_DATAVIEW)) {
			DataView dataview = new DataView(mContext, mTableID);
			dataview.setText(getFontSize(mFontSize), getFontType(mFontType));
			mView = dataview;
		} else if (mType.equals(DesignTag.COMPONENT_DOODLE)) {
			DoodleView doodleView = new DoodleView(mContext, mId);
			mView = doodleView;
		} else if (mType.equals(DesignTag.COMPONENT_GAUGE)) {
			Gauge gauge = new Gauge(mContext);
			gauge.setProgress(mInitialValue);
			gauge.setMax(mMaxValue);
			gauge.setMinValue(mMinValue);
			mView = gauge;
		} else {
			Button button = new Button(mContext);
			button.setText(mLabel);
			mView = button;
		}
	}

	/**
	 * Parse resource directory to find name
	 * 
	 * @param name
	 *            resource file name
	 * @return resource file path
	 */
	private String findResourceFile(String name) {
		String result = "";
		StringBuffer testName = new StringBuffer(name);
		testName.append(".");
		StringBuffer path = new StringBuffer(DmaHttpClient.getResourcePath());
		path.append("/");
		File resourceDirectory = new File(path.toString());
		String[] files = resourceDirectory.list();
		int count = files.length;
		int i = 0;
		while (i < count) {
			if (files[i].startsWith(testName.toString())) {
				result = files[i];
				i = count;
			}
			i++;
		}
		if (result.equals("")) {
			return result;
		} else {
			path.append(result);
			return path.toString();
		}
	}

	/**
	 * Sets view's on click event
	 * 
	 * @param funcName
	 *            The function which called in on click event
	 * @param view
	 *            The view need to be captured
	 */
	public void setOnclickFunction(final String funcName, final View view) {
		if (view instanceof DataView) {
			((DataView) view).getListView().setOnItemClickListener(
					new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {
							if (((DataView) view).hasHeader()
									&& (((DataView) view).getSelectedRow() != -1)) {
								((DataView) view).setRowBackground(
										((DataView) view).getSelectedRow() + 1,
										false);
							} else {
								if (((DataView) view).getSelectedRow() != -1) {
									((DataView) view).setRowBackground(
											((DataView) view).getSelectedRow(),
											false);
								}
							}
							((DataView) view).setCurrentPosition(position);
							((DataView) view).setRowBackground(position, true);

							Function.createFunction(funcName);
						}
					});
		} else {
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Function.createFunction(funcName);
					view.setEnabled(true);
				}
			});
		}
	}

	/**
	 * Sets view's on change event
	 * 
	 * @param funcName
	 *            The function which called in on change event
	 * @param view
	 *            The view need to be captured
	 */
	public void setOnchangeFunction(String funcName, View view) {
		if (view instanceof Gauge) {
			((Gauge) view).setOnChangeFunction(funcName);
		} else if (view instanceof ComboBox) {
			((ComboBox) view).setOnChangeFunction(funcName);
		}
	}

	/**
	 * Converts align string to a Gravity constant
	 * 
	 * @param align
	 *            A string represents view's alignment
	 * @return Gravity constant
	 */
	private int getGravity(String align) {
		int gravity = 0;
		if (align.equals(Constant.LEFT)) {
			gravity = Gravity.LEFT;
		} else if (align.equals(Constant.CENTER)) {
			gravity = Gravity.CENTER;
		} else if (align.equals(Constant.RIGHT)) {
			gravity = Gravity.RIGHT;
		}
		return gravity;
	}

	private float getFontSize(String fs) {
		float fontSize = 14;
		if (fs.equals(Constant.SMALL)) {
			fontSize = 12;
		} else if (fs.equals(Constant.BIG)) {
			fontSize = 16;
		} else if (fs.equals(Constant.EXTRA)) {
			fontSize = 18;
		}
		return fontSize;
	}

	private Typeface getFontType(String ft) {
		Typeface fontType = Typeface.DEFAULT;
		if (ft.equals(Constant.ITALIC)) {
			fontType = Typeface.create(Typeface.SERIF, Typeface.ITALIC);
		} else if (ft.equals(Constant.BOLD)) {
			fontType = Typeface.DEFAULT_BOLD;
		} else if (ft.equals(Constant.UNDERLINE)) {
			// Underline text, not implemented yet in android
		} else if (ft.equals(Constant.ITALICBOLD)) {
			fontType = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC);
		}
		return fontType;
	}

	public void setValue(Object value) {
		View view = mView;
		if (view instanceof NumberBox) {
			((NumberBox) view).setValue(value);
		} else if (view instanceof TextField) {
			((TextField) view).setText(value.toString());
		} else if (view instanceof Label) {
			((Label) view).setText(value.toString());
		} else if (view instanceof TextZone) {
			((TextZone) view).setText(value.toString());
		} else if (view instanceof TimeField) {
			((TimeField) view).setTime(value.toString());
		} else if (view instanceof DateField) {
			((DateField) view).setDate(value.toString());
		} else if (view instanceof Gauge) {
			((Gauge) view).setValue(Integer.valueOf(value.toString()));
		} else if (view instanceof ImageView) {
			Drawable d = Drawable.createFromPath(DmaHttpClient.getFilesPath()
					+ value.toString());
			((ImageView) view).setBackgroundDrawable(d);
		}
	}

	public Object getValue() {
		Object result = null;
		View view = mView;
		if (view instanceof Barcode) {
			result = ((Barcode) view).getContent();
		} else if (view instanceof ComboBox) {
			result = ((ComboBox) view).getValue();
		} else if (view instanceof DateField) {
			result = ((DateField) view).getDate();
		} else if (view instanceof DoodleView) {
			result = ((DoodleView) view).getImageName();
		} else if (view instanceof Gauge) {
			result = ((Gauge) view).getValue();
		} else if (view instanceof Label) {
			result = ((Label) view).getText();
		} else if (view instanceof NumberBox) {
			result = ((NumberBox) view).getValue();
		} else if (view instanceof PictureBoxView) {
			result = ((PictureBoxView) view).getPhotoName();
		} else if (view instanceof TextZone) {
			result = ((TextZone) view).getValue();
		} else if (view instanceof TimeField) {
			result = ((TimeField) view).getTime();
		}
		return result;
	}

	public String getLabel() {
		String result = "";
		View view = mView;
		if (view instanceof Label) {
			result = ((Label) view).getText().toString();
		} else if (view instanceof TextZone) {
			result = ((TextZone) view).getValue();
		} else if (view instanceof TextField) {
			result = ((TextField) view).getValue();
		} else if (view instanceof ComboBox) {
			result = ((ComboBox) view).getLabel();
		}
		return result;
	}

	public void setText(String text) {
		String newText = "";
		if (!text.equals("null")) {
			newText = text;
		}
		View view = mView;
		if (view instanceof TextZone) {
			((TextZone) view).setText(newText);
		} else if (view instanceof Label) {
			((Label) view).setText(newText);
		} else if (view instanceof TextField) {
			((TextField) view).setText(newText);
		} else if (view instanceof Button) {
			((Button) view).setText(newText);
		}
	}

	public void setEnabled(boolean state) {
		View view = mView;
		if (view instanceof Button) {
			((Button) view).setEnabled(state);
		}
	}

	public boolean isEnabled() {
		return mView.isEnabled();
	}

	public void setFocus() {
		View view = mView;
		if (view.isFocusableInTouchMode()) {
			view.requestFocus();
		}
	}

	public void setVisible(boolean state) {
		View view = mView;
		if (state) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.INVISIBLE);
		}
	}

	public boolean isVisible() {
		View view = mView;
		if (view.getVisibility() == View.VISIBLE) {
			return true;
		} else {
			return false;
		}
	}

	public void reSet() {
		setValue(mLabel);
	}
}