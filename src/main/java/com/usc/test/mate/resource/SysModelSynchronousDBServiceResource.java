package com.usc.test.mate.resource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.usc.app.action.mate.MateFactory;
import com.usc.cache.redis.RedisUtil;
import com.usc.server.DBConnecter;
import com.usc.server.init.InitJurisdictionData;
import com.usc.server.md.GlobalGrid;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.ModelClassView;
import com.usc.server.md.ModelQueryView;
import com.usc.server.md.ModelRelationShip;
import com.usc.server.md.mapper.GlobalGridRowMapper;
import com.usc.server.md.mapper.ItemRowMapper;
import com.usc.server.md.mapper.ModelClassViewRowMapper;
import com.usc.server.md.mapper.ModelQueryViewRowMapper;
import com.usc.server.md.mapper.RelationShipRowMapper;
import com.usc.server.util.LoggerFactory;
import com.usc.test.mate.action.service.ModelServer;
import com.usc.util.ObjectHelperUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/modelSynchronous", produces = "application/json;charset=UTF-8")
@Slf4j
public class SysModelSynchronousDBServiceResource {

	private static RedisUtil redis = null;
	@Autowired
	private ModelServer modelServer;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private final String DATETIME = "DATETIME";
	private final String VARCHAR = "VARCHAR";
	private final String INT = "INT";
	private final String DOUBLE = "DOUBLE";
	private final String NUMBER = "NUMBER";
	private final String FLOAT = "FLOAT";
	private final String BOOLEAN = "BOOLEAN";
	private final String LONGTEXT = "LONGTEXT";

	/**
	 * <p>
	 * 新建model_item 对象数据同步模型数据到数据库
	 *
	 * @param queryParam
	 * @return
	 */

	@Transactional
	@PostMapping("/all")
	public Object synchronous(@RequestBody String params) {
		RedisUtil redis = RedisUtil.getInstanceOfString();
		String user = JSONObject.parseObject(params).getString("userName");
		JSONObject map = new JSONObject();
		if (redis.hasKey("OPENMODEL"))
		{

			String muser = (String) redis.get("OPENMODEL");
			if (!user.equals(muser))
			{
				map.put("flag", false);
				map.put("info", "用户：" + muser + " 正在建模，您无权操作建模数据");
				return map;
			}
		} else
		{
			map.put("flag", false);
			map.put("info", "请先开启建模工作权限");
			return map;
		}
		return synchronousDBRedis();
	}

	@Transactional
	private synchronized Object synchronousDBRedis() {
		JSONObject map = new JSONObject();
		LoggerFactory.logInfo("----------开始同步数据库相关数据----------");
		synchronousItemC();
		synchronousItemU();
		synchronousItemF();
		synchronousItemHS();
		synchronousOthers();
		LoggerFactory.logInfo("----------开始初始化模块权限相关数据----------");
		InitJurisdictionData.init();
		LoggerFactory.logInfo("----------同步工作完成----------");
		map.put("flag", true);
		map.put("info", "同步完成");
		return map;
	}

	private void synchronousOthers() {
		jdbcTemplate.batchUpdate("UPDATE USC_MODEL_NAVIGATION SET state='F' WHERE del=0 AND state='C'",
				"UPDATE USC_MODEL_MENU SET state='F' WHERE del=0 AND state='C'");
	}

	private void synchronousItemHS() {
		List<Map<String, Object>> itemsM = jdbcTemplate
				.queryForList("SELECT * FROM usc_model_item WHERE del=0 AND state='HS' AND effective=1");
		if (!ObjectHelperUtils.isEmpty(itemsM))
		{
			for (Map<String, Object> map : itemsM)
			{ SynchItemHS("usc_model_item", map); }
		}
	}

	/**
	 * <P>
	 * 同步已生效的业务对象修改后的所有信息（业务对象，字段，菜单，属性页，表格）
	 */
	private void synchronousItemU() {
		String[] modelTables = getModelTables();
		for (String table : modelTables)
		{
			List<Map<String, Object>> itemsM = jdbcTemplate
					.queryForList("SELECT * FROM " + table + " WHERE del=0 AND state='U'");
			if (!ObjectHelperUtils.isEmpty(itemsM))
			{
				for (Map<String, Object> itemMap : itemsM)
				{ SynchItemU(table, itemMap); }
			}

		}
	}

