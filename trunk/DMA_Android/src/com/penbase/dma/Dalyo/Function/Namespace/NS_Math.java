package com.penbase.dma.Dalyo.Function.Namespace;

import com.penbase.dma.Constant.ScriptAttribute;
import com.penbase.dma.Constant.ScriptTag;
import com.penbase.dma.Dalyo.Function.Function;

import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

public class NS_Math {
	public static Object Abs(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if (value.toString().indexOf(".") != -1) {
			return Math.abs(Double.valueOf(value.toString()));
		} else if (value.toString().indexOf(",") != -1) {
			Object result = Math.abs(Double.valueOf(value.toString().replace(
					",", ".")));
			return result.toString().replace(".", ",");
		} else {
			return Math.abs(Integer.valueOf(value.toString()));
		}
	}

	public static int Ceil(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if (value.toString().indexOf(",") != -1) {
			value = value.toString().replace(",", ".");
		}
		return Double.valueOf(Math.ceil(Double.valueOf(value.toString())))
				.intValue();
	}

	public static Object Division(Element element) {
		Object left = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_A,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object right = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_B,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if ((left.toString().indexOf(".") != -1)
				|| (right.toString().indexOf(".") != -1)) {
			return (Double.valueOf(left.toString()) / Double.valueOf(right
					.toString()));
		} else if ((left.toString().indexOf(",") != -1)
				|| (right.toString().indexOf(",") != -1)) {
			left = left.toString().replace(",", ".");
			right = right.toString().replace(",", ".");
			double result = (Double.valueOf(left.toString()) / Double
					.valueOf(right.toString()));
			return String.valueOf(result).replace(".", ",");
		} else {
			if ((Integer.valueOf(left.toString()) % Integer.valueOf(right
					.toString())) == 0) {
				return (Integer.valueOf(left.toString()) / Integer
						.valueOf(right.toString()));
			} else {
				return (Double.valueOf(left.toString()) / Double.valueOf(right
						.toString()));
			}
		}
	}

	public static int Floor(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if (value.toString().indexOf(",") != -1) {
			value = value.toString().replace(",", ".");
		}
		return Double.valueOf(Math.floor(Double.valueOf(value.toString())))
				.intValue();
	}

	public static String Format(Element element) {
		String result = "";
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object decimal = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_DECIMALS,
				ScriptAttribute.PARAMETER_TYPE_INT);
		Object hasThousandsSep = Function.getValue(element,
				ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_THOUSANDSSEP,
				ScriptAttribute.PARAMETER_TYPE_BOOLEAN);
		boolean hasThousandsSepValue = false;
		if (hasThousandsSep != null) {
			hasThousandsSepValue = Boolean.parseBoolean(hasThousandsSep
					.toString());
		}

