package com.usc.test.mate.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.usc.app.exception.GetExceptionDetails;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.server.DBConnecter;
import com.usc.server.util.SystemTime;
import com.usc.server.util.uuid.USCUUID;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MCreateRelationAction {
	public static Map<String, Object> create(String queryParam) {
		try
		{
			JSONObject jsonObject = JSONObject.parseObject(queryParam);
			String user = jsonObject.containsKey("userName") ? jsonObject.getString("userName") : null;
			String table = jsonObject.containsKey("tableName") ? jsonObject.getString("tableName") : null;
			String pid = jsonObject.containsKey("PID") ? jsonObject.getString("PID") : null;
			String fk = jsonObject.containsKey("fk") ? jsonObject.getString("fk") : null;
			String dataString = jsonObject.get("data").toString();
			if (table == null || dataString == null)
			{ return StandardResultTranslate.getResult(false, "Action_Create"); }
			List<HashMap> dataList = new ArrayList<>();
			if (table.equals("usc_model_property_field") || table.equals("usc_model_grid_field"))
			{
				dataList = JSONArray.parseArray(dataString, HashMap.class);
			} else
			{
				dataList.add(JSONObject.parseObject(dataString, HashMap.class));
			}
			return create(table, fk, pid, user, dataList);

		} catch (Exception e)
		{
			return StandardResultTranslate.getResult(false, GetExceptionDetails.details(e));
		}
	}

	public static Map<String, Object> create(String table, String fk, String pid, String user, List<HashMap> dataList) {
		List<HashMap<String, Object>> newList = new ArrayList<>();
		for (HashMap data : dataList)
		{
			data.remove("key");
			data.put("ID", USCUUID.UUID());
			data.put("DEL", 0);
			data.put("MYSM", "N");
			data.put("CTIME", SystemTime.getTimestamp());
			data.put("CUSER", user);
			data.put("STATE", "C");
			data.put("SORT", (new Date().getTime()) / 1000);
			if (table.equals("usc_model_menu") || table.equals("usc_model_relationship")
					|| table.equals("usc_model_queryview"))
			{
				data.put("VER", 0);
				data.put("EFFECTIVE", 1);
			}
			if (pid != null && fk != null)
			{ data.put(fk.toUpperCase(), pid); }
			int i = 0;
			StringBuffer fields = new StringBuffer("(");
			StringBuffer values = new StringBuffer("(");
			Object[] objects = new Object[data.keySet().size()];
			for (Object object : data.keySet())
			{
				String field = (String) object;
				if (i > 0)
				{
					fields.append(",");
					values.append(",");
				}
				fields.append(field);
				values.append("?");
				objects[i] = data.get(object);
				i++;
			}
			String f = fields.append(")").toString();
			String v = values.append(")").toString();
			String sql = "INSERT INTO " + table + f + " VALUES " + v;
			if (insertOrUpdate(sql, objects))
			{ newList.add(data); }
		}
		if (table.equals("usc_model_classview"))
		{
			List<HashMap> nodes = new ArrayList<>();
			for (HashMap<String, Object> map : newList)
			{
				HashMap node0 = new HashMap<String, Object>();
				node0.put("NO", map.get("NO"));
				node0.put("NAME", map.get("NAME"));
				node0.put("DATACONDITION", map.get("WCONDITION"));
				node0.put("PID", 0);
				node0.put("ITEMID", map.get("ID"));
				nodes.add(node0);
			}
			create("usc_model_classview_node", null, null, user, nodes);
		}
		return StandardResultTranslate.getQueryResult(true, "Action_Create", newList);

	}

	public static boolean insertOrUpdate(String insertSql, Object[] objects) {
		try (Connection connection = DBConnecter.getModelConnection();
				PreparedStatement ps = connection.prepareStatement(insertSql);)
		{

			for (int j = 0; j < objects.length; j++)
			{ ps.setObject(j + 1, objects[j]); }
			return ps.executeUpdate() > 0;

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