	/**
	 * <P>
	 * 同步已生效的业务对象新建的关联信息（字段，菜单，属性页，表格）
	 */
	private void synchronousItemF() {
		LoggerFactory.logInfo("----------开始同步新增字段对线数据库相关数据----------");
		String[] modelTables = getModelTables();
		for (String table : modelTables)
		{
			String sql = "SELECT * FROM " + table + " WHERE del=0 AND state='F'";
			if (!"USC_MODEL_GRID_GLOBAL".equals(table))
			{ sql += " AND effective<>1"; }
			List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
			if (!ObjectHelperUtils.isEmpty(maps))
			{
				for (Map<String, Object> map : maps)
				{ SynchItemF(table, map); }
			}
		}
	}

	/**
	 * <P>
	 * 同步新建未生效的业务对象新建的所有数据（业务对象，字段，菜单，属性页，表格）
	 */

	@Transactional(rollbackFor = Exception.class)
	private void synchronousItemC() {
		String[] modelTables = getModelTables();
		for (String table : modelTables)
		{
			List<Map<String, Object>> itemC = jdbcTemplate
					.queryForList("SELECT * FROM " + table + " WHERE del=0 AND state='C'");
			if (itemC != null && itemC.size() > 0)
			{
				for (Map<String, Object> itemMap : itemC)
				{ SynchItemC(table, itemMap); }
			}
		}

	}

	@PostMapping("/single")
	public Object singleSynchronization(@RequestBody String param) {
		JSONObject jsonObject = JSONObject.parseObject(param);
		String userName = jsonObject.getString("userName");
		Map<String, Object> result = new HashMap<String, Object>();
		if (!modelServer.isModelingUser(userName))
		{
			result.put("flag", false);
			result.put("info", "未开启建模，禁止操作同步功能");
			return result;
		}
		boolean b = false;

		JSONObject object = jsonObject.getJSONObject("data");
		String state = object.getString("STATE");
		String table = jsonObject.getString("tableName");
		if (ObjectHelperUtils.isEmpty(table))
		{
			result.put("info", "表对象参数不正确");
			return result;
		}
		table = table.toUpperCase();
		if ("USC_MODEL_NAVIGATION".equals(table))
		{
			b = SynchClassView();
		} else
		{
			if ("U".equals(state))
			{ b = SynchItemU(table, object); }
			if ("C".equals(state))
			{ b = SynchItemC(table, object); }
			if ("HS".equals(state))
			{
				b = SynchItemHS(table, object);

			}
			if ("F".equals(state))
			{
				if (0 == object.getIntValue("EFFECTIVE"))
				{ b = SynchItemF(table, object); }
			}
		}

		if (b)
		{
			result.put("info", "单条建模同步成功");
		} else
		{
			result.put("info", "单条建模同步失败");
		}
		result.put("flag", true);

		return result;

	}

	private boolean SynchClassView() {
		jdbcTemplate.batchUpdate("UPDATE USC_MODEL_NAVIGATION SET state='F' WHERE del=0 AND state<>'F'",
				"UPDATE USC_MODEL_ITEMMENU SET state='F' WHERE del=0 AND EXISTS(SELECT 1 FROM USC_MODEL_NAVIGATION WHERE "
						+ "del=0 AND id = USC_MODEL_ITEMMENU.itemid) AND state<>'F'");
		InitJurisdictionData.init();
		return true;
	}

	private boolean SynchItemC(String table, Map<String, Object> object) {
		boolean b = false;
		String id = (String) object.get("ID");
		if ("USC_MODEL_ITEM".equals(table))
		{
			String itemNo = (String) object.get("ITEMNO");
//			String name = (String) object.get("name");
			String tableName = (String) object.get("TABLENAME");

			String createTableSql = getCreateTableSql(object);
			if (createTableSql == null)
			{
				log.error("同步对象>>> " + itemNo + " 失败！原因：未正常创建表对象",
						new SQLException("failed: tableName >>>" + tableName));
			} else
			{
				try
				{
					jdbcTemplate.batchUpdate(createTableSql);
					execute("state='F',mysm=null", id, null);
					return initModel(table, id);
				} catch (Exception e)
				{
					e.printStackTrace();
					return b;
				}

			}
		}

		if ("USC_MODEL_CLASSVIEW".equals(table))
		{
			int[] is = jdbcTemplate
					.batchUpdate(new String[] { "UPDATE " + table + " SET state='F' ,effective=1 WHERE id='" + id + "'",
							"UPDATE usc_model_itemmenu SET state='F' WHERE del=0 AND itemid='" + id + "'",
							"UPDATE usc_model_classview_node SET state='F'  WHERE del=0 AND itemid='" + id + "'" });
			b = 1 == is[0];

			return b ? initModel(table, id) : false;
		}

		if ("USC_MODEL_GRID_GLOBAL".equals(table))
		{
			jdbcTemplate.batchUpdate(new String[] { "UPDATE " + table + " SET state='F'  WHERE id='" + id + "'",
					"UPDATE usc_model_grid_field SET state='F' WHERE del=0 AND rootid='" + id + "'" });
			return initModel(table, id);
		}

		int[] is = jdbcTemplate
				.batchUpdate(new String[] { "UPDATE " + table + " SET state='F' ,effective=1 WHERE id='" + id + "'",
						"UPDATE usc_model_itemmenu SET state='F' WHERE del=0 AND itemid='" + id + "'" });
		b = 1 == is[0];

		return b ? initModel(table, id) : false;
	}

