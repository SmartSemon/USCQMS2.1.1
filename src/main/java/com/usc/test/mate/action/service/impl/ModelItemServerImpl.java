package com.usc.test.mate.action.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.cache.redis.RedisUtil;
import com.usc.test.mate.action.MCreateAction;
import com.usc.test.mate.action.MUpdateAction;
import com.usc.test.mate.action.service.ModelItemServer;
import com.usc.util.ObjectHelperUtils;

@Service("modelItemServer")
public class ModelItemServerImpl implements ModelItemServer {
	@Autowired
	@Qualifier("modelJdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Override
	public Object createItem(String params) {

		return MCreateAction.createModelObj(params);
	}

	@Override
	public Object updateItem(String params) {
		try
		{
			return MUpdateAction.update(params);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object deleteItem(String params) {
		JSONObject jsonObject = JSONObject.parseObject(params);
		String user = jsonObject.getString("userName");
		JSONObject map = new JSONObject();
		if (isModelingUser(user))
		{
			String tableName = jsonObject.getString("tableName");
			String state = jsonObject.getString("STATE") != null ? jsonObject.getString("STATE")
					: jsonObject.getString("state");
			List<JSONObject> jsonArray = JSONArray.parseArray(jsonObject.getString("data"), JSONObject.class);
			for (JSONObject object : jsonArray)
			{
				state = state != null ? state : object.getString("STATE");
				String id = object.getString("ID");

				if ("C".equals(state))
				{ deleteC(tableName, id); }
				if ("U".equals(state))
				{ deleteU(tableName, id, object.getIntValue("VER")); }
				if ("F".equals(state))
				{ deleteF(tableName, id, user); }
			}

			map.put("flag", true);
			map.put("info", "删除成功");
			map.put("sign", "D");
		} else
		{
			map.put("flag", false);
			map.put("info", "请先开启建模工作权限");
			return map;
		}

		return new ActionMessage(true, RetSignEnum.DELETE, StandardResultTranslate.translate("Action_Delete_1"));
	}

	@Override
	public Object recoveryItem(String params) {
		JSONObject jsonObject = JSONObject.parseObject(params);
		String table = jsonObject.getString("tableName");
		List<JSONObject> jsonArray = JSONArray.parseArray(jsonObject.getString("data"), JSONObject.class);
		for (JSONObject object : jsonArray)
		{
			String id = object.getString("ID");
			Integer eff = object.getInteger("EFFECTIVE");
			if (eff == 1)
			{
				jdbcTemplate.batchUpdate("UPDATE " + table + " SET state='F' WHERE id='" + id + "'");
			} else
			{
				recovery(table, id);
			}

		}
		return new ActionMessage(true, RetSignEnum.DELETE, "恢复成功", null);
	}

	private void deleteC(String tableName, String id) {
		String[] sqls = null;
		if (tableName.equals("usc_model_field") || tableName.equals("usc_model_navigation")
				|| tableName.equals("usc_model_grid_field") || tableName.equals("usc_model_property_field")
				|| tableName.equals("usc_model_relationpage_sign") || tableName.equals("usc_model_classview_node"))
		{
			jdbcTemplate.batchUpdate("DELETE FROM " + tableName + " WHERE id='" + id + "'");
			return;
		}
		if (tableName.equals("usc_model_itemmenu"))
		{
			deleteMenus(id);
			jdbcTemplate.batchUpdate("DELETE FROM " + tableName + " WHERE id='" + id + "'");
		}
		if (tableName.equals("usc_model_grid") || tableName.equals("usc_model_property"))
		{
			jdbcTemplate.batchUpdate("DELETE FROM " + tableName + "_field WHERE itemid='" + id + "'",
					"DELETE FROM " + tableName + " WHERE id='" + id + "'");
			return;
		}
		if (tableName.equals("usc_model_relationpage"))
		{
			jdbcTemplate.batchUpdate("DELETE FROM " + tableName + "_sign WHERE itemid='" + id + "'",
					"DELETE FROM " + tableName + " WHERE id='" + id + "'");
			return;
		}
		if (tableName.equals("usc_model_item"))
		{
			sqls = new String[] { "DELETE FROM usc_model_item WHERE id='" + id + "'",
					"DELETE FROM usc_model_field WHERE itemid='" + id + "'",
					"DELETE FROM usc_model_itemmenu WHERE itemid='" + id + "'",
					"DELETE FROM usc_model_grid WHERE itemid='" + id + "'",
					"DELETE FROM usc_model_grid_field WHERE itemid='" + id + "'",
					"DELETE FROM usc_model_property WHERE itemid='" + id + "'",
					"DELETE FROM usc_model_property_field WHERE itemid='" + id + "'",
					"DELETE FROM usc_model_relationpage WHERE itemid='" + id + "'",
					"DELETE FROM usc_model_relationpage_sign WHERE itemid='" + id + "'" };
		}
		if (tableName.equals("usc_model_relationship") || tableName.equals("usc_model_queryview"))
		{
			sqls = new String[] { "DELETE FROM " + tableName + " WHERE id='" + id + "'",
					"DELETE FROM usc_model_itemmenu WHERE itemid='" + id + "'" };
		}
		if (tableName.equals("usc_model_classview"))
		{
			sqls = new String[] { "DELETE FROM usc_model_classview WHERE id='" + id + "'",
					"DELETE FROM usc_model_classview_node WHERE itemid='" + id + "'",
					"DELETE FROM usc_model_itemmenu WHERE itemid='" + id + "'" };
		}
		if (sqls != null)
		{ jdbcTemplate.batchUpdate(sqls); }

	}

	private void deleteU(String tableName, String id, int ver) {
		if (tableName.equals("usc_model_field") || tableName.equals("usc_model_itemmenu")
				|| tableName.equals("usc_model_grid_") || tableName.equals("usc_model_grid_field")
				|| tableName.equals("usc_model_property") || tableName.equals("usc_model_property_field")
				|| tableName.equals("usc_model_relationpage") || tableName.equals("usc_model_relationpage_sign")
				|| tableName.equals("usc_model_classview_node"))
		{
			deleteC(tableName, id);
			return;
		}
		Integer v = ver - 1;
		if (tableName.equals("usc_model_item"))
		{
			jdbcTemplate.batchUpdate("UPDATE " + tableName + " SET state='F',del=0,effective=1 WHERE del=0 AND ver=" + v
					+ " AND tablename=(SELECT T.tablename FROM (SELECT tablename FROM " + tableName + " WHERE id='" + id
					+ "') T)");
		} else
		{
			jdbcTemplate.batchUpdate("UPDATE " + tableName + " SET state='F',del=0,effective=1 WHERE del=0 AND ver=" + v
					+ " AND no=(SELECT T.no FROM (SELECT no FROM " + tableName + " WHERE id='" + id + "') T)");
		}
		deleteC(tableName, id);
	}

	private void deleteF(String tableName, String itemID, String user) {
		jdbcTemplate.batchUpdate("UPDATE " + tableName + " SET del=1, state='HS', duser='" + user
				+ "',dtime=(select now()) WHERE id='" + itemID + "'");
	}

	private void recovery(String tableName, String id) {
		String[] sqls = null;
		if (tableName.equals("usc_model_item"))
		{
			sqls = new String[] { "UPDATE usc_model_item SET state='F', del=0, effective=-2 WHERE id='" + id + "'",
					"UPDATE usc_model_itemmenu SET state='F', del=0 WHERE itemid='" + id + "'",
					"UPDATE usc_model_grid SET state='F', del=0 WHERE itemid='" + id + "'",
					"UPDATE usc_model_grid_field SET state='F', del=0 WHERE itemid='" + id + "'",
					"UPDATE usc_model_property SET state='F', del=0 WHERE itemid='" + id + "'",
					"UPDATE usc_model_property_field SET state='F', del=0 WHERE itemid='" + id + "'",
					"UPDATE usc_model_relationpage SET state='F', del=0 WHERE itemid='" + id + "'",
					"UPDATE usc_model_relationpage_sign SET state='F', del=0 WHERE itemid='" + id + "'" };
		}
		if (tableName.equals("usc_model_relationship") || tableName.equals("usc_model_queryview"))
		{
			sqls = new String[] { "UPDATE " + tableName + " SET state='F', del=0, effective=-2 WHERE id='" + id + "'",
					"UPDATE usc_model_itemmenu SET state='F', del=0 WHERE itemid='" + id + "'" };
		}
		if (tableName.equals("usc_model_classview"))
		{
			sqls = new String[] { "UPDATE " + tableName + " SET state='F', del=0, effective=-2 WHERE id='" + id + "'",
					"UPDATE usc_model_node SET state='F', del=0 WHERE itemid='" + id + "'",
					"UPDATE usc_model_itemmenu SET state='F', del=0 WHERE itemid='" + id + "'" };
		}

		jdbcTemplate.batchUpdate(sqls);
	}

	private void deleteMenus(String pid) {
		List<Map<String, Object>> chrildMensList = jdbcTemplate
				.queryForList("SELECT id,mtype FROM usc_model_itemmenu WHERE del=0 AND pid='" + pid + "'");
		if (ObjectHelperUtils.isNotEmpty(chrildMensList))
		{
			for (Map<String, Object> map : chrildMensList)
			{
				String cid = (String) map.get("id");
				Integer mt = (Integer) map.get("mtype");
				if (mt == 1)
				{ deleteMenus(cid); }
				jdbcTemplate.batchUpdate("DELETE FROM usc_model_itemmenu WHERE id='" + cid + "'");
			}
		}
	}

	public boolean isModelingUser(String userName) {
		if (userName != null)
		{
			RedisUtil redis = RedisUtil.getInstanceOfObject();
			String user = (String) redis.hget("OPENMODEL", userName);
			if (user != null)
			{ return true; }
		}

		return false;
	}

}
