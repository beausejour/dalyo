package com.penbase.dma.Dalyo.Component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;

import com.penbase.dma.Constant.Constant;
import com.penbase.dma.Constant.DesignTag;
import com.penbase.dma.Dalyo.BarcodeReader.BarcodeComponent;
import com.penbase.dma.Dalyo.Component.Custom.*;
import com.penbase.dma.Dalyo.Component.Custom.Dataview.DalyoDataView;
import com.penbase.dma.Dalyo.Component.Custom.Doodle.DalyoDoodle;
import com.penbase.dma.Dalyo.Component.Custom.PictureBox.DalyoPictureBox;
import com.penbase.dma.Dalyo.HTTPConnection.DmaHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Generic graphic component
 */
public class Component {
	private Context mContext;
	private String mId = null;
	private String mType = null;
	private String mLabel = null;
	private String mFontColor = null;
	private String mFontSize = null;
	private String mFontType = null;
	private String mBackgroundColor = null;
	private DalyoComponent mComponent = null;
	private String mTableID = null;
	private String mFieldID = null;
	private String mDefaultValue = null;

	// Variables for combobox
	private ArrayList<String> mLabelList = null;
	private ArrayList<String> mValueList = null;
	private String mBullet = null;

	// Variable for image
	private String mBackground = null;

	// Variable for Label
	private String mAlign = null;

	// Variable for textfield
	private boolean mEditable;
	private String mMultiLine = null;
	private String mTextFilter = null;
	private String mTrigger = null;
	private boolean mPassword = false;

	// Variable for TimeField/DateField
	private String mDateTime = null;

	// Variable for Gauge
	private int mMinValue;
	private int mMaxValue;

	private int mStepValue;

	public Component(Context c, String t) {
		this.mContext = c;
		this.mType = t;
	}

	public void setFontColor(String fc) {
		this.mFontColor = fc;
	}

	public void setFontSize(String fs) {
		this.mFontSize = fs;
	}

	public void setFontType(String ft) {
		this.mFontType = ft;
	}