	private boolean SynchItemU(String table, Map<String, Object> object) {
		List<String> sqls = new Vector<String>();
		boolean b = true;
		String id = (String) object.get("ID");
		Integer ver = (Integer) object.get("VER");
		if ("USC_MODEL_ITEM".equals(table))
		{
			String name = (String) object.get("NAME");
			String tableName = (String) object.get("TABLENAME");
			sqls.add("ALTER TABLE " + tableName + " COMMENT '" + name + "'");
			List<Map<String, Object>> fieldC = jdbcTemplate.queryForList("SELECT * FROM USC_MODEL_FIELD WHERE "
					+ "del=0 AND state='C' AND itemid='" + id + "' ORDER BY SORT");
			if (!CollectionUtils.isEmpty(fieldC))
			{ b = addFields(sqls, tableName, fieldC); }

			List<Map<String, Object>> fieldM = jdbcTemplate.queryForList("SELECT * FROM USC_MODEL_FIELD WHERE "
					+ "del=0 AND state='U' AND mysm='M' AND itemid='" + id + "' ORDER BY SORT");
			if (!CollectionUtils.isEmpty(fieldM))
			{ b = modifiyFields(sqls, tableName, fieldM); }

			sqls.add("UPDATE USC_MODEL_ITEM SET state='HS',effective=0,del=0 WHERE del=0 AND ver<>" + ver
					+ " AND tablename='" + tableName + "'");
			jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
			execute("state='F',mysm=null", id, null);
			return b ? initModel(table, id) : false;
		}

		String oldVerID = "(SELECT T.id FROM (SELECT id FROM USC_MODEL_RELATIONSHIP WHERE del=0 AND ver=" + (ver - 1)
				+ " AND no='" + object.get("no") + "') T)";
		if ("USC_MODEL_CLASSVIEW".equals(table))
		{
			int[] is = jdbcTemplate
					.batchUpdate(new String[] { "UPDATE " + table + " SET state='F' ,effective=1 WHERE id='" + id + "'",
							"UPDATE USC_MODEL_ITEMMENU SET state='F' WHERE del=0 AND itemid='" + id + "'",
							"UPDATE USC_MODEL_CLASSVIEW_NODE SET state='F'  WHERE del=0 AND itemid='" + id + "'",
							"UPDATE " + table + " SET state='HS',effective=0 WHERE id=" + oldVerID,
							"UPDATE USC_MODEL_ITEMMENU SET state='HS' WHERE del=0 AND itemid=" + oldVerID,
							"UPDATE USC_MODEL_CLASSVIEW_NODE SET state='HS'  WHERE del=0 AND itemid=" + oldVerID });
			b = 1 == is[0];
			return b ? initModel(table, id) : false;
		}

		int[] is = jdbcTemplate
				.batchUpdate(new String[] { "UPDATE " + table + " SET state='F' ,effective=1 WHERE id='" + id + "'",
						"UPDATE USC_MODEL_ITEMMENU SET state='F' WHERE del=0 AND itemid='" + id + "'",
						"UPDATE " + table + " SET state='HS',effective=0 WHERE id=" + oldVerID,
						"UPDATE USC_MODEL_ITEMMENU SET state='HS' WHERE del=0 AND itemid=" + oldVerID });
		b = 1 == is[0];

		return b ? initModel(table, id) : false;
	}

