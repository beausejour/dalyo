package com.penbase.dma.Dalyo.Component.Custom;

import java.text.DateFormat;
import java.util.Calendar;

import com.penbase.dma.R;
import com.penbase.dma.Dalyo.Function.Function;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker.OnTimeChangedListener;

public class DalyoDateTimePickerDialog extends AlertDialog implements
		OnClickListener, OnDateChangedListener, OnTimeChangedListener {
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	private int mInitialYear;
	private int mInitialMonth;
	private int mInitialDay;
	private int mInitialHour;
	private int mInitialMinute;

	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private boolean mHasDate;
	private boolean mHasTime;
	private final Calendar mCalendar;
	private final DateFormat mTitleDateFormat;
	private final DateFormat mTitleTimeFormat;
	private TextView mTextView;
	private String mOnChangeFunctionName = null;

	public DalyoDateTimePickerDialog(Context context, TextView textView,
			boolean hasDate, boolean hasTime) {
		super(context);
		mTextView = textView;
		mHasDate = hasDate;
		mHasTime = hasTime;
		mTitleDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		mTitleTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		mCalendar = Calendar.getInstance();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.datetimedialog, null);
		mDatePicker = (DatePicker) view.findViewById(R.id.datepicker);
		mDatePicker.init(mCalendar.get(Calendar.YEAR), mCalendar
				.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),
				this);
		mTimePicker = (TimePicker) view.findViewById(R.id.timepicker);
		mTimePicker.setOnTimeChangedListener(this);
		mTimePicker.setIs24HourView(true);

		setButton(context.getText(R.string.ok), this);
		setButton2(context.getText(R.string.cancel), (OnClickListener) null);
		setIcon(R.drawable.ic_dialog_time);

		if (!mHasDate) {
			mDatePicker.setVisibility(View.GONE);
		}
		if (!mHasTime) {
			mTimePicker.setVisibility(View.GONE);
		}

		setView(view);
		initialMaxCalendar();
	}

	public void initialMaxCalendar() {
		mYear = Integer.MAX_VALUE;
		mMonth = Integer.MAX_VALUE;
		mDay = Integer.MAX_VALUE;
		mHour = Integer.MAX_VALUE;
		mMinute = Integer.MAX_VALUE;
	}
	
	public void setInitialValues(String value) {
		if ((value != null) && !(value.equals(""))) {
			if (mHasDate && mHasTime) {
				String displayDate = value.split(" ")[0];
				mInitialYear = Integer.valueOf(displayDate.split("/")[2]);
				mInitialMonth = Integer.valueOf(displayDate.split("/")[1]) - 1;
				mInitialDay = Integer.valueOf(displayDate.split("/")[0]);
				setCalendar(mInitialYear, mInitialMonth, mInitialDay);
				mDatePicker
						.updateDate(mInitialYear, mInitialMonth, mInitialDay);

				String displayTime = value.split(" ")[1];
				mInitialHour = Integer.valueOf(displayTime.split(":")[0]);
				mInitialMinute = Integer.valueOf(displayTime.split(":")[1]);
				setCalendar(mInitialHour, mInitialMinute);
				mTimePicker.setCurrentHour(mInitialHour);
				mTimePicker.setCurrentMinute(mInitialMinute);
			} else if (mHasDate) {
				String displayDate = value.split(" ")[0];
				mInitialYear = Integer.valueOf(displayDate.split("/")[2]);
				mInitialMonth = Integer.valueOf(displayDate.split("/")[1]) - 1;
				mInitialDay = Integer.valueOf(displayDate.split("/")[0]);
				setCalendar(mInitialYear, mInitialMonth, mInitialDay);
				mDatePicker
						.updateDate(mInitialYear, mInitialMonth, mInitialDay);
			} else if (mHasTime) {
				String displayTime = value.split(" ")[0];
				mInitialHour = Integer.valueOf(displayTime.split(":")[0]);
				mInitialMinute = Integer.valueOf(displayTime.split(":")[1]);
				setCalendar(mInitialHour, mInitialMinute);
				mTimePicker.setCurrentHour(mInitialHour);
				mTimePicker.setCurrentMinute(mInitialMinute);
			}
		} else {
			mInitialYear = mCalendar.get(Calendar.YEAR);
			mInitialMonth = mCalendar.get(Calendar.MONTH);
			mInitialDay = mCalendar.get(Calendar.DAY_OF_MONTH);
			mDatePicker.updateDate(mInitialYear, mInitialMonth, mInitialDay);

			mInitialHour = mCalendar.get(Calendar.HOUR_OF_DAY);
			mInitialMinute = mCalendar.get(Calendar.MINUTE);
			mTimePicker.setCurrentHour(mInitialHour);
			mTimePicker.setCurrentMinute(mInitialMinute);
		}
	}

	@Override
	public void show() {
		super.show();
		if (mHasDate) {
			if (mYear == Integer.MAX_VALUE && mMonth == Integer.MAX_VALUE
					&& mDay == Integer.MAX_VALUE) {
				mDatePicker
						.updateDate(mInitialYear, mInitialMonth, mInitialDay);
				mCalendar.set(mInitialYear, mInitialMonth, mInitialDay);
			} else {
				mDatePicker.updateDate(mYear, mMonth, mDay);
				mCalendar.set(mYear, mMonth, mDay);
			}
		}
		if (mHasTime) {
			if (mHour == Integer.MAX_VALUE && mMinute == Integer.MAX_VALUE) {
				mTimePicker.setCurrentHour(mInitialHour);
				mTimePicker.setCurrentMinute(mInitialMinute);
				mCalendar.set(Calendar.HOUR_OF_DAY, mInitialHour);
				mCalendar.set(Calendar.MINUTE, mInitialMinute);
			} else {
				mTimePicker.setCurrentHour(mHour);
				mTimePicker.setCurrentMinute(mMinute);
				mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
				mCalendar.set(Calendar.MINUTE, mMinute);
			}
		}
		updateTitle();
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		setCalendar(year, monthOfYear, dayOfMonth);
		updateTitle();
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		setCalendar(hourOfDay, minute);
		updateTitle();
	}

	public void setOnChangeFunction(String functionName) {
		mOnChangeFunctionName = functionName;
	}

	private void setCalendar(int year, int month, int day) {
		mCalendar.set(Calendar.YEAR, year);
		mCalendar.set(Calendar.MONTH, month);
		mCalendar.set(Calendar.DAY_OF_MONTH, day);
	}

	private void setCalendar(int hour, int minute) {
		mCalendar.set(Calendar.HOUR_OF_DAY, hour);
		mCalendar.set(Calendar.MINUTE, minute);
	}

	private void updateTitle() {
		if (mHasDate && mHasTime) {
			setTitle(mTitleDateFormat.format(mCalendar.getTime()) + " "
					+ mTitleTimeFormat.format(mCalendar.getTime()));
		} else if (mHasDate) {
			setTitle(mTitleDateFormat.format(mCalendar.getTime()));
		} else if (mHasTime) {
			setTitle(mTitleTimeFormat.format(mCalendar.getTime()));
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (mHasDate) {
			mDatePicker.clearFocus();
			mYear = mCalendar.get(Calendar.YEAR);
			mMonth = mCalendar.get(Calendar.MONTH);
			mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		}
		if (mHasTime) {
			mTimePicker.clearFocus();
			mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
			mMinute = mCalendar.get(Calendar.MINUTE);
		}
		if (!mTextView.getText().toString().equals(getText())) {
			Function.createFunction(mOnChangeFunctionName);
		}
		mTextView.setText(getText());
		onSaveInstanceState();
	}

	public String getText() {
		StringBuffer result = new StringBuffer();
		if (mHasDate && mHasTime) {
			result.append(mTitleDateFormat.format(mCalendar.getTime()));
			result.append(" ");
			result.append(mTitleTimeFormat.format(mCalendar.getTime()));
		} else if (mHasDate) {
			result.append(mTitleDateFormat.format(mCalendar.getTime()));
		} else if (mHasTime) {
			result.append(mTitleTimeFormat.format(mCalendar.getTime()));
		}
		return result.toString();
	}
}