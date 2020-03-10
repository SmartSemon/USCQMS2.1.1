package com.usc.server.md.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.usc.server.DBConnecter;
import com.usc.server.md.ItemGridField;
import com.usc.server.md.ItemMenu;
import com.usc.server.md.ItemPageField;
import com.usc.server.md.ItemPeptidePage;
import com.usc.server.md.ItemRelationPageSign;
import com.usc.server.md.MenuLibrary;
import com.usc.util.ObjectHelperUtils;

/**
 *
 * <p>
 * Title: BeanFactoryConverter
 * </p>
 *
 * <p>
 * Description: JavaBean转换器
 * </p>
 *
 * @author PuTianXiong
 *
 * @date 2019年4月26日
 *
 */
public class ItemRelBeanFactoryConverter {

	protected static JdbcTemplate jdbcTemplate;
	protected static List<ItemPeptidePage> itemPeptidePageList;
	protected static List<ItemPageField> itemPageFieldList;
	private static List<ItemGridField> itemGridFieldList;
	private static List<ItemRelationPageSign> itemRelationPageSignList;
	private static List<ItemMenu> itemRelationMenuList;

	/**
	 * @param calss
	 * @param resultSet 返回单个实体对象
	 * @return
	 */
	public static <T> T getBean(Class<T> calss, ResultSet resultSet) {
		try
		{
			jdbcTemplate = new JdbcTemplate(DBConnecter.getDataSource());
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		T object = null;
		try
		{
			object = createBean(calss, resultSet);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return object;
	}

	/**
	 * @see 返回实体对象集
	 */
	public static <T> List<T> getBeans(Class<T> calss, ResultSet resultSet) {
		List<T> ts = null;
		try
		{
			ts = new ArrayList<>();
			while (resultSet.next())
			{ ts.add(createBean(calss, resultSet)); }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return ts;
	}

	/**
	 * @说明 创建实体
	 * @param calss
	 * @param resultSet
	 * @return 实体对象
	 * @throws Exception
	 */
	private static <T> T createBean(Class<T> calss, ResultSet resultSet) throws Exception {
		T object = calss.newInstance();
		String className = calss.getSimpleName();
		// 获取字段
		Field[] fields = calss.getDeclaredFields();
		String rootId = resultSet.getString("id");
		String itemId = null;
		try
		{
			itemId = resultSet.getString("itemid");
		} catch (Exception e)
		{
			itemId = null;
		}

		// 遍历fields
		for (Field field : fields)
		{
			// 获取字段名
			String fieldName = field.getName();
			if ("serialVersionUID".equals(fieldName))
			{ continue; }
			Object fieldVlaue = null;
			try
			{
				resultSet.findColumn(fieldName);
				fieldVlaue = resultSet.getObject(fieldName);
			} catch (SQLException e)
			{
				if ("ItemPage".equals(className))
				{
					boolean peptide = resultSet.getBoolean("peptide");
					if (peptide && ((fieldName.equals("peptidePageMap") || fieldName.equals("peptidePageList"))))
					{
						itemPeptidePageList = getItemPeptidePageList(itemId, rootId);

						if (fieldName.equals("peptidePageMap"))
						{
							fieldVlaue = ModelInfoToMap.getItemPeptidePage(itemPeptidePageList);
						} else
						{
							fieldVlaue = itemPeptidePageList;
						}
					}
					if (!peptide && (fieldName.equals("pageFieldMap") || fieldName.equals("pageFieldList")))
					{
						itemPageFieldList = getItemPageFieldList(itemId, rootId);

						if (fieldName.equals("pageFieldMap"))
						{
							fieldVlaue = ModelInfoToMap.getItemPageField(itemPageFieldList);
						} else
						{
							fieldVlaue = itemPageFieldList;
						}
					}
				} else if ("ItemPeptidePage".equals(className)
						&& ("pageFieldList".equals(fieldName) || "pageFieldMap".equals(fieldName)))
				{
					String rotid = resultSet.getString("rootid");
					String itId = resultSet.getString("itemid");
					List<ItemPageField> itemPeptidePageFields = getItemPeptidePageFieldList(itId, rotid, rootId);
					if (fieldName.equals("pageFieldMap"))
					{
						fieldVlaue = ModelInfoToMap.getItemPageField(itemPeptidePageFields);
					} else
					{
						fieldVlaue = itemPeptidePageFields;
					}
				} else if ("ItemGrid".equals(className)
						&& ("gridFieldList".equals(fieldName) || "gridFieldMap".equals(fieldName)))
				{
					itemGridFieldList = getItemGridFieldList(itemId, rootId);
					switch (fieldName)
					{
					case "gridFieldMap":
						fieldVlaue = ModelInfoToMap.getItemGridField(itemGridFieldList);
						break;

					default:
						fieldVlaue = itemGridFieldList;
						break;
					}
				} else if ("ItemRelationPage".equals(className) && ("itemRelationPageSignMap".equals(fieldName)
						|| "itemRelationPageSignList".equals(fieldName)))
				{
					itemRelationPageSignList = getItemRelationPageSignList(itemId, rootId);
					switch (fieldName)
					{
					case "itemRelationPageSignMap":
						fieldVlaue = ModelInfoToMap.getItemRelationPageSign(itemRelationPageSignList);
						break;

					default:
						fieldVlaue = itemRelationPageSignList;
						break;
					}
				} else if ("ModelRelationShip".equals(className)
						&& ("relationMenuList".equals(fieldName) || "relationMenuMap".equals(fieldName)))
				{
					itemRelationMenuList = getRelationMenuList(itemId, rootId);
					switch (fieldName)
					{
					case "relationMenuMap":
						fieldVlaue = ModelInfoToMap.getItemMenu(itemRelationMenuList);
						break;

					default:
						fieldVlaue = itemRelationMenuList;
						break;
					}
				} else if ("ItemMenu".equals(className) && "beforeActionList".equals(fieldName))
				{
					fieldVlaue = getItemMenuBeforeOrAfterActionList(itemId, rootId, "before");
				} else if ("ItemMenu".equals(className) && "afterActionList".equals(fieldName))
				{ fieldVlaue = getItemMenuBeforeOrAfterActionList(itemId, rootId, "after"); }
			}

			if (fieldVlaue != null)
			{
				// 获取方法名
				String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				// 获取字段类型
				Class<?> type = field.getType();
				Method method = calss.getDeclaredMethod(setMethodName, type);
				method.invoke(object, DataClassType.getValue(type, fieldVlaue));
			}

		}
		return object;
	}

	private static List<ItemPeptidePage> getItemPeptidePageList(String itemId, String rootId) {
		String sql = "SELECT id,no,name,rootid,itemid,sort FROM usc_model_property_field WHERE del=0 AND "
				+ "pid='0' AND itemid='" + itemId + "' AND rootid='" + rootId + "' ORDER BY sort";
		return jdbcTemplate.query(sql, new PeptidePageRowMapper());
	}

	private static List<ItemMenu> getRelationMenuList(String itemId, String rootId) {
		return jdbcTemplate
				.query("SELECT * FROM usc_model_itemmenu WHERE del=0 AND state='F' AND abtype is null AND itemid='"
						+ rootId + "'", new MenuRowMapper());
	}

	private static List<MenuLibrary> getItemMenuBeforeOrAfterActionList(String itemId, String rootId, String abtype) {
		return jdbcTemplate.query("SELECT * FROM usc_model_itemmenu WHERE del=0 AND state='F' AND abtype='" + abtype
				+ "' AND mtype=0 AND itemid='" + itemId
				+ "' AND  pid=(SELECT M.id FROM usc_model_itemmenu M WHERE M.del=0 AND M.abtype='" + abtype
				+ "' AND M.mtype=1 AND M.pid='" + rootId + "')", new MenuLibraryRowMapper());
	}

	private static List<ItemPageField> getItemPeptidePageFieldList(String itemId, String rootId, String pid) {
		String sql = getSql(itemId, rootId, "usc_model_property_field").replace(_go, " AND pid='" + pid + "'" + _go);
		return jdbcTemplate.query(sql, new PageFieldRowMapper());
	}

	private static List<ItemPageField> getItemPageFieldList(String itemId, String rootId) {
		return jdbcTemplate.query(getSql(itemId, rootId, "usc_model_property_field"), new PageFieldRowMapper());
	}

	private static List<ItemGridField> getItemGridFieldList(String itemId, String rootId) {
		String sql = null;
		if (ObjectHelperUtils.isEmpty(itemId))
		{
			sql = "SELECT * FROM USC_MODEL_GRID_FIELD WHERE del=0 AND state='F' AND rootid = '" + rootId + "'";
		} else
		{
			sql = getSql(itemId, rootId, "usc_model_property");
		}
		return jdbcTemplate.query(sql, new GridFieldRowMapper());
	}

	private static List<ItemRelationPageSign> getItemRelationPageSignList(String itemId, String rootId) {

		return jdbcTemplate.query("SELECT * FROM usc_model_relationpage_sign WHERE del=0 AND state='F' AND itemid='"
				+ itemId + "' AND rootid='" + rootId + "'", new RelationPageSignRowMapper());
	}

	private static final String _go = " GROUP BY no,id ORDER BY sort,ctime,id";

	private static String getSql(String itemId, String rootId, String tableName) {
		String sql = "SELECT P.id AS id,P.no AS no,P.name AS name,P.editable AS editable,P.sort AS sort,P.ctime AS ctime,";
		String sql2 = "F.fieldname AS fieldname,F.ftype AS ftype,F.flength AS flength,P.defaultv AS defaultv,"
				+ "F.allownull AS allownull,F.accuracy AS accuracy,F.only AS only,"
				+ "F.ispk AS ispk,F.editor AS editor,F.editparams AS editparams,F.type AS type,F.itemid AS itemid "
				+ "FROM usc_model_field F,";
		String condition = "WHERE P.del=0 AND P.state='F' AND F.del=0 AND F.state='F' AND P.itemid='" + itemId
				+ "' AND P.rootid='" + rootId + "' AND (F.no=P.no OR P.no is null OR P.no='') AND F.itemid='" + itemId
				+ "'";
		if ("usc_model_property_field".equals(tableName))
		{
			sql += "P.wline AS wline," + sql2 + "usc_model_property_field P " + condition;
		} else
		{
			sql += "P.align AS align,P.width AS width,P.screen AS screen," + sql2 + "usc_model_grid_field P "
					+ condition;
		}
		return sql + _go;
	}
}
