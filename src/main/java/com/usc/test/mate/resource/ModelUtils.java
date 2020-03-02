package com.usc.test.mate.resource;

import java.util.List;
import java.util.Map;

import com.usc.app.action.mate.MateFactory;
import com.usc.autho.UserAuthority;
import com.usc.obj.api.bean.UserInformation;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.ItemMenu;
import com.usc.server.md.ItemRelationPage;
import com.usc.server.md.ItemRelationPageSign;
import com.usc.server.md.ModelClassView;
import com.usc.server.md.ModelQueryView;
import com.usc.server.md.ModelRelationShip;
import com.usc.test.mate.action.MGetClassViewModelData;
import com.usc.util.ObjectHelperUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModelUtils {
	public static void getAllPageData(Map<String, Object> resultMap, ItemInfo info, String itemGridNo,
			String itemPropertyNo, String itemRelationPageNo, int faceType) {

		resultMap.put("itemID", info.getId());
		resultMap.put("itemNo", info.getItemNo());
		resultMap.put("itemName", info.getName());
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
			getRelationModleData(info, itemRelationPage, faceType);
			resultMap.put("itemRelationPage", appItemRelationPage.getItemRelationPageSignList());
		}
	}

	private static void getRelationModleData(ItemInfo info, ItemRelationPage itemRelationPage, int faceType) {
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
				if (propertyNo != null)
				{
					itemRelationPageSign.setItemRelationPropertyPage(info.getItemPage(propertyNo));
				} else
				{
					itemRelationPageSign.setItemRelationPropertyPage(info.getDefaultItemPage());
				}
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
					modelRelationShipData.setItemMenus(infoBInfo.getItemMenuList());
					modelRelationShipData.setItemGrid(infoBInfo.getItemGrid(itemRelationPageSign.getItemGrid()));
					modelRelationShipData.setItemPropertyPage(infoBInfo.getDefaultItemPage());
					if (faceType == 3)
					{
						ItemRelationPage itembRelationPage = infoBInfo.getDefaultItemRelationPage();
						if (itembRelationPage != null)
						{
							getRelationModleData(infoBInfo, itembRelationPage, -1);
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
		resultMap.put("itemName", info.getName());
		resultMap.put("itemType", info.getType());
		resultMap.put("isLife", info.getIsLife());
		List<ItemMenu> itemMenus = info.getItemMenuList();
		UserAuthority.authorityMenus(userInformation, itemMenus);
		resultMap.put("itemMenus", itemMenus);
		resultMap.put("itemGrid", info.getItemGrid(itemGridNo));
		resultMap.put("itemPropertyPage", info.getItemPage(itemPropertyNo));
		resultMap.put("itemColor", info.getColors());
		if (faceType == -1 || faceType == 0 || faceType == 1)
		{ return; }
		ItemRelationPage itemRelationPage = info.getItemRelationPage(itemRelationPageNo);
		if (itemRelationPage == null)
		{
			resultMap.put("itemRelationPage", null);
		} else
		{
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
			String rType = itemRelationPageSign.getRType();
			switch (rType)
			{
			case "relationproperty":
				String propertyNo = itemRelationPageSign.getRelevanceNo();
				if (propertyNo != null)
				{
					itemRelationPageSign.setItemRelationPropertyPage(info.getItemPage(propertyNo));
				} else
				{
					itemRelationPageSign.setItemRelationPropertyPage(info.getDefaultItemPage());
				}
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
					List<ItemMenu> relationMenus = modelRelationShipData.getRelationMenuList();
					List<ItemMenu> itemBMenus = infoBInfo.getItemMenuList();
					if (faceType != 0)
					{
						UserAuthority.authorityMenus(userInformation, relationMenus);
						UserAuthority.authorityMenus(userInformation, itemBMenus);
					}

					modelRelationShipData.setIsLife(infoBInfo.getIsLife());
					modelRelationShipData.setItemMenus(itemBMenus);
					modelRelationShipData.setItemGrid(infoBInfo.getItemGrid(itemRelationPageSign.getItemGrid()));
					modelRelationShipData.setItemPropertyPage(infoBInfo.getDefaultItemPage());
					if (String.valueOf(faceType).startsWith("3"))
					{
						ItemRelationPage itembRelationPage = infoBInfo.getDefaultItemRelationPage();
						if (itembRelationPage != null)
						{
							getRelationModleData(infoBInfo, itembRelationPage, -1);
							modelRelationShipData.setItemRelationPage(itembRelationPage.getItemRelationPageSignList());
						}
					}

				}
				itemRelationPageSign.setModelRelationShip(modelRelationShipData);
				break;

			case "relationqueryview":
				String queryViewNo = itemRelationPageSign.getRelevanceNo();
				ModelQueryView queryView = MateFactory.getQueryView(queryViewNo);
				if (queryView != null)
				{
					ItemInfo queryViewInfo = MateFactory.getItemInfo(queryView.getItemNo());
					if (queryViewInfo == null)
					{
						log.error(">>>>>> QueryView_ItemInfo->" + queryView.getItemNo() + " Not found ");
						break;
					}
					queryView.setIsLife(queryViewInfo.getIsLife());
					queryView.setItemMenus(queryViewInfo.getItemMenuList());
					queryView.setItemGrid(queryViewInfo.getItemGrid(itemRelationPageSign.getItemGrid()));
					queryView.setItemPropertyPage(queryViewInfo.getDefaultItemPage());
					itemRelationPageSign.setModelQueryView(queryView);
				}
				break;
			case "relationclassview":
				String classViewNo = itemRelationPageSign.getRelevanceNo();
				ModelClassView classView = MGetClassViewModelData.getClassViewModelData(classViewNo, null,
						userInformation.getUserName());
				if (classView != null)
				{
					ItemInfo classViewInfo = MateFactory.getItemInfo(classView.getItemNo());
					if (classViewInfo == null)
					{
						log.error(">>>>>> ModelClassView_ItemInfo->" + classView.getItemNo() + " Not found ");
						break;
					}
					classView.setIsLife(classViewInfo.getIsLife());
					classView.setItemMenus(classViewInfo.getItemMenuList());
					classView.setItemGrid(classViewInfo.getItemGrid(itemRelationPageSign.getItemGrid()));
					classView.setItemPropertyPage(classViewInfo.getDefaultItemPage());
					itemRelationPageSign.setModelClassView(classView);
				}
				break;
			case "dynamicRelationPage":

			default:
				break;
			}
		}

	}
}