	private boolean SynchItemHS(String table, Map<String, Object> object) {
		RedisUtil redis = RedisUtil.getInstanceOfObject();
		boolean b = true;
		String id = (String) object.get("ID");

		Integer eff = (Integer) object.get("EFFECTIVE");
		if (1 == eff)
		{
			if ("USC_MODEL_ITEM".equals(table))
			{
				String itemNo = (String) object.get("ITEMNO");
				String tableName = (String) object.get("TABLENAME");
				int[] is = jdbcTemplate.batchUpdate(
						new String[] { "UPDATE USC_MODEL_ITEM SET state='HS' ,del=0,effective=0 WHERE id='" + id + "'",
								"UPDATE USC_MODEL_ITEMMENU SET state='HS' ,del=0 WHERE itemid='" + id + "'",
								"UPDATE USC_MODEL_FIELD SET state='HS' ,del=0 WHERE itemid='" + id + "'",
								"UPDATE USC_MODEL_GRID SET state='HS' ,del=0 WHERE itemid='" + id + "'",
								"UPDATE USC_MODEL_GRID_FIELD SET state='HS' ,del=0 WHERE itemid='" + id + "'",
								"UPDATE USC_MODEL_PROPERTY SET state='HS' ,del=0 WHERE itemid='" + id + "'",
								"UPDATE USC_MODEL_PROPERTY_FIELD SET state='HS' ,del=0 WHERE itemid='" + id + "'",
								"UPDATE USC_MODEL_RELATIONPAGE SET state='HS' ,del=0 WHERE itemid='" + id + "'",
								"UPDATE USC_MODEL_RELATIONPAGE_SIGN SET state='HS' ,del=0 WHERE itemid='" + id + "'" });
				if (1 == is[0])
				{
					ItemInfo itemInfo = jdbcTemplate
							.queryForObject("SELECT * FROM USC_MODEL_ITEM WHERE id='" + id + "'", new ItemRowMapper());
					if (itemInfo != null)
					{
						redis.hdel("MODEL_ITEMDATA", itemNo);
						redis.hdel("MODEL_ITEMDATABYTABLE", tableName);
					}
				}
			}
			if ("USC_MODEL_CLASSVIEW".equals(table))
			{
				int[] is = jdbcTemplate.batchUpdate(new String[] {
						"UPDATE " + table + " SET state='HS' ,effective=0 WHERE id='" + id + "'",
						"UPDATE USC_MODEL_ITEMMENU SET state='HS' WHERE del=0 AND itemid='" + id + "'",
						"UPDATE USC_MODEL_CLASSVIEW_NODE SET state='HS'  WHERE del=0 AND itemid='" + id + "'", });
				b = (1 == is[0]);
				if (b)
				{ redis.hdel("MODEL_QUERYVIEWDATA", object.get("NO")); }
				return b;
			}

			int[] is = jdbcTemplate.batchUpdate(
					new String[] { "UPDATE " + table + " SET state='HS' ,effective=0 WHERE id='" + id + "'",
							"UPDATE USC_MODEL_ITEMMENU SET state='HS' WHERE del=0 AND itemid='" + id + "'" });
			b = (1 == is[0]);
			if ("USC_MODEL_RELATIONSHIP".equals(table))
			{

				if (b)
				{ redis.hdel("MODEL_RELATIONSHIPDATA", object.get("NO")); }
			}
			if ("USC_MODEL_QUERYVIEW".equals(table))
			{
				if (b)
				{ redis.hdel("MODEL_QUERYVIEWDATA", object.get("NO")); }
			}
		}
		return false;

	}