		if (decimal != null) {
			String decimalString = "";
			if (Integer.valueOf(decimal.toString()) > 0) {
				decimalString = "0.";
				int decimalInt = Integer.valueOf(decimal.toString());
				for (int i = 0; i < decimalInt; i++) {
					decimalString += "0";
				}
			}
			DecimalFormat transform = new DecimalFormat(decimalString);
			if (value.toString().indexOf(",") != -1) {
				result = transform.format(Double.parseDouble(value.toString()
						.replace(",", ".")));
			} else {
				result = transform.format(Double.parseDouble(value.toString()));
				result = result.replace(",", ".");
			}
		} else {
			result = value.toString();
		}
		if (!hasThousandsSepValue) {
			return result;
		} else {
			if (result.indexOf(".") != -1) {
				if (result.indexOf(".") > 3) {
					result = result.substring(0, result.indexOf(".") - 3)
							+ " "
							+ result.substring(result.indexOf(".") - 3, result
									.length());
				}
			} else if (result.indexOf(",") != -1) {
				if (result.indexOf(",") > 3) {
					result = result.substring(0, result.indexOf(",") - 3)
							+ " "
							+ result.substring(result.indexOf(",") - 3, result
									.length());
				}
			} else {
				if (result.length() > 3) {
					result = result.substring(0, result.length() - 3)
							+ " "
							+ result.substring(result.length() - 3, result
									.length());
				}
			}
			return result;
		}
	}

	public static Object Multiple(Element element) {
		Object left = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_A,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object right = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_B,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if ((left.toString().indexOf(",") != -1)
				|| (right.toString().indexOf(",") != -1)) {
			left = left.toString().replace(",", ".");
			right = right.toString().replace(",", ".");
			double result = (Double.valueOf(left.toString()) * Double
					.valueOf(right.toString()));
			return String.valueOf(result).replace(".", ",");
		} else {
			return new BigDecimal(left.toString()).multiply(new BigDecimal(
					right.toString()));
		}
	}

	public static Object Percentage(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object percent = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_PERCENT,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		return new BigDecimal(value.toString()).multiply(new BigDecimal(percent
				.toString()).divide(new BigDecimal("100")));
	}

	public static int Random(Element element) {
		Object max = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_MAX,
				ScriptAttribute.PARAMETER_TYPE_INT);
		Object min = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_MIN,
				ScriptAttribute.PARAMETER_TYPE_INT);
		if ((max == null) && (min == null)) {
			return new Random().nextInt();
		} else if (min == null) {
			return new Random().nextInt(Integer.valueOf(max.toString()));
		} else {
			return Double.valueOf(
					Integer.valueOf(min.toString()) * Math.random()).intValue();
		}
	}

	public static Object Round(Element element) {
		Object value = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_VALUE,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object decimal = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_DECIMALS,
				ScriptAttribute.PARAMETER_TYPE_INT);
		Object result = null;

		if (decimal != null) {
			String decimalString = "";
			if (Integer.valueOf(decimal.toString()) > 0) {
				decimalString = "0.";
				int decimalInt = Integer.valueOf(decimal.toString());
				for (int i = 0; i < decimalInt; i++) {
					decimalString += "0";
				}
			}
			DecimalFormat transform = new DecimalFormat(decimalString);
			if (value.toString().indexOf(",") != -1) {
				result = transform.format(Double.parseDouble(value.toString()
						.replace(",", ".")));
			} else {
				result = transform.format(Double.parseDouble(value.toString()));
				result = result.toString().replace(",", ".");
			}
		} else {
			result = value.toString();
		}
		return result;
	}

	public static Object Subtract(Element element) {
		Object left = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_A,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object right = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_B,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if ((left.toString().indexOf(".") != -1)
				|| (right.toString().indexOf(".") != -1)) {
			return (Double.valueOf(left.toString()) - Double.valueOf(right
					.toString()));
		} else if ((left.toString().indexOf(",") != -1)
				|| (right.toString().indexOf(",") != -1)) {
			left = left.toString().replace(",", ".");
			right = right.toString().replace(",", ".");
			double result = (Double.valueOf(left.toString()) - Double
					.valueOf(right.toString()));
			return String.valueOf(result).replace(".", ",");
		} else {
			return (Integer.valueOf(left.toString()) - Integer.valueOf(right
					.toString()));
		}
	}

	public static Object Sum(Element element) {
		Object left = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_A,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		Object right = Function.getValue(element, ScriptTag.PARAMETER,
				ScriptAttribute.PARAMETER_NAME_B,
				ScriptAttribute.PARAMETER_TYPE_NUMERIC);
		if ((left.toString().indexOf(".") != -1)
				|| (right.toString().indexOf(".") != -1)) {
			return (Double.valueOf(left.toString()) + Double.valueOf(right
					.toString()));
		} else if ((left.toString().indexOf(",") != -1)
				|| (right.toString().indexOf(",") != -1)) {
			left = left.toString().replace(",", ".");
			right = right.toString().replace(",", ".");
			double result = (Double.valueOf(left.toString()) + Double
					.valueOf(right.toString()));
			return String.valueOf(result).replace(".", ",");
		} else {
			return (Integer.valueOf(left.toString()) + Integer.valueOf(right
					.toString()));
		}
	}
}
