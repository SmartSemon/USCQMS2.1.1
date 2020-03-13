package com.usc.test.mate.resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.dto.Dto;
import com.usc.dto.impl.MapDto;
import com.usc.server.jdbc.DBUtil;
import com.usc.test.mate.action.MCreateAction;
import com.usc.test.mate.action.service.ModelItemServer;
import com.usc.test.mate.action.service.ModelServer;
import com.usc.util.ObjectHelperUtils;

@RestController
@RequestMapping(value = "/sysModelItem", produces = "application/json;charset=UTF-8")
public class SysModelItemResource {

	@Autowired
	private ModelServer modelServer;
	@Autowired
	private ModelItemServer modelItemServer;

	@PostMapping("/create")
	public Object create(@RequestBody String queryParam) {
		return MCreateAction.createModelObj(queryParam);
	}

	@PostMapping("/delete")
	public Dto delete(@RequestBody String queryParam) {
		modelItemServer.deleteItem(queryParam);
		return new MapDto("flag", "sucess");
	}

	@GetMapping("/packet")
	public Object findPagingList(@RequestParam(value = "queryParam", required = false) String queryParam)
			throws JsonParseException, JsonMappingException, IOException {
		JSONObject jsonObject = JSONObject.parseObject(queryParam);
		String userName = jsonObject.getString("userName");
		String table = jsonObject.getString("tableName");
		String queryCondition = ObjectHelperUtils.isNotEmpty(jsonObject.getString("condition"))
				? " AND " + jsonObject.getString("condition")
				: "";

		StringBuffer condition = modelServer.isModelingUser(userName)
				? new StringBuffer(
						"del=0 AND effective<>-1 AND (state='F' OR (cuser='" + userName + "' AND state IN('C','U')))")
				: new StringBuffer("del=0 AND effective <>0 AND state='F'");
		List<Map<String, Object>> dataList = DBUtil.queryForList(table, condition.append(queryCondition).toString());
		return StandardResultTranslate.getQueryResult(true, "Action_Query", dataList);
	}

	@GetMapping("/getAbolishedItem")
	public Object getAbolishedItem(@RequestParam(value = "queryParam", required = false) String queryParam)
			throws JsonParseException, JsonMappingException, IOException {
		JSONObject jsonObject = JSONObject.parseObject(queryParam);
		String table = jsonObject.getString("tableName");

		List<Map<String, Object>> dataList = DBUtil.queryForList(table, "state='HS' AND effective IN(-1,0,1)");
		return StandardResultTranslate.getQueryResult(true, "Action_Query", dataList);
	}

	@PostMapping("/recovery")
	public Object recoveryItem(@RequestBody String queryParam) {
		return modelItemServer.recoveryItem(queryParam);

	}

	@GetMapping("/query")
	public Object queryItemList(@RequestParam(value = "queryParam", required = false) String queryParam)
			throws JsonParseException, JsonMappingException, IOException {
		JSONObject jsonObject = JSONObject.parseObject(queryParam);
		String userName = jsonObject.getString("userName");
		String table = jsonObject.getString("tableName");
		String queryWord = jsonObject.getString("queryWord");
		String queryCondition = "";
		StringBuffer condition = new StringBuffer("del=0");
		if (table.equals("usc_model_item") || table.equals("usc_model_relationship")
				|| table.equals("usc_model_queryview") || table.equals("usc_model_classview"))
		{
			if (modelServer.isModelingUser(userName))
			{
				condition.append(
						" AND effective IN(1,0) AND (state='F' OR (cuser='" + userName + "' AND state IN('C','U')))");
			} else
			{
				condition.append(" AND effective IN(1,-1) AND state='F'");
			}
		} else
		{
			if (modelServer.isModelingUser(userName))
			{
				condition.append(" AND state IN('F','C','U')");
			} else
			{
				condition.append(" AND state='F'");
			}

		}
		List<Map<String, Object>> dataList = null;
		if (ObjectHelperUtils.isNotEmpty(queryWord))
		{
			queryWord = queryWord.toUpperCase();
			String field = "UPPER(itemno)";
			if (!table.equals("usc_model_item"))
			{ field = "UPPER(no)"; }
			queryCondition = " AND (" + field + " LIKE '%" + queryWord + "%' OR UPPER(name) LIKE '%" + queryWord
					+ "%' )";
		}
		dataList = DBUtil.queryForList(table, condition.append(queryCondition).toString());
		return StandardResultTranslate.getQueryResult(true, "Action_Query", dataList);
	}

	@GetMapping("/queryItemPGR")
	public Object getItemList(@RequestParam(value = "queryParam", required = false) String queryParam)
			throws JsonParseException, JsonMappingException, IOException {
		JSONObject jsonObject = JSONObject.parseObject(queryParam);
		String userName = jsonObject.getString("userName");
		String table = jsonObject.getString("tableName");
		String itemNo = jsonObject.getString("itemNo");
		StringBuffer condition = new StringBuffer("del=0");
		if (table.equals("usc_model_item") || table.equals("usc_model_relationship")
				|| table.equals("usc_model_queryview") || table.equals("usc_model_classview"))
		{
			if (modelServer.isModelingUser(userName))
			{
				condition.append(" AND effective IN(1,0) AND state IN('F','C','U')");
			} else
			{
				condition.append(" AND effective IN(1,-1) AND state='F'");
			}
		} else
		{
			if (modelServer.isModelingUser(userName))
			{
				condition.append(" AND state IN('F','C','U')");
			} else
			{
				condition.append(" AND state='F'");
			}

		}
		List<Map<String, Object>> dataList = DBUtil.queryForList(table,
				condition.append(" AND itemid=(SELECT id FROM usc_model_item WHERE del=0 AND itemno='" + itemNo
						+ "' ORDER BY ctime DESC LIMIT 0,1)").toString());
		return StandardResultTranslate.getQueryResult(true, "Action_Query", dataList);
	}

	@GetMapping("/queryItemFields")
	public Object getItemFields(@RequestParam(value = "queryParam", required = false) String queryParam) {
		JSONObject jsonObject = JSONObject.parseObject(queryParam);
		String table = "USC_MODEL_FIELD";
		String propertyNo = jsonObject.getString("propertyPageNo");
		String itemNo = jsonObject.getString("itemNo");
		String sql = " DEL=0";
		String itemidsql = "(SELECT id FROM usc_model_item WHERE del=0 AND itemno='" + itemNo
				+ "' ORDER BY ctime DESC LIMIT 0,1)";
		if (propertyNo == null)
		{
			sql += " AND itemid=" + itemidsql;
		} else
		{
			table = "USC_MODEL_PROPERTY_FIELD";
			sql += " AND ROOTID = (SELECT ID FROM USC_MODEL_PROPERTY WHERE DEL=0 AND NO='" + propertyNo
					+ "' AND itemid=" + itemidsql + ")";
		}
		List<Map<String, Object>> dataList = DBUtil.queryForList(table, sql);
		return dataList;
	}
}
