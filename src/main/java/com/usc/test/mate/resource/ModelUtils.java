package com.usc.test.mate.resource;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.usc.app.action.mate.MateFactory;
import com.usc.app.sys.online.OnlineUsers;
import com.usc.autho.UserAuthority;
import com.usc.obj.api.bean.UserInformation;
import com.usc.server.md.ItemField;
import com.usc.server.md.ItemGrid;
import com.usc.server.md.ItemGridField;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.ItemMenu;
import com.usc.server.md.ItemPage;
import com.usc.server.md.ItemPageField;
import com.usc.server.md.ItemRelationPage;
import com.usc.server.md.ItemRelationPageSign;
import com.usc.server.md.ModelClassView;
import com.usc.server.md.ModelQueryView;
import com.usc.server.md.ModelRelationShip;
import com.usc.test.mate.action.MGetClassViewModelData;
import com.usc.util.JBeanUtils;
import com.usc.util.ObjectHelperUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModelUtils {
	public static void getAllPageData(Map<String, Object> resultMap, ItemInfo info, String itemGridNo,
			String itemPropertyNo, String itemRelationPageNo, int faceType) {
		setAcceptLanguage(info, null);
		resultMap.put("itemID", info.getId());
		resultMap.put("itemNo", info.getItemNo());
		resultMap.put("itemType", info.getType());
		resultMap.put("itemMenus", info.getItemMenuList());
		resultMap.put("itemGrid", info.getItemGrid(itemGridNo));
		resultMap.put("itemPropertyPage", info.getItemPage(itemPropertyNo));
		if (faceType == -1 || faceType == 0)
		{ return; }
		ItemRelationPage itemRelationPage = info.getItemRelationPage(itemRelationPageNo);
		if (itemRelationPage == null)
		{
			resultMap.put("itemRelationPage", null);
		} else
		{
			ItemRelationPage appItemRelationPage = itemRelationPage.clone();
			getRelationModleData(info, itemRelationPage, faceType, null);
			resultMap.put("itemRelationPage", appItemRelationPage.getItemRelationPageSignList());
		}
	}

	private static void getRelationModleData(ItemInfo info, ItemRelationPage itemRelationPage, int faceType,
			UserInformation userInformation) {
		if (itemRelationPage == null)
		{ return; }
		List<ItemRelationPageSign> itemRelationPageSigns = itemRelationPage.getItemRelationPageSignList();
		if (ObjectHelperUtils.isEmpty(itemRelationPageSigns))
		{ return; }
		for (ItemRelationPageSign itemRelationPageSign : itemRelationPageSigns)
		{
			String rType = itemRelationPageSign.getRType();
			switch (rType)
			{
			case "relationproperty":
				String propertyNo = itemRelationPageSign.getRelevanceNo();
				itemRelationPageSign.setItemRelationPropertyPage(getItemPage(info, propertyNo, userInformation));
				break;

			case "relationpage":
				String relationShipNo = itemRelationPageSign.getRelevanceNo();
				ModelRelationShip modelRelationShipData = null;
				if (relationShipNo != null)
				{
					modelRelationShipData = MateFactory.getRelationShip(relationShipNo.toUpperCase());
					if (modelRelationShipData == null)
					{
						log.error(">>>>>> ModelRelationShip Not found :" + relationShipNo);
						break;
					}
					ItemInfo infoBInfo = MateFactory.getItemInfo(modelRelationShipData.getItemB().toUpperCase());
					if (infoBInfo == null)
					{
						log.error(">>>>>> ModelRelationShip->" + relationShipNo + ",itemBInfo Not found ");
						break;
					}
					modelRelationShipData.setItemMenus(getItemMenus(infoBInfo, userInformation));
					modelRelationShipData.setItemGrid(infoBInfo.getItemGrid(itemRelationPageSign.getItemGrid()));
					modelRelationShipData.setItemPropertyPage(infoBInfo.getDefaultItemPage());
					if (faceType == 3)
					{
						ItemRelationPage itembRelationPage = infoBInfo.getDefaultItemRelationPage();
						if (itembRelationPage != null)
						{
							getRelationModleData(infoBInfo, itembRelationPage, -1, userInformation);
							modelRelationShipData.setItemRelationPage(itembRelationPage.getItemRelationPageSignList());
						}
					}

				}
				itemRelationPageSign.setModelRelationShip(modelRelationShipData);
				break;

			case "relationqueryview":
				String queryViewNo = itemRelationPageSign.getRelevanceNo();
				ModelQueryView queryView = MateFactory.getQueryView(queryViewNo.toUpperCase());
				if (queryView != null)
				{
					setAcceptLanguage(queryView, userInformation);
					ItemInfo queryViewInfo = MateFactory.getItemInfo(queryView.getItemNo().toUpperCase());
					if (queryViewInfo == null)
					{
						log.error(">>>>>> QueryView_ItemInfo->" + queryView.getItemNo() + " Not found ");
						break;
					}
					queryView.setItemMenus(queryViewInfo.getItemMenuList());
					queryView.setItemGrid(queryViewInfo.getItemGrid(itemRelationPageSign.getItemGrid()));
					queryView.setItemPropertyPage(queryViewInfo.getDefaultItemPage());
					itemRelationPageSign.setModelQueryView(queryView);
				}
				break;

			default:
				break;
			}
		}

	}

	public static void getClientAllPageData(UserInformation userInformation, Map<String, Object> resultMap,
			ItemInfo info, String itemGridNo, String itemPropertyNo, String itemRelationPageNo, int faceType) {

		resultMap.put("itemID", info.getId());
		resultMap.put("itemNo", info.getItemNo());
		setAcceptLanguage(info, userInformation);

		resultMap.put("itemType", info.getType());
		resultMap.put("isLife", info.getIsLife());

		List<ItemMenu> itemMenus = getItemMenus(info, userInformation);
		UserAuthority.authorityMenus(userInformation, itemMenus);
		resultMap.put("itemMenus", itemMenus);

		resultMap.put("itemGrid", getItemGrid(info, itemGridNo, userInformation));

		List<ItemField> supQueryFields = info.getSupQueryFieldList();
		supQueryFields.forEach(supQueryField -> setAcceptLanguage(supQueryField, userInformation));
		resultMap.put("itemSuppertQueryFields", supQueryFields);

		resultMap.put("itemColor", info.getColors());
		if (faceType == -1 || faceType == 0 || faceType == 1)
		{ return; }
		ItemRelationPage itemRelationPage = info.getItemRelationPage(itemRelationPageNo);
		if (itemRelationPage == null)
		{
			resultMap.put("itemRelationPage", null);
		} else
		{
			setAcceptLanguage(itemRelationPage, userInformation);
			ItemRelationPage appItemRelationPage = itemRelationPage.clone();
			getClientRelationModleData(userInformation, info, itemRelationPage, faceType);
			resultMap.put("itemRelationPage", appItemRelationPage.getItemRelationPageSignList());
		}
	}

	private static void getClientRelationModleData(UserInformation userInformation, ItemInfo info,
			ItemRelationPage itemRelationPage, int faceType) {
		if (itemRelationPage == null)
		{ return; }
		List<ItemRelationPageSign> itemRelationPageSigns = itemRelationPage.getItemRelationPageSignList();
		if (ObjectHelperUtils.isEmpty(itemRelationPageSigns))
		{ return; }
		for (ItemRelationPageSign itemRelationPageSign : itemRelationPageSigns)
		{
			setAcceptLanguage(itemRelationPageSign, userInformation);
			String rType = itemRelationPageSign.getRType();
			switch (rType)
			{
			case "relationproperty":
				String propertyNo = itemRelationPageSign.getRelevanceNo();
				itemRelationPageSign.setRelationPageSign(getItemPage(info, propertyNo, userInformation));
				break;

			case "relationpage":
				String relationShipNo = itemRelationPageSign.getRelevanceNo();
				ModelRelationShip modelRelationShipData = null;
				if (relationShipNo != null)
				{
					modelRelationShipData = MateFactory.getRelationShip(relationShipNo);
					if (modelRelationShipData == null)
					{
						log.error(">>>>>> ModelRelationShip Not found :" + relationShipNo);
						break;
					}
					ItemInfo infoBInfo = MateFactory.getItemInfo(modelRelationShipData.getItemB());
					if (infoBInfo == null)
					{
						log.error(">>>>>> ModelRelationShip->" + relationShipNo + ",itemBInfo Not found ");
						break;
					}
					setAcceptLanguage(infoBInfo, userInformation);
					List<ItemMenu> relationMenus = modelRelationShipData.getRelationMenuList();
					relationMenus.forEach(relationMenu -> setAcceptLanguage(relationMenu, userInformation));

					List<ItemMenu> itemBMenus = getItemMenus(infoBInfo, userInformation);
					if (faceType != 0)
					{
						UserAuthority.authorityMenus(userInformation, relationMenus);
						UserAuthority.authorityMenus(userInformation, itemBMenus);
					}

					modelRelationShipData.setIsLife(infoBInfo.getIsLife());
					modelRelationShipData.setItemMenus(itemBMenus);
					modelRelationShipData
							.setItemGrid(getItemGrid(infoBInfo, itemRelationPageSign.getItemGrid(), userInformation));
					modelRelationShipData.setItemPropertyPage(getItemPage(infoBInfo, null, userInformation));
					if (String.valueOf(faceType).startsWith("3"))
					{
						ItemRelationPage itembRelationPage = infoBInfo.getDefaultItemRelationPage();
						if (itembRelationPage != null)
						{
							setAcceptLanguage(itembRelationPage, userInformation);
							getRelationModleData(infoBInfo, itembRelationPage, -1, userInformation);
							modelRelationShipData.setItemRelationPage(itembRelationPage.getItemRelationPageSignList());
						}
					}

				}
				itemRelationPageSign.setRelationPageSign(modelRelationShipData);
				break;

			case "relationqueryview":
				String queryViewNo = itemRelationPageSign.getRelevanceNo();
				ModelQueryView queryView = MateFactory.getQueryView(queryViewNo);
				if (queryView != null)
				{
					setAcceptLanguage(queryView, userInformation);
					ItemInfo queryViewInfo = MateFactory.getItemInfo(queryView.getItemNo());
					if (queryViewInfo == null)
					{
						log.error(">>>>>> QueryView_ItemInfo->" + queryView.getItemNo() + " Not found ");
						break;
					}
					queryView.setIsLife(queryViewInfo.getIsLife());
					queryView.setItemMenus(getItemMenus(queryViewInfo, userInformation));
					queryView.setItemGrid(
							getItemGrid(queryViewInfo, itemRelationPageSign.getItemGrid(), userInformation));
					queryView.setItemPropertyPage(getItemPage(queryViewInfo, null, userInformation));
					itemRelationPageSign.setRelationPageSign(queryView);
				}
				break;
			case "relationclassview":
				String classViewNo = itemRelationPageSign.getRelevanceNo();
				ModelClassView classView = MGetClassViewModelData.getClassViewModelData(classViewNo, null,
						userInformation.getUserName());
				if (classView != null)
				{
					setAcceptLanguage(classView, userInformation);
					ItemInfo classViewInfo = MateFactory.getItemInfo(classView.getItemNo());
					if (classViewInfo == null)
					{
						log.error(">>>>>> ModelClassView_ItemInfo->" + classView.getItemNo() + " Not found ");
						break;
					}
					classView.setIsLife(classViewInfo.getIsLife());
					classView.setItemMenus(getItemMenus(classViewInfo, userInformation));
					classView.setItemGrid(
							getItemGrid(classViewInfo, itemRelationPageSign.getItemGrid(), userInformation));
					classView.setItemPropertyPage(getItemPage(classViewInfo, null, userInformation));
					itemRelationPageSign.setRelationPageSign(classView);
				}
				break;
			case "dynamicRelationPage":

			default:
				break;
			}
		}

	}

	public static ItemInfo getItemInfo(String itemNo, UserInformation userInformation) {
		ItemInfo info = MateFactory.getItemInfo(itemNo);
		setAcceptLanguage(info, userInformation);

		return info;
	}

	public static List<ItemMenu> getItemMenus(ItemInfo info, UserInformation userInformation) {
		List<ItemMenu> itemMenus = info.getItemMenuList();
		if (itemMenus != null)
		{ itemMenus.forEach(itemMenu -> setAcceptLanguage(itemMenu, userInformation)); }

		return itemMenus;
	}

	public static ItemPage getItemPage(ItemInfo info, String itemPageNo, UserInformation userInformation) {
		ItemPage itemPage = info.getItemPage(itemPageNo);
		if (itemPage != null)
		{
			setAcceptLanguage(itemPage, userInformation);
			List<ItemPageField> fields = itemPage.getPageFieldList();
			if (fields != null)
			{
				fields.forEach(itemGridField -> setAcceptLanguage(itemGridField, userInformation));
				itemPage.setPageFieldList(fields);
			}

		}

		return itemPage;
	}

	public static ItemGrid getItemGrid(ItemInfo info, String itemGridNo, UserInformation userInformation) {
		ItemGrid itemGrid = info.getItemGrid(itemGridNo);
		if (itemGrid != null)
		{
			setAcceptLanguage(itemGrid, userInformation);
			List<ItemGridField> fields = itemGrid.getGridFieldList();
			if (fields != null)
			{
				fields.forEach(itemGridField -> setAcceptLanguage(itemGridField, userInformation));
				itemGrid.setGridFieldList(fields);
			}
		}

		return itemGrid;
	}

	public static void setAcceptLanguage(Object modelInfo, UserInformation userInformation) {
		if (modelInfo != null)
		{
			Locale userLanguag = userInformation == null ? Locale.SIMPLIFIED_CHINESE
					: OnlineUsers.getOnUser(userInformation.getUserName()).getLocale();
			String caption = "";
			if (userLanguag.equals(Locale.SIMPLIFIED_CHINESE))
			{
				caption = (String) JBeanUtils.reflection(modelInfo, modelInfo.getClass(), "getName");
			} else if (userLanguag.equals(Locale.US))
			{ caption = (String) JBeanUtils.reflection(modelInfo, modelInfo.getClass(), "getEnName"); }
			JBeanUtils.reflection(modelInfo, modelInfo.getClass(), "setCaption", new Class<?>[] { String.class },
					caption);
		}
	}
}