	private boolean SynchItemF(String table, Map<String, Object> object) {
		boolean b = true;
		String id = (String) object.get("ID");

		if ("USC_MODEL_ITEM".equals(table))
		{
			int[] is = jdbcTemplate.batchUpdate(
					new String[] { "UPDATE USC_MODEL_ITEM SET state='F' ,del=0,effective=1 WHERE id='" + id + "'",
							"UPDATE USC_MODEL_ITEMMENU SET state='F' ,del=0 WHERE itemid='" + id + "'",
							"UPDATE USC_MODEL_FIELD SET state='F' ,del=0 WHERE itemid='" + id + "'",
							"UPDATE USC_MODEL_GRID SET state='F' ,del=0 WHERE itemid='" + id + "'",
							"UPDATE USC_MODEL_GRID_FIELD SET state='F' ,del=0 WHERE itemid='" + id + "'",
							"UPDATE USC_MODEL_PROPERTY SET state='F' ,del=0 WHERE itemid='" + id + "'",
							"UPDATE USC_MODEL_PROPERTY_FIELD SET state='F' ,del=0 WHERE itemid='" + id + "'",
							"UPDATE USC_MODEL_RELATIONPAGE SET state='F' ,del=0 WHERE itemid='" + id + "'",
							"UPDATE USC_MODEL_RELATIONPAGE_SIGN SET state='F' ,del=0 WHERE itemid='" + id + "'" });
			b = (1 == is[0]);
			return b ? initModel(table, id) : false;
		}

		if ("USC_MODEL_CLASSVIEW".equals(table))
		{
			int[] is = jdbcTemplate
					.batchUpdate(new String[] { "UPDATE " + table + " SET state='F' ,effective=1 WHERE id='" + id + "'",
							"UPDATE usc_model_itemmenu SET state='F' WHERE del=0 AND itemid='" + id + "'",
							"UPDATE usc_model_classview_node SET state='F'  WHERE del=0 AND itemid='" + id + "'" });
			b = (1 == is[0]);

			return b ? initModel(table, id) : false;
		}

		if ("USC_MODEL_GRID_GLOBAL".equals(table))
		{
			jdbcTemplate.batchUpdate(new String[] { "UPDATE " + table + " SET state='F'  WHERE id='" + id + "'",
					"UPDATE usc_model_grid_field SET state='F' WHERE del=0 AND rootid='" + id + "'" });
			return initModel(table, id);
		}

		int[] is = jdbcTemplate
				.batchUpdate(new String[] { "UPDATE " + table + " SET state='F' ,effective=1 WHERE id='" + id + "'",
						"UPDATE usc_model_itemmenu SET state='F' WHERE del=0 AND itemid='" + id + "'" });
		b = (1 == is[0]);

		return b ? initModel(table, id) : false;
	}

	private boolean addFields(List<String> slqs, String tableName, List<Map<String, Object>> fieldsC) {
		if (ObjectHelperUtils.isEmpty(tableName) || ObjectHelperUtils.isEmpty(fieldsC))
		{ return false; }
		String alterTable = "ALTER TABLE ";
		String add = " ADD ";
		for (Map<String, Object> addField : fieldsC)
		{
			String field = getFieldSql(addField);
//			field = field.substring(0, field.length() - 1);
			String alterSql = (alterTable + tableName + add + field);
			System.out.println(alterSql);
			slqs.add(alterSql);
		}
		return true;
	}

	private boolean modifiyFields(List<String> sqls, String tableName, List<Map<String, Object>> fieldsM) {
		if (ObjectHelperUtils.isEmpty(tableName) || ObjectHelperUtils.isEmpty(fieldsM))
		{ return false; }
		String alterTable = "ALTER TABLE ";
		String modify = " MODIFY ";
		for (Map<String, Object> modifyField : fieldsM)
		{
			String field = getFieldSql(modifyField);

//			try
//			{
//				field = field.substring(0, field.length() - 1);
//			} catch (Exception e)
//			{
//				System.out.println(field);
//				System.out.println(modifyField.get("FIELDNAME"));
//			}

			String alterSql = alterTable + tableName + modify + field;
			System.out.println(alterSql);
			sqls.add(alterSql);
		}
		return true;
	}

