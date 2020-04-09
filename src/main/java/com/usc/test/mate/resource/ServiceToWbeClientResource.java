package com.usc.test.mate.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usc.app.action.mate.MateFactory;
import com.usc.app.sys.online.OnlineUsers;
import com.usc.app.util.UserInfoUtils;
import com.usc.autho.UserAuthority;
import com.usc.obj.api.BeanFactoryConverter;
import com.usc.obj.api.bean.UserInformation;
import com.usc.server.DBConnecter;
import com.usc.server.jdbc.DBUtil;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.ItemMenu;
import com.usc.server.md.mapper.MenuRowMapper;
import com.usc.test.mate.action.MGetClassViewModelData;
import com.usc.test.mate.jsonbean.USCObjectJSONBean;
import com.usc.test.mate.jsonbean.USCObjectQueryJsonBean;

@RestController
@RequestMapping(value = "/sysModelToWbeClient", produces = "application/json;charset=UTF-8")
public class ServiceToWbeClientResource {
	public static ConcurrentHashMap<String, Map<String, Map<String, Object>>> webPageModelData = new ConcurrentHashMap<String, Map<String, Map<String, Object>>>();

	@PostMapping("/getModelData")
	public Map<String, Object> getModelData(@RequestBody USCObjectJSONBean queryParam, HttpServletRequest request)
			throws Exception {
		USCObjectJSONBean jsonBean = queryParam;
//		try
//		{
//			jsonBean = BeanFactoryConverter.getJsonBean(USCObjectJSONBean.class, queryParam);
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
		String userName = jsonBean.getUserName() != null ? jsonBean.getUserName() : request.getHeader("UserName");
		String pageID = jsonBean.getPageId();
		if (pageID != null)
		{
			Map<String, Map<String, Object>> wpm = webPageModelData
					.get(OnlineUsers.getUserNameAcceptLanguage(userName));
			if (wpm != null)
			{
				Map<String, Object> um = wpm.get(pageID);
				if (um != null)
				{ return um; }
			}
		}

		ItemInfo info = jsonBean.getItemInfo();
		Map<String, Object> resultMap = new HashMap<>();
		if (info == null)
		{
			resultMap.put("flag", true);
			resultMap.put("info", "对象不存在");
			return resultMap;
		}

		UserInformation userInformation = UserInfoUtils.getUserInformation(userName);
		String itemGridNo = jsonBean.itemGridNo;
		String itemPropertyNo = jsonBean.itemPropertyNo;
		int faceType = jsonBean.faceType;
		String itemRelationPageNo = jsonBean.itemRelationPageNo;

		List<ItemMenu> pageMenus = DBConnecter.getModelJdbcTemplate().query(
				"SELECT * FROM usc_model_itemmenu WHERE del=0 AND state='F' AND itemid='" + jsonBean.getPageId() + "'",
				new MenuRowMapper());
		if (pageMenus != null)
		{
			for (ItemMenu itemMenu : pageMenus)
			{ ModelUtils.setAcceptLanguage(itemMenu, userInformation); }
		}
		UserAuthority.authorityMenus(userInformation, pageMenus);
		resultMap.put("flag", true);
		resultMap.put("faceType", faceType);
		resultMap.put("pageMenus", pageMenus);
		ModelUtils.getClientAllPageData(userInformation, resultMap, info, itemGridNo, itemPropertyNo,
				itemRelationPageNo, faceType);

		return resultMap;
	}

	@PostMapping("/getClassViewModelData")
	public Map<String, Object> getClassViewModelData(@RequestBody String queryParam, HttpServletRequest request)
			throws Exception {
		USCObjectJSONBean jsonBean = null;
		Map<String, Object> resultMap = new HashMap<>();
		try
		{
			jsonBean = BeanFactoryConverter.getJsonBean(USCObjectJSONBean.class, queryParam);
		} catch (Exception e)
		{

			e.printStackTrace();
			resultMap.put("flag", false);
			resultMap.put("info", "参数不正确，获取建模数据失败");
			return resultMap;
		}
		String userName = jsonBean.getUserName() != null ? jsonBean.getUserName() : request.getHeader("UserName");
		String pageID = jsonBean.getPageId();
		if (pageID != null)
		{
			Map<String, Map<String, Object>> wpm = webPageModelData
					.get(OnlineUsers.getUserNameAcceptLanguage(userName));
			if (wpm != null)
			{
				Map<String, Object> um = wpm.get(pageID);
				if (um != null)
				{ return um; }
			}
		}

		ItemInfo info = jsonBean.getItemInfo();

		if (info == null)
		{
			resultMap.put("flag", true);
			resultMap.put("info", "对象不存在");
			return resultMap;
		}

		UserInformation userInformation = UserInfoUtils.getUserInformation(userName);
		String itemGridNo = jsonBean.itemGridNo;
		String itemPropertyNo = jsonBean.itemPropertyNo;
		int faceType = jsonBean.faceType;
		String itemRelationPageNo = jsonBean.itemRelationPageNo;
		List<ItemMenu> pageMenus = DBConnecter.getModelJdbcTemplate().query(
				"SELECT * FROM usc_model_itemmenu WHERE del=0 AND state='F' AND itemid='" + pageID + "'",
				new MenuRowMapper());
		if (pageMenus != null)
		{
			for (ItemMenu itemMenu : pageMenus)
			{ ModelUtils.setAcceptLanguage(itemMenu, userInformation); }
		}
		UserAuthority.authorityMenus(userInformation, pageMenus);

		resultMap.put("flag", true);
		resultMap.put("faceType", faceType);
		resultMap.put("pageMenus", pageMenus);
		ModelUtils.getClientAllPageData(userInformation, resultMap, info, itemGridNo, itemPropertyNo,
				itemRelationPageNo, faceType);
		if (jsonBean.getViewNo() != null)
		{
			if (faceType == 5)
			{
				resultMap.put("classViewNodeList", MGetClassViewModelData
						.getClassViewModelData(jsonBean.getViewNo(), null, userName).getClassViewNodeList());
				return resultMap;
			}
		}

		return null;
	}

