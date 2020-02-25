package com.usc.test.mate.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.server.DBConnecter;
import com.usc.server.jdbc.DBUtil;
import com.usc.server.util.SystemTime;
import com.usc.server.util.uuid.USCUUID;
import com.usc.util.ObjectHelperUtils;
import com.usc.util.Symbols;

/**
 *
 * <p>
 * Title: ModelCreateAction
 * </p>
 *
 * <p>
 * Description: 创建模型业务对象数据
 * </p>
 *
 * @author PuTianXiong
 *
 * @date 2019年4月12日
 *
 */
public class MCreateAction
{
	public MCreateAction()
	{
	}

	private static JdbcTemplate jdbcTemplate = DBConnecter.getJdbcTemplate();

	private final static String INSERT_INTO = "insert into ";
	private final static String _L = " (";
	private final static String _VALUES = ",ID,DEL,CTIME,STATE,CUSER) values (";
	private final static String _R = ")";

	private static List<Map> list;

	private static String name;

	private static int itemType;

//	private static int islife;

	static String cuser;

	public static Object createModelObj(String jsonString)
	{
		if (jsonString == null)
			return null;
		JSONObject jsonObject = JSON.parseObject(jsonString);
		cuser = jsonObject.getString("userName");
		String tn = jsonObject.getString("tableName");
		Map<String, Object> data = JSONObject.parseObject(String.valueOf(jsonObject.getString("data")));
		data.put("VER", 0);
		data.put("EFFECTIVE", 0);
		data.put("SORT", (new Date().getTime()) / 1000);
		if (data.containsKey("BRIEFEXP"))
		{
			data.put("BRIEFEXP", String.valueOf(data.get("BRIEFEXP")));
		}
		;
		String tableNo = (String) data.get("ITEMNO");
		String tableName = (String) data.get("TABLENAME");
		String name = (String) data.get("NAME");

		List<Map<String, Object>> oldItemList = DBUtil.queryForList("USC_MODEL_ITEM",
				"del=? AND (UPPER(itemno)=? OR UPPER(tablename)=?)", new Object[]
				{ 1, tableNo.toUpperCase(), tableName.toUpperCase() });
		if (ObjectHelperUtils.isNotEmpty(oldItemList))
		{
			String updateItemSql = "UPDATE " + tn + " SET name='" + data.get("NAME") + "',islife=" + data.get("ISLIFE")
					+ ",type=" + data.get("TYPE") + ",queryfields=null,remark='" + data.get("REMARK")
					+ ",del=0,mysm='M',state='F' WHERE id='" + oldItemList.get(0).get("ID") + "'";
			String updateItemFieldSql = "UPDATE usc_model_field SET mysm='M',del=0,state='F' WHERE itemid='"
					+ oldItemList.get(0).get("ID") + "'";
			try
			{
				String[] sqls = new String[]
				{ updateItemSql, updateItemFieldSql };
				jdbcTemplate.batchUpdate(sqls);
			} catch (Exception e)
			{
				return StandardResultTranslate.getResult(false, e.getMessage());
			}

		} else
		{
			List<Map<String, Object>> itemList = DBUtil.queryForList("USC_MODEL_ITEM",
					"del=? AND (UPPER(itemno)=? OR UPPER(tablename)=?)", new Object[]
					{ 0, tableNo.toUpperCase(), tableName.toUpperCase() });
			if (ObjectHelperUtils.isEmpty(itemList))
			{
				data.put("MYSM", "N");
				Object[] objects = getObject(tn, data);
				name = (String) data.get("NAME");
				itemType = (Integer) data.get("TYPE");
//				islife = (Integer) data.get("ISLIFE");
				BatchInsertM(data, (String) objects[0], (Object[]) objects[1]);
			} else
			{
				return StandardResultTranslate.getResult(Symbols.L_MiddleBracket + tableNo.toUpperCase() + Symbols.COLON
						+ name + Symbols.R_MiddleBracket + "对象已存在", false);
			}
		}

		return new ActionMessage(true, RetSignEnum.NEW, StandardResultTranslate.translate("Action_Create_1"), data);
	}

