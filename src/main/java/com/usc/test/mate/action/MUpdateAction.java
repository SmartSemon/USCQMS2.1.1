package com.usc.test.mate.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.server.DBConnecter;
import com.usc.server.jdbc.DBUtil;
import com.usc.server.util.SystemTime;

@Transactional
public class MUpdateAction {

	@Transactional(rollbackFor = Exception.class)
	public static Map<String, Object> update(String jsonString) throws Exception {
		if (jsonString == null || jsonString.trim().equals("{}"))
			StandardResultTranslate.getResult(false, "Action_Update");
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
		String user = jsonObject.getString("userName");
		String tableName = jsonObject.getString("tableName");
		String dataJson = jsonObject.get("data").toString();
		String uDataJson = jsonObject.get("uData").toString();

		if (tableName == null || dataJson == null || uDataJson == null)
			return StandardResultTranslate.getResult(false, "Action_Update");

		Map<String, Object> map = JSONObject.parseObject(dataJson);
		map.remove("key");
		Map<String, Object> uMap = JSONObject.parseObject(uDataJson);
		uMap.remove("key");
		uMap.put("MUSER", user);
		uMap.put("MYSM", "M");
		uMap.put("MTIME", SystemTime.getTimestamp());
		StringBuffer fields = new StringBuffer("UPDATE " + tableName + " SET ");
		int len = uMap.keySet().size() + 1;
		Object[] objects = new Object[len];
		int i = 0;
		for (String field : uMap.keySet())
		{
			if (i > 0)
			{ fields.append(","); }
			fields.append(field + "=?");
			objects[i] = uMap.get(field);
			i++;
		}
		String sql = fields.append(" WHERE id=?").toString();
		objects[i] = map.get("ID");

		if (DBUtil.insertOrUpdate(sql, objects))
		{
			MCreateAction.insertOrUpdate("UPDATE usc_model_item SET mysm=? WHERE id=?",
					new Object[] { "M", map.get("ID") });
			map.putAll(uMap);
			return StandardResultTranslate.getResult("Action_Update_1", map);
		} else
		{
			return StandardResultTranslate.getResult(false, "Action_Update");
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public static Map<String, Object> updateDefaultc(String queryParam) {
		if (queryParam == null || queryParam.trim().equals("{}"))
			StandardResultTranslate.getResult(false, "Action_Update");
		JSONObject jsonObject = JSONObject.parseObject(queryParam);
		String user = jsonObject.containsKey("userName") ? jsonObject.getString("userName") : null;
		String tableName = jsonObject.containsKey("tableName") ? jsonObject.getString("tableName") : null;
		String dataJson = jsonObject.containsKey("data") ? jsonObject.get("data").toString() : null;

		if (tableName == null || dataJson == null)
			return StandardResultTranslate.getResult(false, "Action_Update");
		JdbcTemplate jdbcTemplate = DBConnecter.getModelJdbcTemplate();
		JSONObject dataJsonObject = JSONObject.parseObject(dataJson);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		List<Object> list = new ArrayList<Object>();
		list.add(1);
		list.add(user);
		list.add(timestamp);
		list.add(dataJsonObject.get("ID"));
		jdbcTemplate.update("UPDATE " + tableName + " SET DEFAULTC=?,MUSER=?,MTIME=? WHERE id=?", list.toArray());
		List<Object> list1 = list;
		list1.set(0, 0);
		list1.add(dataJsonObject.get("ITEMID"));
		jdbcTemplate.update(
				"UPDATE " + tableName + " SET DEFAULTC=?,MUSER=?,MTIME=? WHERE del=0 AND id<>? AND itemid=?",
				list.toArray());
		return StandardResultTranslate.getResult(true, "Action_Update");
	}

}
