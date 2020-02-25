package com.usc.server.md.field;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import com.usc.server.md.ItemField;
import com.usc.util.ObjectHelperUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldUtils
{
	public static Object getObjectByType(Object object, ItemField field)
	{
		if (object == null)
			return null;

		String strFieldType = field.getFType();
		try
		{
			if (strFieldType == null)
			{

				if (object instanceof String)
				{
					return ((String) object).trim();
				}
				if (object.getClass().getName().equals("java.util.Timestamp"))
				{
					return ((Timestamp) fromSqlTimestamp(object)).toString().replace(".0", "");
				}
				return object;
			}

			String editor = field.getEditor();
			if (FieldEditor.MAPVALUELIST.equals(editor))
			{
				if (field.getFieldEditor().gainMapValueListKV().containsKey(object.toString()))
				{
					return field.getFieldEditor().gainMapValueListKV().get(object.toString());
				}
			}
			if (FieldAdapter.isInt(strFieldType))
			{
				if (object instanceof Boolean)
				{
					return BooleanUtils.toInteger((Boolean) object);
				}
				return Integer.valueOf(String.valueOf(object));
			}
			if (FieldAdapter.isVarchar(strFieldType))
			{
				return (String.valueOf(object)).trim();
			}
			if (FieldAdapter.isFloat(strFieldType))
			{
				return Float.valueOf(String.valueOf(object));
			}
			if (FieldAdapter.isDouble(strFieldType))
			{
				return Double.valueOf(String.valueOf(object));
			}
			if (FieldAdapter.isNumeric(strFieldType))
			{
				return (object != null) ? object : null;
			}
			if (FieldAdapter.isBoolean(strFieldType))
			{
				if (object instanceof Boolean)
				{
					return object;
				}
				if (object instanceof Integer)
				{
					return BooleanUtils.toBoolean((int) object);
				}
				if (object instanceof String)
				{
					return BooleanUtils.toBoolean((String) object);
				}
				return false;
			}
			if (FieldAdapter.isDateTime(strFieldType))
			{
				return dateFieldValueToString(object, field);
			}
			if (FieldAdapter.isRichText(strFieldType))
			{
				return object.toString();
			}
			{
				return object;
			}
		} catch (Exception e)
		{
			log.error("Field translate Error :" + field, e);
		}

		return null;

	}

	public static Object getDBObjectByType(Object object, ItemField field)
	{
		if (object == null)
			return null;

		String strFieldType = field.getFType();
		try
		{
			if (strFieldType == null)
			{

				if (object instanceof String)
				{
					return ((String) object).trim();
				}
				if (object.getClass().getName().equals("java.util.Timestamp"))
				{
					return (Timestamp) fromSqlTimestamp(object);
				}
				return object;
			}

			String editor = field.getEditor();
			if (FieldEditor.MAPVALUELIST.equals(editor))
			{
				String obj = object.toString();
				Map<String, String> kv = field.getFieldEditor().gainMapValueListKV();

				if (ObjectHelperUtils.isNotEmpty(kv) && !kv.containsKey(obj))
				{
					Map<String, String> vk = field.getFieldEditor().gainMapValueListVK();
					if (vk.containsKey(object.toString()))
					{
						obj = vk.get(object.toString());
					}
				}

				object = FieldAdapter.isInt(strFieldType) ? Integer.valueOf(obj) : obj;
			}
			if (FieldAdapter.isInt(strFieldType))
			{
				if (object instanceof Integer)
				{
					return (Integer) object;
				}
				if (object instanceof CharSequence)
				{
					try
					{
						return Integer.valueOf(String.valueOf(object));
					} catch (Exception e)
					{
						return object;
					}

				}
				if (object instanceof Boolean)
				{
					return BooleanUtils.toInteger((Boolean) object);
				}
			}
			if (FieldAdapter.isVarchar(strFieldType))
			{
				return (String.valueOf(object)).trim();
			}
			if (FieldAdapter.isFloat(strFieldType))
			{
				return Float.valueOf(String.valueOf(object));
			}
			if (FieldAdapter.isDouble(strFieldType))
			{
				return Double.valueOf(String.valueOf(object));
			}
			if (FieldAdapter.isNumeric(strFieldType))
			{
				return (object != null) ? object : null;
			}
			if (FieldAdapter.isBoolean(strFieldType))
			{
				if (object instanceof Boolean)
				{
					return object;
				}
				if (object instanceof Integer)
				{
					return BooleanUtils.toBoolean((int) object);
				}
				if (object instanceof String)
				{
					return BooleanUtils.toBoolean((String) object);
				}
				return false;
			}
			if (FieldAdapter.isDateTime(strFieldType))
			{
				return dateFieldValueCastToDate(object, field);
			}
			if (FieldAdapter.isRichText(strFieldType))
			{
				return object.toString();
			}
			{
				return object;
			}
		} catch (Exception e)
		{
			log.error("Field translate Error :" + field, e);
		}

		return null;

	}

	public static Object getObjectByType(ResultSet rest, int nIndex, ItemField field) throws Exception
	{
		if (rest == null)
			return null;

		Object object = rest.getObject(nIndex);
		return getObjectByType(object, field);

	}

	public static Timestamp fromSqlTimestamp(Object value) throws Exception
	{
		if (value == null)
			return null;

		Class<?> clz = value.getClass();
		Method method = clz.getMethod("timestampValue", new Class[0]);
		return (Timestamp) method.invoke(value, new Object[0]);
	}

	public static String dateFieldValueToString(Object object, ItemField field)
	{
		try
		{
			return (object == null || field == null) ? null
					: transFormatTimeToString((Date) object, field.getFieldEditor().gainFormatter());
		} catch (Exception e)
		{
			if (object instanceof String)
			{
				return (String) object;
			}
			if (object instanceof Number)
			{
				Long _long = ((Number) object).longValue();
				Date time = new Date(_long);
				return transFormatTimeToString(time, field.getFieldEditor().gainFormatter());
			}
		}
		return null;
	}

	public static Date dateFieldValueCastToDate(Object object, ItemField field)
	{
		if (object == null)
		{
			return null;
		}
		if (object instanceof Date)
		{
			return (Date) object;
		}
		return TypeUtils.castToDate(String.valueOf(object), field.getFieldEditor().gainFormatter());
	}

	public static String transFormatTimeToString(Date time, String format)
	{
		return JSON.toJSONStringWithDateFormat(time, format).replace("\"", "");
	}
}