	private static Object[] getObject(String table, Map<String, Object> data)
	{
		StringBuffer fields = new StringBuffer("");
		StringBuffer values = new StringBuffer("?,?,?,?,?");
		int len = data.keySet().size() + 5;
		Object[] objects = new Object[len];
		int i = 0;
		for (Object object : data.keySet())
		{
			if (i != 0)
			{
				fields.append(",");
			}
			String f = (String) object;
			fields.append(f);
			values.append(",?");
			Object objectv = data.get(object);
			if ("ITEMNO".equals(f) || "TABLENAME".equals(f))
			{
				objectv = String.valueOf(objectv).toUpperCase();
			}
			objects[i] = objectv;
			i++;
		}
		objects[i] = USCUUID.UUID();
		objects[i + 1] = 0;
		objects[i + 2] = SystemTime.getTimestamp();
		objects[i + 3] = "C";
		objects[i + 4] = cuser;
		data.put("ID", objects[len - 5]);
		data.put("DEL", objects[len - 4]);
		data.put("CTIME", objects[len - 3]);
		data.put("STATE", objects[len - 2]);
		data.put("CUSER", objects[len - 1]);

		String insertSql = INSERT_INTO + table + _L + fields.toString() + _VALUES + values + _R;
		return new Object[]
		{ insertSql, objects, data };
	}

	@Transactional
	public static Map<String, Object> BatchInsertM(Map<String, Object> map, String insertSql, Object... objects)
	{
		Map<String, Object> newData = map;
		if (DBUtil.insertOrUpdate(insertSql, objects))
		{
			String itemid = (String) newData.get("ID");
			createModeldefaultvFields(itemid);
			createModeldefaultvGrids(itemid);
			createModeldefaultvPropertice(itemid);
			createModeldefaultvMenus(itemid);
			return StandardResultTranslate.getResult("Action_Create", newData);
		} else
		{
			return StandardResultTranslate.getResult(false, "Action_Create");
		}

	}

	private static void createModeldefaultvGrids(String itemid)
	{
		Vector<String> idV = createData(MDefaultValues.getDefaultGrid(itemid), "usc_model_grid");
		if (ObjectHelperUtils.isNotEmpty(idV))
		{
			createModeldefaultvGridField(itemid, idV.get(0));
		}

	}

	private static void createModeldefaultvGridField(String itemid, String rootid)
	{
		createData(MDefaultValues.getDefaultGridField(itemType, itemid, rootid), "usc_model_grid_field");

	}

	private static void createModeldefaultvPropertice(String itemid)
	{
		Vector<String> idV = createData(MDefaultValues.getDefaultProperty(itemid), "usc_model_property");
		if (ObjectHelperUtils.isNotEmpty(idV))
		{
			createData(MDefaultValues.getDefaultPropertyField(itemType, itemid, idV.get(0)),
					"usc_model_property_field");
		}

	}

	private static void createModeldefaultvMenus(String itemid)
	{

		String table = "usc_model_menu";
		String condition = "del=? AND state=? AND implclass IN(?,?)";
		Object[] objects = new Object[]
		{ 0, "F", "com.unismartcore.app.action.DeleteUSCObjectAction",
				"com.unismartcore.app.action.BatchModifyAction" };
		List<Map<String, Object>> list = DBUtil.queryForList(table, condition, objects);
		if (ObjectHelperUtils.isNotEmpty(list))
		{
			createData(MDefaultValues.getDefaultMenu(list, itemid), "USC_MODEL_ITEMMENU");
		}
	}

	private static Vector<String> createData(List<Map<String, Object>> list2, String tableName)
	{
		List<Object[]> list = new ArrayList<Object[]>();
		Vector<String> ids = new Vector<String>();
		Object[] objects = null;
		for (Map map : list2)
		{
			objects = getObject(tableName, map);
			Object[] objs = (Object[]) objects[1];
			list.add(objs);
			Map<String, Object> newData = (Map<String, Object>) objects[2];
			ids.add((String) newData.get("ID"));
		}

		String insertSql = (String) objects[0];

		jdbcTemplate.batchUpdate(insertSql, list);
		return ids;
	}

	private static void createModeldefaultvFields(String itemid)
	{
		createData(MDefaultValues.getDefaultField(itemType, itemid), "USC_MODEL_FIELD");

	}

}
