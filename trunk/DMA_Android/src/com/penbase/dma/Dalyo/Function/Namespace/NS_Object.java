package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;
import com.penbase.dma.Dalyo.Function.DateTime.DalyoDate;
import com.penbase.dma.Dalyo.Function.DateTime.Time;
import com.penbase.dma.View.ApplicationView;

import org.w3c.dom.Element;

import java.util.Date;
import java.util.GregorianCalendar;

public class NS_Object {
	public static boolean Not(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE,
				ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		if (value != null) {
			return !Boolean.parseBoolean(value.toString());
		} else {
			return false;
		}
	}

	public static Object NotNull(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		Object defaultValue = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_DEFAULT, ScriptAttribute.OBJECT);
		if (value != null) {
			return value;
		} else if (defaultValue != null) {
			return defaultValue;
		} else {
			return null;
		}
	}

	public static boolean ToBoolean(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		try {
			int intValue = Integer.valueOf(value.toString());
			if (intValue != 0) {
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException nfe) {
			return Boolean.valueOf(value.toString());
		}
	}

	public static Object ToComponent(Element element) {
		return Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
	}

	public static Object ToDate(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof DalyoDate) {
			return ((DalyoDate) value).toDate();
		} else {
			if (value.toString().contains(" ")) {
				// DateTime
				String dateString = value.toString().split(" ")[0];
				String timeString = value.toString().split(" ")[1];
				int year = Integer.valueOf(dateString.split("/")[2]);
				int month = Integer.valueOf(dateString.split("/")[1]);
				int day = Integer.valueOf(dateString.split("/")[0]);

				int hour = Integer.valueOf(timeString.split(":")[0]);
				int minute = Integer.valueOf(timeString.split(":")[1]);
				int second = Integer.valueOf(timeString.split(":")[2]);

				return new Date(new GregorianCalendar(year, month, day, hour,
						minute, second).getTimeInMillis());
			} else if (value.toString().contains("/")) {
				// Date
				int year = Integer.valueOf(value.toString().split("/")[2]);
				int month = Integer.valueOf(value.toString().split("/")[1]);
				int day = Integer.valueOf(value.toString().split("/")[0]);
				return new Date(new GregorianCalendar(year, month, day)
						.getTimeInMillis());
			} else {
				// Time
				return null;
			}
		}
	}

	public static Object ToDataset(Element element) {
		return Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
	}

	public static Object ToField(Element element) {
		return Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
	}

	public static Object ToForm(Element element) {
		return Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
	}

	public static Integer ToInt(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof DalyoDate) {
			return ((DalyoDate) value).toInt();
		} else if (value instanceof Time) {
			return ((Time) value).toInt();
		} else if (value.toString().indexOf(".") != -1) {
			return Double.valueOf(value.toString()).intValue();
		} else {
			Integer result = null;
			try {
				result = Integer.valueOf(value.toString());
			} catch (NumberFormatException nfe) {
				ApplicationView
						.errorDialog("Check your variable's type (ToInt) !");
			}
			return result;
		}
	}

	public static Object ToList(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		return value;
	}

	public static Number ToNumeric(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof DalyoDate) {
			return ((DalyoDate) value).toInt();
		} else if (value instanceof Time) {
			return ((Time) value).toInt();
		} else if (value.toString().indexOf(".") != -1) {
			return Double.valueOf(value.toString());
		} else {
			Integer result = null;
			try {
				result = Integer.valueOf(value.toString());
			} catch (NumberFormatException nfe) {
				ApplicationView
						.errorDialog("Check your variable's type (ToNumeric) !");
			}
			return result;
		}
	}

	public static Object ToRecord(Element element) {
		return Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
	}

	public static String ToString(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof DalyoDate) {
			return ((DalyoDate) value).toString();
		} else if (value instanceof Time) {
			return ((Time) value).toString();
		} else if (value == null) {
			return "";
		} else {
			return value.toString();
		}
	}

	public static Object ToTable(Element element) {
		return Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
	}

	public static Object ToTime(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE, ScriptAttribute.OBJECT);
		if (value instanceof String) {
			String[] sections = value.toString().split(":");
			int sectionLength = sections.length;
			if (sectionLength == 2) {
				Time time = new Time(Integer.parseInt(sections[0]), Integer
						.parseInt(sections[1]), 0);
				return time.toString();
			} else if (sectionLength == 3) {
				Time time = new Time(Integer.parseInt(sections[0]), Integer
						.parseInt(sections[1]), Integer.parseInt(sections[2]));
				return time.toString();
			} else {
				return null;
			}
		} else {
			return value;
		}
	}
}
