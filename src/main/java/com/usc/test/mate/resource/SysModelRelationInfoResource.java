package com.usc.test.mate.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.usc.app.bs.resource.BaseResource;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.server.DBConnecter;
import com.usc.test.mate.action.MCreateRelationAction;
import com.usc.test.mate.action.MRelationQueryAction;
import com.usc.test.mate.action.MUpOrDown;
import com.usc.test.mate.action.MUpdateAction;
import com.usc.test.mate.action.service.ModelItemServer;

@RestController
@RequestMapping(value = "/ModelItemRelationInfo", produces = "application/json;charset=UTF-8")
public class SysModelRelationInfoResource extends BaseResource {

	@Autowired
	@Qualifier("modelJdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ModelItemServer modelItemServer;

	@Transactional
	@PostMapping("/createData")
	public Map<String, Object> createData(@RequestBody String queryParam) {
		return MCreateRelationAction.create(queryParam);
	}

	@Transactional
	@PostMapping("/delete")
	public Object delete(@RequestBody String queryParam) {

		return modelItemServer.deleteItem(queryParam);
	}

	@Transactional
	@PostMapping("/update")
	public Map<String, Object> update(@RequestBody String queryParam) {
		try
		{
			return MUpdateAction.update(queryParam);
		} catch (Exception e)
		{
			e.printStackTrace();
			return StandardResultTranslate.getResult(false, "Action_Update" + "\n" + e.getMessage());
		}
	}

	@PostMapping("/updateDefaultc")
	public Map<String, Object> updateDefaultc(@RequestBody String queryParam) {
		try
		{
			return MUpdateAction.updateDefaultc(queryParam);
		} catch (Exception e)
		{
			e.printStackTrace();
			return StandardResultTranslate.getResult(false, "Action_Update" + "\n" + e.getMessage());
		}
	}

	@PostMapping("/moveUpOrDown")
	public Map<String, Object> moveupordown(@RequestBody String queryParam) {
		try
		{
			return MUpOrDown.move(queryParam);
		} catch (Exception e)
		{
			e.printStackTrace();
			return StandardResultTranslate.getResult(false, "Action_Update" + "\n" + e.getMessage());
		}
	}

	@PostMapping("/moveMenu")
	public Map<String, Object> menumove(@RequestBody String queryParam) {
		try
		{
			return MUpOrDown.menuMove(queryParam);
		} catch (Exception e)
		{
			e.printStackTrace();
			return StandardResultTranslate.getResult(false, "Action_Update" + "\n" + e.getMessage());
		}
	}

	@PostMapping("/deleteMenu")
	public Map<String, Object> deleteMenu(@RequestBody String queryParam) {
		JSONObject jsonObject = JSONObject.parseObject(queryParam);
		String user = jsonObject.getString("userName");
		String tableName = jsonObject.getString("tableName");
		JSONObject data = JSONObject.parseObject(jsonObject.getString("data"));
		String id = data.getString("ID");
		String sql = "UPDATE " + tableName + " SET del=1,state='HS',effective=0,duser='" + user
				+ "',dtime=(SELECT NOW()) WHERE id='" + id + "'";

		try
		{
			jdbcTemplate.batchUpdate(sql);
			return StandardResultTranslate.getResult(true, "Action_Delete");
		} catch (Exception e)
		{
			e.printStackTrace();
			return StandardResultTranslate.getResult(false, "Action_Delete" + "\n" + e.getMessage());
		}
	}

	@GetMapping("/selectByCondition")
	public Object findModelFieldsPagingList(@RequestParam(value = "queryParam", required = false) String queryParam)
			throws JsonParseException, JsonMappingException, IOException {
		return MRelationQueryAction.query(queryParam);
	}

	@GetMapping("/getSysMenu")
	public Object getSysMenu(@RequestParam(value = "queryParam", required = false) String queryParam)
			throws JsonParseException, JsonMappingException, IOException {
		JSONObject jsonObject = JSONObject.parseObject(queryParam);

		String tableName = jsonObject.getString("tableName");
		Object[] objects = new Object[] { 0 };
		List<Map<String, Object>> dataList = DBConnecter.getModelJdbcTemplate()
				.queryForList("select * from " + tableName + " where del=? AND state<>'HS' ORDER BY sort", objects);
		return StandardResultTranslate.getQueryResult(true, "Action_Query", dataList);
	}

	@PostMapping("/createSysMenu")
	public Object add(@RequestBody String queryParam) {
		JSONObject jsonObject = JSONObject.parseObject(queryParam);
		String tableName = jsonObject.getString("tableName");
		String user = jsonObject.getString("userName");
		HashMap data = JSONObject.parseObject(jsonObject.get("data").toString(), HashMap.class);
		data.remove("key");
		List list = new ArrayList<HashMap>();
		list.add(data);

		return MCreateRelationAction.create(tableName, null, null, user, list);
	}

}