	protected void execute(String setString, String itemID, String condition) {
		condition = (condition != null) ? (" AND state='" + condition + "'") : "";
		String item = "UPDATE USC_MODEL_ITEM SET effective=1," + setString + " WHERE id ='" + itemID + "'" + condition;
		String field = "UPDATE USC_MODEL_FIELD SET " + setString + " WHERE del=0  AND itemid ='" + itemID + "'"
				+ condition;
		String itemmenu = "UPDATE USC_MODEL_ITEMMENU SET " + setString + " WHERE del=0  AND itemid = '" + itemID + "'"
				+ condition;
		String grid = "UPDATE USC_MODEL_GRID SET " + setString + " WHERE del=0 AND itemid = '" + itemID + "'"
				+ condition;
		String gridField = "UPDATE USC_MODEL_GRID_FIELD SET " + setString + " WHERE del=0 AND itemid ='" + itemID + "'"
				+ condition;
		String property = "UPDATE USC_MODEL_PROPERTY SET " + setString + " WHERE del=0 AND itemid ='" + itemID + "'"
				+ condition;
		String propertyField = "UPDATE USC_MODEL_PROPERTY_FIELD SET " + setString + " WHERE del=0 AND itemid ='"
				+ itemID + "'" + condition;
		String relationPage = "UPDATE USC_MODEL_RELATIONPAGE SET " + setString + " WHERE del=0 AND itemid ='" + itemID
				+ "'" + condition;
		String relationPageSign = "UPDATE USC_MODEL_RELATIONPAGE_SIGN SET " + setString + " WHERE del=0 AND itemid ='"
				+ itemID + "'" + condition;
		String[] sql = new String[9];
		sql[0] = item;
		sql[1] = field;
		sql[2] = itemmenu;
		sql[3] = grid;
		sql[4] = gridField;
		sql[5] = property;
		sql[6] = propertyField;
		sql[7] = relationPage;
		sql[8] = relationPageSign;
		jdbcTemplate.batchUpdate(sql);

	}

	private String getCreateTableSql(Map<String, Object> itemMap) {
		String itemid = (String) itemMap.get("ID");
		List<Map<String, Object>> fieldC = jdbcTemplate.queryForList("SELECT * FROM USC_MODEL_FIELD WHERE "
				+ "del=0 AND state='C' AND itemid='" + itemid + "' ORDER BY SORT");
		if (fieldC == null || fieldC.isEmpty())
			return null;
		String tableName = (String) itemMap.get("TABLENAME");
		int type = (Integer) itemMap.get("TYPE");
		String createL = "CREATE TABLE " + tableName + "(";
		String name = (String) itemMap.get("NAME");
		StringBuffer cSql = new StringBuffer("");
		String pk = "	PRIMARY KEY (ID)";
		String tableRemark = ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '" + name + "' ";

		cSql.append(createL);
		for (Map<String, Object> fieldMap : fieldC)
		{
			String field = getFieldSql(fieldMap) + ",";
			cSql.append("\n	" + field);
		}
		cSql.append("\n" + pk);
		if (type == 2)
		{
			cSql.append(",");
			String sql2 = "\n" + "  KEY INDEX_" + tableName.toUpperCase() + "_ITEMA (itema) USING BTREE,\n"
					+ "  KEY INDEX_" + tableName.toUpperCase() + "_ITEMAID (itemaid) USING BTREE,\n" + "  KEY INDEX_"
					+ tableName.toUpperCase() + "_ITEMAB (itemb) USING BTREE,\n" + "  KEY INDEX_"
					+ tableName.toUpperCase() + "_ITEMBID (itembid) USING BTREE\n";
			cSql.append(sql2);
		} else if (type == 3)
		{
			cSql.append(",");
			String sql3 = "\n" + "  KEY INDEX_" + tableName.toUpperCase() + "_ITEMA (itemid) USING BTREE,\n"
					+ "  KEY INDEX_" + tableName.toUpperCase() + "_ITEMAID (nodeid) USING BTREE\n";
			cSql.append(sql3);
		}
		cSql.append(tableRemark + ";");
		System.out.println(cSql);
		return cSql.toString();

	}

	private String getFieldSql(Map<String, Object> fieldMap) {
		String fname = (String) fieldMap.get("FIELDNAME"); // 字段名
		String name = (String) fieldMap.get("NAME"); // 字段名
		String ftype = (String) fieldMap.get("FTYPE"); // 字段类型
		Object flength = fieldMap.get("FLENGTH"); // 字段长度
		Object accuracy = fieldMap.get("ACCURACY"); // 字段精度

		String sql = "";
		if (ftype.equals(DATETIME))
		{
			sql = fname + " TIMESTAMP ";
		} else if (ftype.equals(VARCHAR))
		{
			sql = fname.equals("ID") ? (fname + " VARCHAR (" + flength + ") NOT NULL ")
					: (fname + " VARCHAR (" + flength + ") DEFAULT NULL ");
		} else if (ftype.equals(LONGTEXT))
		{
			String type = "LONGTEXT";
			if (DBConnecter.isOracle())
			{ type = "CLOB"; }
			sql = (fname + " " + type + " DEFAULT NULL ");
		} else if (ftype.equals(INT))
		{
			Object _flength = flength == null ? 11 : flength;
			sql = fname + " INT(" + _flength + ") DEFAULT NULL ";
		} else if (ftype.equals(FLOAT))
		{
			Object _flength = flength == null ? 12 : flength;
			Object _accuracy = accuracy == null ? 1 : accuracy;
			sql = fname + " FLOAT(" + _flength + "," + _accuracy + ") DEFAULT NULL ";
		} else if (ftype.equals(DOUBLE))
		{
			Object _flength = flength == null ? 13 : flength;
			Object _accuracy = accuracy == null ? 4 : accuracy;
			sql = fname + " DOUBLE(" + _flength + "," + _accuracy + ") DEFAULT NULL ";
		} else if (ftype.equals(NUMBER))
		{
			Object _flength = flength == null ? 13 : flength;
			Object _accuracy = accuracy == null ? 4 : accuracy;
			sql = fname + " NUMERIC(" + _flength + "," + _accuracy + ") DEFAULT NULL ";
		} else if (ftype.equals(BOOLEAN))
		{ sql = fname + " INT(1) DEFAULT NULL "; }
		return sql + " COMMENT '" + name + "'";

	}

