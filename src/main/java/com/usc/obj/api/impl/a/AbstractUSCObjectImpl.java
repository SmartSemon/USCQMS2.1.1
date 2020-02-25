package com.usc.obj.api.impl.a;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.format.datetime.DateFormatter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.TypeUtils;
import com.usc.app.action.mate.MateFactory;
import com.usc.app.exception.ThrowException;
import com.usc.obj.api.InvokeContext;
import com.usc.obj.api.RelationShip;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.USCObjectAction;
import com.usc.obj.util.USCObjExpHelper;
import com.usc.server.md.ItemField;
import com.usc.server.md.ItemGrid;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.ItemMenu;
import com.usc.server.md.ItemPage;
import com.usc.server.md.ItemRelationPage;
import com.usc.server.md.field.FieldAdapter;
import com.usc.server.md.field.FieldEditor;
import com.usc.server.md.field.FieldNameInitConst;
import com.usc.server.md.field.FieldUtils;
import com.usc.server.util.LoggerFactory;
import com.usc.util.ObjectHelperUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * <p>
 * Title: USCObjectImpl
 * </p>
 *
 * <p>
 * Description: USCObject实现类
 * </p>
 *
 * @author PuTianXiong
 *
 * @date 2019年5月7日
 *
 */
@NoArgsConstructor
@Data
public abstract class AbstractUSCObjectImpl implements USCObject, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4551319315703124886L;
	@JSONField(ordinal = 1)
	protected String itemNo;
	@JSONField(ordinal = 2)
	protected ItemInfo itemInfo;
	@JSONField(ordinal = 3)
	protected Map<String, Object> fieldValues;
	protected Map<String, Object> uMap;
	protected RelationShip relationShip;
	protected USCObjectAction uscObjectAction;

	public AbstractUSCObjectImpl(String objType)
	{
		init(objType, null);
	}

	public AbstractUSCObjectImpl(String objType, HashMap<String, Object> map)
	{
		init(objType, map);
	}

	public AbstractUSCObjectImpl(String objType, JSONObject map)
	{
		init(objType, map);
	}

	private void init(String objType, Map<String, Object> map)
	{
		setItemNo(objType);
		setItemInfo(MateFactory.getItemInfo(itemNo));
		this.fieldValues = new HashMap<String, Object>();
		if (ObjectHelperUtils.isNotEmpty(map))
		{
			map.forEach((field, v) -> {
				ItemField itemField = getItemField(field);
				if (itemField != null)
				{
					try
					{
						fieldValues.put(field, FieldUtils.getDBObjectByType(v, itemField));
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				} else
				{
					fieldValues.put("USC_OBJECT", itemNo);
				}
			});
		}
	}

	@Override
	public String getItemNo()
	{
		return this.itemNo;
	}

	public void setItemNo(String itemNo)
	{
		this.itemNo = itemNo;
		if (itemNo != null && this.itemInfo == null)
		{
			setItemInfo(MateFactory.getItemInfo(itemNo));
		}

	}

	public ItemInfo getItemInfo()
	{
		if (this.itemInfo == null)
		{
			this.itemInfo = MateFactory.getItemInfo(itemNo);
		}
		return this.itemInfo;
	}

	public void setItemInfo(ItemInfo itemInfo)
	{
		this.itemInfo = itemInfo;
	}

	@Override
	public Map<String, Object> getFieldValues()
	{
		return this.fieldValues;
	}

	public void setFieldValues(HashMap<String, Object> hashMap)
	{
		this.fieldValues = hashMap;
	}

	@Override
	public Object getFieldValue(String field)
	{
		if (fieldValues == null)
		{
			return null;
		}
		if (!containsField(field))
		{
			return null;
		}
		ItemField itemField = getItemField(field);
		return getDBValue(itemField);
	}

	@Override
	public Object getFieldValueBykey(String field)
	{
		if (fieldValues == null)
		{
			return null;
		}
		if (fieldValues.containsKey(field))
		{
			return fieldValues.get(field);
		}
		return null;
	}

	private Object getDBValue(ItemField field)
	{
		String strFieldType = field.getFType();
		String editor = field.getEditor();
		Object object = fieldValues.get(field.getNo());
		if (object == null)
		{
			return null;
		}
		if (FieldAdapter.isInt(strFieldType))
		{
			Object objectV = object;
			if (FieldEditor.MAPVALUELIST.equals(editor))
			{
				if (object instanceof Integer)
				{
					return object;
				}
				Map<String, String> KV = field.getFieldEditor().gainMapValueListKV();
				if (KV.containsKey(object.toString()))
				{
					return Integer.valueOf(object.toString());
				} else
				{
					objectV = field.getFieldEditor().gainMapValueListVK().get(object.toString());
				}
				object = objectV != null ? Integer.valueOf(objectV.toString()) : object;
			}
			return object;
		} else if (FieldAdapter.isVarchar(strFieldType))
		{
			Object objectV = object;
			if (FieldEditor.MAPVALUELIST.equals(editor))
			{
				Map<String, String> KV = field.getFieldEditor().gainMapValueListKV();
				if (KV.containsKey(object.toString()))
				{
					return object;
				} else
				{
					objectV = field.getFieldEditor().gainMapValueListVK().get(object.toString());
				}

				if (objectV == null)
				{
					return object;
				}
			}
			return objectV;
		} else if (FieldAdapter.isFloat(strFieldType))
		{
			return TypeUtils.castToFloat(object);
		} else if (FieldAdapter.isDouble(strFieldType))
		{
			return TypeUtils.castToDouble(object);
		} else if (FieldAdapter.isNumeric(strFieldType))
		{
			return TypeUtils.castToBigDecimal(object);
		} else if (FieldAdapter.isBoolean(strFieldType))
		{
			if (object instanceof Boolean)
			{
				return (boolean) object;
			} else if (object instanceof Integer)
			{
				return BooleanUtils.toBoolean((Integer) object);
			} else if (object instanceof String)
			{
				return BooleanUtils.toBoolean(String.valueOf(object));
			}
			return false;
		} else if (FieldAdapter.isDateTime(strFieldType))
		{
			if (object instanceof String)
			{
				try
				{
					object = new DateFormatter(field.getFieldEditor().gainFormatter()).parse(object.toString(),
							Locale.getDefault());
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
			} else if (object instanceof Date)
			{
				return object;
			}
			return object;
		} else
		{
			return object;
		}

	}

	@Override
	public String getFieldValueToString(String field)
	{
		Object object = getFieldValue(field);
		if (!ObjectHelperUtils.isEmpty(object))
		{
			try
			{
				return String.valueOf(FieldUtils.getObjectByType(object, getItemField(field))).trim();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public Integer getFieldValueToInteger(String field)
	{
		Object value = getFieldValue(field);

		return TypeUtils.castToInt(value);
	}

	@Override
	public Boolean getFieldValueToBoolen(String field)
	{
		Boolean value = TypeUtils.castToBoolean(getFieldValue(field));
		return value == null ? false : value;
	}

	@Override
	public Date getFieldValueToDate(String field)
	{
		Object value = getFieldValue(field);
		return TypeUtils.castToDate(value);
	}

	@Override
	public Float getFieldValueToFloat(String field)
	{
		Object value = getFieldValue(field);
		return TypeUtils.castToFloat(value);
	}

	@Override
	public Double getFieldValueToDouble(String field)
	{
		Object value = getFieldValue(field);
		return TypeUtils.castToDouble(value);
	}

	@Override
	public BigDecimal getFieldValueToBigDecimal(String field)
	{
		Object value = getFieldValue(field);
		return TypeUtils.castToBigDecimal(value);
	}

	@Override
	public boolean containsField(String paramString)
	{
		return this.itemInfo.containsField(paramString);
	}

	@Override
	public boolean delete(InvokeContext context) throws Exception
	{
		if (isDeleteAble(context))
		{
			return false;
		}
		if (!isDeleted(context))
		{
			USCObjectAction objectAction = getUSCObjectAction(context);
			if (objectAction == null)
			{
				return false;
			}
			objectAction.delete(context);
		}

		return Boolean.TRUE;
	}

	public USCObjectAction getUSCObjectAction(InvokeContext context)
	{
		if (this.uscObjectAction != null)
		{
			this.uscObjectAction.setCurrObj(this);
		} else
		{
			this.uscObjectAction = context.getActionObjType();
			uscObjectAction.setCurrObj(this);
		}
		return uscObjectAction;
	}

	@Override
	public String getID()
	{
		Object id = getFieldValue(FieldNameInitConst.FIELD_ID) != null ? getFieldValue(FieldNameInitConst.FIELD_ID)
				: getFieldValue("DID");
		if (id == null)
		{
			LoggerFactory.logError("对象不完整", new NullPointerException("_Object incompleteness"));
			return null;
		}
		return String.valueOf(id);
	}

	@Override
	public String getFieldNoByFieldName(String paramString)
	{
		List<ItemField> fields = this.itemInfo.getItemFieldList();
		if (fields != null)
		{
			for (ItemField itemField : fields)
			{
				if (paramString.equals(itemField.getFieldName()))
				{
					return itemField.getNo();
				}
			}
		}
		return null;
	}

	@Override
	public String getObjIcon()
	{
		return null;
	}

	@Override
	public String getObjCaption()
	{
		return this.itemInfo.getName();
	}

	@Override
	public Map<String, Object> getFieldValuesJSON(boolean b)
	{
		if (b)
		{
			JSONObject jsonObject = new JSONObject(this.fieldValues.size());
			this.fieldValues.forEach((f, v) -> {
				jsonObject.put(f, getFieldValueJson(f, v));
			});
			return jsonObject;
		}

		return getFieldValues();
	}

	public Object getFieldValueJson(String fieldNo, Object object)
	{
		if (object == null)
		{
			return null;
		}

		ItemField field = getItemField(fieldNo);
		if (field == null)
		{
			return this.itemNo;
		}
		try
		{
			return FieldUtils.getObjectByType(object, field);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return object;
	}

	@Override
	public String getTableName()
	{
		return this.itemInfo.getTableName();
	}

	@Override
	public void setFieldValue(String fieldNo, Object paramObject)
	{
		if (fieldNo == null)
		{
			return;
		}
		if (FieldNameInitConst.isSystemField(fieldNo) && !FieldNameInitConst.isEnableSystemField(fieldNo))
			return;
		if (uMap == null)
		{
			uMap = new HashMap<String, Object>();
		}
		if (containsField(fieldNo))
		{
			ItemField itemField = this.getItemField(fieldNo);
			if (ObjectHelperUtils.isEmpty(paramObject) && this.getFieldValue(fieldNo) != null)
			{
				uMap.put(fieldNo, null);
			} else
			{
				try
				{
					Object objectU = FieldUtils.getDBObjectByType(paramObject, itemField);
					Object objectH = getDBValue(itemField);
					if (!objectU.equals(objectH))
					{
						uMap.put(itemField.getNo(), FieldUtils.getDBObjectByType(paramObject, itemField));
					}
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}
//				if (!String.valueOf(paramObject).equals(this.getFieldValueToString(fieldNo)))
//				{
//					try
//					{
//						uMap.put(itemField.getNo(), FieldUtils.getDBObjectByType(paramObject, itemField));
//					} catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//					FieldAdapter adapter = new FieldAdapter();
//					Map<String, Object> d = adapter.filterResult(itemField, paramObject);
//					if (!ObjectHelperUtils.isEmpty(d))
//					{
//						uMap.putAll(d);
//					}
//				}

			}
		} else
		{
			ThrowException.throwException(new Throwable("Current Business Object Field Not Found : " + fieldNo));
		}
	}

	@Override
	public void setObjectFieldValues(Map<String, Object> uData)
	{
		if (ObjectHelperUtils.isEmpty(uData))
			return;
		uData.forEach((field, v) -> setFieldValue(field, v));
	}

	public ItemField getItemField(String field)
	{
		return this.itemInfo.getItemField(field);
	}

	@Override
	public ItemPage getItemPage(String paramString)
	{
		return this.itemInfo.getItemPage(paramString);
	}

	@Override
	public ItemGrid getItemGrid(String paramString)
	{
		return this.itemInfo.getItemGrid(paramString);
	}

	@Override
	public ItemRelationPage getItemRelationPage(String paramString)
	{
		return this.itemInfo.getItemRelationPage(paramString);
	}

	@Override
	public ItemMenu getMenu(String paramString)
	{
		return this.itemInfo.getItemMenu(paramString);
	}

	@Override
	public boolean isDeleteAble(InvokeContext paramInvokeContext) throws Exception
	{
		if ("F".equals(getFieldValueToString(FieldNameInitConst.FIELD_STATE)))
		{
			return false;
		}
		return true;
	}

	@Override
	public void setCurrObj(USCObject uscObject)
	{
		this.itemNo = uscObject.getItemNo();
		this.fieldValues = uscObject.getFieldValues();
	}

	@Override
	public String toString()
	{
		String str = this.itemInfo.getBriefExp();
		if (ObjectHelperUtils.isEmpty(str))
		{
			Integer lf = this.itemInfo.getType();
			return (lf == 0 || lf == 1) ? (this.getFieldValueToString("NO") + "::" + this.getFieldValueToString("NAME"))
					: this.itemInfo.getName();
		} else
		{
			return USCObjExpHelper.parseObjValueInExpression(this, str);
		}
	}
}