	@PostMapping("/getClassNodeModelData")
	public Map<String, Object> getClassNodeModelData(@RequestBody String queryParam, HttpServletRequest request) {
		USCObjectQueryJsonBean jsonBean = null;
		try
		{
			jsonBean = BeanFactoryConverter.getJsonBean(USCObjectQueryJsonBean.class, queryParam);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		ItemInfo info = jsonBean.getItemInfo();
		if (info == null)
			return null;
		String itemGridNo = jsonBean.itemGridNo;
		String itemPropertyNo = jsonBean.itemPropertyNo;
		int faceType = jsonBean.faceType;
		String itemRelationPageNo = jsonBean.itemRelationPageNo;

		String userName = jsonBean.getUserName() != null ? jsonBean.getUserName() : request.getHeader("UserName");
		UserInformation userInformation = UserInfoUtils.getUserInformation(userName);

		Map<String, Object> result = new HashMap<>();

		Map<String, Object> itemModel = new HashMap<>();
		result.put("flag", true);
		result.put("faceType", faceType);
		ModelUtils.getClientAllPageData(userInformation, itemModel, info, itemGridNo, itemPropertyNo,
				itemRelationPageNo, 2);
		result.put("itemModel", itemModel);

		Map<String, Object> classModel = new HashMap<>();
		ItemInfo classInfo = MateFactory.getItemInfo(jsonBean.getClassNodeItemNo());
		String classNodeItemPropertyNo = jsonBean.getClassNodeItemPropertyNo();
		ModelUtils.getClientAllPageData(userInformation, classModel, classInfo, null, classNodeItemPropertyNo, null,
				faceType);

		List<ItemMenu> pageMenus = DBConnecter.getModelJdbcTemplate().query(
				"SELECT * FROM usc_model_itemmenu WHERE del=0 AND state='F' AND itemid='" + jsonBean.getPageId() + "'",
				new MenuRowMapper());
		Object itemMenus = classModel.get("itemMenus");
		if (itemMenus != null)
		{
			List<ItemMenu> itemMenus2 = (List<ItemMenu>) itemMenus;
			pageMenus.addAll(itemMenus2);
			for (ItemMenu itemMenu : pageMenus)
			{ ModelUtils.setAcceptLanguage(itemMenu, userInformation); }
		}

		UserAuthority.authorityMenus(userInformation, pageMenus);

		classModel.put("itemMenus", pageMenus);

		result.put("classNodeModel", classModel);

		List classNodeData = DBUtil.getSQLResultByCondition(classInfo, "itemno='" + info.getItemNo() + "'");
		result.put("classNodeDataList", classNodeData);
		return result;
	}

	@PostMapping("/getModel/gridData")
	public Object GetModelGridData(@RequestBody String queryParam, HttpServletRequest request) {
		try
		{

			USCObjectJSONBean bean = BeanFactoryConverter.getJsonBean(USCObjectJSONBean.class, queryParam);
			ItemInfo info = bean.getItemInfo();
			if (info == null)
			{ return null; }
			UserInformation userInformation = UserInfoUtils.getUserInformation(bean.getUserName());
			return ModelUtils.getItemGrid(info, bean.getItemGridNo(), userInformation);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}

	@PostMapping("/getModel/propertyData")
	public Object GetModePropertyData(@RequestBody String queryParam, HttpServletRequest request) {
		try
		{
			USCObjectJSONBean bean = BeanFactoryConverter.getJsonBean(USCObjectJSONBean.class, queryParam);
			ItemInfo info = bean.getItemInfo();
			if (info == null)
			{ return null; }
			UserInformation userInformation = UserInfoUtils.getUserInformation(bean.getUserName());
			return ModelUtils.getItemPage(info, bean.getItemPropertyNo(), userInformation);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}

	@PostMapping("/getModel/menuData")
	public Object GetModeMenuData(@RequestBody String queryParam, HttpServletRequest request) {
		try
		{
			USCObjectJSONBean bean = BeanFactoryConverter.getJsonBean(USCObjectJSONBean.class, queryParam);
			ItemInfo info = bean.getItemInfo();

			if (info == null)
			{ return null; }
			UserInformation userInformation = UserInfoUtils.getUserInformation(bean.getUserName());
			return ModelUtils.getItemMenus(info, userInformation);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}

	@PostMapping("/getModel/relationPageData")
	public Object GetModeRelationPageData(@RequestBody String queryParam, HttpServletRequest request) {
		try
		{
			USCObjectJSONBean bean = BeanFactoryConverter.getJsonBean(USCObjectJSONBean.class, queryParam);
			ItemInfo info = bean.getItemInfo();

			return info.getItemRelationPage(bean.getItemRelationPageNo());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}
}