	public void setBackgroundColor(String bc) {
		this.mBackgroundColor = bc;
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

	public void setDefaultValue(String v) {
		this.mDefaultValue = v;
	}

	public void setTableId(String tid) {
		this.mTableID = tid;
	}

	public void setFieldId(String fid) {
		this.mFieldID = fid;
	}

	public void setItemList(ArrayList<String> l) {
		((DalyoComboBox) mComponent).setItemList(l);
	}

	public void setLabelList(ArrayList<String> l) {
		this.mLabelList = l;
	}

	public void setValueList(ArrayList<String> v) {
		this.mValueList = v;
	}

	public void setBullet(String bullet) {
		this.mBullet = bullet;
	}

	public void setDataviewColumns(ArrayList<ArrayList<String>> l) {
		((DalyoDataView) mComponent).setColumnInfo(l);
	}

	public void setDataviewOncalculate(HashMap<Integer, String> onc) {
		((DalyoDataView) mComponent).setOncalculate(onc);
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

	public void setTrigger(String trigger) {
		this.mTrigger = trigger;
	}

	public void setPassword(boolean value) {
		this.mPassword = value;
	}

	public void setDateTime(String dateTime) {
		this.mDateTime = dateTime;
	}

	public void setMinValue(int min) {
		this.mMinValue = min;
	}

	public void setMaxValue(int max) {
		this.mMaxValue = max;
	}

	public void setStepValue(int step) {
		this.mStepValue = step;
	}

	public DalyoComponent getDalyoComponent() {
		return mComponent;
	}

	public String getId() {
		return mId;
	}

	/**
	 * Instantiates a View object by its type
	 */
	public void setView() {
		if (mType.equals(DesignTag.COMPONENT_BARCODECOMPONENT)) {
			BarcodeComponent barcodeComponent = new BarcodeComponent(mContext,
					mId);
			mComponent = barcodeComponent;
		} else if (mType.equals(DesignTag.COMPONENT_BUTTON)) {
			DalyoButton button = null;
			if (mLabel != null) {
				button = new DalyoButton(mContext, mLabel, false);
				button.setTypeface(getFontType(mFontType));
				button.setTextSize(getFontSize(mFontSize));
			}
			if (mBackground != null) {
				String path = findResourceFile(mBackground);
				if (path.length() > 0) {
					button = new DalyoButton(mContext, path, true);
				} else {
					button = new DalyoButton(mContext, mLabel, false);
				}
			}
			if (mAlign != null) {
				button.setGravity(getGravity(mAlign));
			}
			if (mFontColor != null) {
				button.setTextColor(getColor(mFontColor));
			}
			if (mBackgroundColor != null) {
				button.setBackgroundColor(getColor(mBackgroundColor));
			}
			mComponent = button;
		} else if (mType.equals(DesignTag.COMPONENT_CHECKBOX)) {
			DalyoCheckBox checkbox = null;
			if (mDefaultValue.equals(Constant.TRUE)) {
				checkbox = new DalyoCheckBox(mContext, mLabel, true);
			} else {
				checkbox = new DalyoCheckBox(mContext, mLabel, false);
			}
			checkbox.setTypeface(getFontType(mFontType));
			checkbox.setTextSize(getFontSize(mFontSize));
			if (mFontColor != null) {
				checkbox.setTextColor(getColor(mFontColor));
			} else {
				checkbox.setTextColor(Color.BLACK);
			}
			if (mAlign != null) {
				checkbox.setGravity(getGravity(mAlign));
			}
			mComponent = checkbox;
		} else if (mType.equals(DesignTag.COMPONENT_COMBOBOX)) {
			DalyoComboBox combobox = null;
			if ((mValueList != null) && (mLabelList != null)) {
				combobox = new DalyoComboBox(mContext, mLabelList, mValueList);
			} else {
				combobox = new DalyoComboBox(mContext);
			}
			combobox.setFont(getFontSize(mFontSize), getFontType(mFontType),
					mFontColor);
			if (mBullet != null) {
				String path = findResourceFile(mBullet);
				if (path.length() > 0) {
					combobox.setBulletPath(path);
				}
			}
			mComponent = combobox;
		} else if (mType.equals(DesignTag.COMPONENT_DATEFIELD)) {
			DalyoDateField datefield = null;
			if (mDateTime != null) {
				datefield = new DalyoDateField(mContext,
						getFontType(mFontType), getFontSize(mFontSize),
						mFontColor, true, mDefaultValue);
			} else {
				datefield = new DalyoDateField(mContext,
						getFontType(mFontType), getFontSize(mFontSize),
						mFontColor, false, mDefaultValue);
			}
			mComponent = datefield;
		} else if (mType.equals(DesignTag.COMPONENT_DATAVIEW)) {
			DalyoDataView dataview = new DalyoDataView(mContext, mTableID);
			dataview.setText(getFontSize(mFontSize), getFontType(mFontType));
			mComponent = dataview;
		} else if (mType.equals(DesignTag.COMPONENT_DOODLE)) {
			DalyoDoodle dalyoDoodle = new DalyoDoodle(mContext, mId);
			mComponent = dalyoDoodle;
		} else if (mType.equals(DesignTag.COMPONENT_GAUGE)) {
			DalyoGauge dalyoGauge = new DalyoGauge(mContext, Integer
					.valueOf(mDefaultValue), mMinValue, mMaxValue);
			mComponent = dalyoGauge;
		} else if (mType.equals(DesignTag.COMPONENT_IMAGE)) {
			DalyoImage imageview = new DalyoImage(mContext);
			if (mBackground != null) {
				String path = findResourceFile(mBackground);
				if (path.length() > 0) {
					imageview.setInitialImage(path);
				}
			}
			mComponent = imageview;
		} else if (mType.equals(DesignTag.COMPONENT_LABEL)) {
			DalyoLabel labelObject = new DalyoLabel(mContext, mLabel,
					getFontType(mFontType), getFontSize(mFontSize));
			if (mFontColor != null) {
				labelObject.setTextColor(getColor(mFontColor));
			}
			if (mAlign != null) {
				labelObject.setGravity(getGravity(mAlign));
			}
			mComponent = labelObject;
		} else if (mType.equals(DesignTag.COMPONENT_NUMBERBOX)) {
			DalyoNumberBox numberbox = new DalyoNumberBox(mContext, Integer
					.valueOf(mDefaultValue), mStepValue);
			numberbox.setMaxValue(mMaxValue);
			numberbox.setMinValue(mMinValue);
			mComponent = numberbox;
		} else if (mType.equals(DesignTag.COMPONENT_PICTUREBOX)) {
			DalyoPictureBox pictureBox = new DalyoPictureBox(mContext, mId);
			mComponent = pictureBox;
		} else if (mType.equals(DesignTag.COMPONENT_RADIOBUTTON)) {
			DalyoRadiobutton dalyoRadiobutton = new DalyoRadiobutton(mContext,
					Boolean.valueOf(mDefaultValue));
			dalyoRadiobutton.setText(mLabel);
			dalyoRadiobutton.setTypeface(getFontType(mFontType));
			dalyoRadiobutton.setTextSize(getFontSize(mFontSize));
			if (mFontColor != null) {
				dalyoRadiobutton.setTextColor(getColor(mFontColor));
			} else {
				dalyoRadiobutton.setTextColor(Color.BLACK);
			}
			mComponent = dalyoRadiobutton;
		} else if (mType.equals(DesignTag.COMPONENT_TEXTFIELD)) {
			DalyoTextField textfield = new DalyoTextField(mContext,
					getFontType(mFontType), getFontSize(mFontSize));
			if (!mMultiLine.equals(Constant.TRUE)) {
				textfield.setSingleLine();
			}
			if (mFontColor != null) {
				textfield.setTextColor(getColor(mFontColor));
			}
			if (mAlign != null) {
				textfield.setGravity(getGravity(mAlign));
			}
			if (mEditable) {
				textfield.setEnabled(!mEditable);
			}
			if (mTrigger != null) {
				textfield.setTrigger(mTrigger);
			}
			if (!mTextFilter.equals(Constant.NONE)) {
				textfield.setTextFilter(mTextFilter);
			}
			if (mPassword) {
				textfield.setPassword();
			}
			mComponent = textfield;
		} else if (mType.equals(DesignTag.COMPONENT_TEXTZONE)) {
			DalyoTextZone textzone = new DalyoTextZone(mContext,
					getFontType(mFontType), getFontSize(mFontSize));
			if (!mMultiLine.equals(Constant.TRUE)) {
				textzone.setSingleLine();
			}
			if (mFontColor != null) {
				textzone.setTextColor(getColor(mFontColor));
			}
			if (mAlign != null) {
				textzone.setGravity(getGravity(mAlign));
			}
			if (mEditable) {
				textzone.setEnabled(!mEditable);
			}
			if (mTrigger != null) {
				textzone.setTrigger(mTrigger);
			}
			if (!mTextFilter.equals(Constant.NONE)) {
				textzone.setTextFilter(mTextFilter);
			}
			mComponent = textzone;
		} else if (mType.equals(DesignTag.COMPONENT_TIMEFIELD)) {
			DalyoTimeField timefield = new DalyoTimeField(mContext,
					getFontType(mFontType), getFontSize(mFontSize), mFontColor,
					mDefaultValue);
			mComponent = timefield;
		} else {
			DalyoButton button = new DalyoButton(mContext, mLabel
					+ " not implemented yet", false);
			mComponent = button;
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

	private int getColor(String color) {
		return Color.parseColor("#" + color);
	}

	public void setValue(Object value) {
		mComponent.setComponentValue(value);
	}

	public void setText(String text) {
		mComponent.setComponentText(text);
	}

	public Object getValue() {
		return mComponent.getComponentValue();
	}

	public String getLabel() {
		return mComponent.getComponentLabel();
	}

	public void setEnabled(boolean state) {
		mComponent.setComponentEnabled(state);
	}

	public boolean isEnabled() {
		return mComponent.isComponentEnabled();
	}

	public void setFocus() {
		mComponent.setComponentFocus();
	}

	public void setVisible(boolean state) {
		mComponent.setComponentVisible(state);
	}

	public boolean isVisible() {
		return mComponent.isComponentVisible();
	}

	public void reSet() {
		// setValue(mLabel);
		mComponent.resetComponent();
	}
}