	private String[] getModelTables() {
		return new String[] { "USC_MODEL_ITEM", "USC_MODEL_RELATIONSHIP", "USC_MODEL_QUERYVIEW", "USC_MODEL_CLASSVIEW",
				"USC_MODEL_GRID_GLOBAL" };
	}

	private boolean initModel(String table, String id) {
		redis = RedisUtil.getInstanceOfObject();
		boolean b = true;
		if ("USC_MODEL_ITEM".equals(table))
		{
			b = doItemInfo(id);

		}
		if ("USC_MODEL_CLASSVIEW".equals(table))
		{
			b = doModelClassView(id);

		}
		if ("USC_MODEL_RELATIONSHIP".equals(table))
		{
			b = doModelRelationShip(id);

		}
		if ("USC_MODEL_QUERYVIEW".equals(table))
		{
			b = doModelQueryView(id);

		}

		if ("USC_MODEL_GRID_GLOBAL".equals(table))
		{
			b = doGlobalGrid(id);

		}
		return b;

	}

	private boolean doItemInfo(String id) {
		boolean b = false;
		ItemInfo itemInfo = jdbcTemplate.queryForObject("SELECT * FROM usc_model_item WHERE id='" + id + "'",
				new ItemRowMapper());
		MateFactory.removeItemInfoCache(itemInfo.getItemNo());
		b = redis.hset("MODEL_ITEMDATA", itemInfo.getItemNo(), itemInfo);
		b = redis.hset("MODEL_ITEMDATABYTABLE", itemInfo.getTableName(), itemInfo);
		return b;
	}

	private boolean doModelRelationShip(String id) {
		ModelRelationShip relationShipInfo = jdbcTemplate.queryForObject(
				"SELECT * FROM USC_MODEL_RELATIONSHIP WHERE id='" + id + "'", new RelationShipRowMapper());
		MateFactory.removeRelationShipCache(relationShipInfo.getNo());
		return redis.hset("MODEL_RELATIONSHIPDATA", relationShipInfo.getNo(), relationShipInfo);
	}

	private boolean doModelClassView(String id) {
		ModelClassView modelClassView = jdbcTemplate.queryForObject(
				"SELECT * FROM USC_MODEL_CLASSVIEW WHERE id='" + id + "'", new ModelClassViewRowMapper());
		MateFactory.removClassViewCache(modelClassView.getNo());
		return redis.hset("MODEL_QUERYVIEWDATA", modelClassView.getNo(), modelClassView);
	}

	private boolean doModelQueryView(String id) {
		ModelQueryView modelQueryView = jdbcTemplate.queryForObject(
				"SELECT * FROM USC_MODEL_QUERYVIEW WHERE id='" + id + "'", new ModelQueryViewRowMapper());
		MateFactory.removQueryViewCache(modelQueryView.getNo());
		return redis.hset("MODEL_QUERYVIEWDATA", modelQueryView.getNo(), modelQueryView);
	}

	private boolean doGlobalGrid(String id) {
		GlobalGrid globalGrid = jdbcTemplate.queryForObject("SELECT * FROM USC_MODEL_GRID_GLOBAL WHERE id='" + id + "'",
				new GlobalGridRowMapper());
		MateFactory.remoGlobalGridCache(globalGrid.getNo());
		return redis.hset("MODELGLOBALGRID", globalGrid.getNo(), globalGrid);
	}
}
