package com.usc.autho;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.usc.app.util.UserInfoUtils;
import com.usc.obj.api.bean.UserInformation;
import com.usc.obj.api.type.user.UserInfoObject;
import com.usc.server.jdbc.SDBUtils;
import com.usc.server.md.ItemMenu;
import com.usc.util.ObjectHelperUtils;

/**
 *
 * <p>
 * Description: 菜单授权
 * </P>
 *
 * @date 2019年9月17日
 * @author PuTianXiong
 */
public class UserAuthority {

	/**
	 * <p>
	 * Description: 根据用户或角色ID获取真实的菜单ID映射
	 * </P>
	 *
	 * @param uID @return 返回已授权的菜单映射 Map @throws
	 */

	private static Map<String, ConcurrentHashMap<String, String>> userAuthMenus = new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>();

	public static Map<String, String> getUserRightMenus(String uID) {
		ConcurrentHashMap<String, String> userMenuIDS = userAuthMenus.get(uID);
		if (userMenuIDS != null)
		{ return userMenuIDS; }
		List<Map<String, Object>> allAuthority = SDBUtils.getUserAuthAllData(uID);
		if (!ObjectHelperUtils.isEmpty(allAuthority))
		{
			ConcurrentHashMap<String, String> menuIDS = new ConcurrentHashMap<String, String>();
			allAuthority.forEach(map -> {
				String ctn = (String) map.get("CTN");
				if (ctn.endsWith("menu"))
				{
					String cid = (String) map.get("CID");
					cid = cid.substring(cid.lastIndexOf("_") + 1, cid.length());
					menuIDS.put(cid, cid);
				}

			});
			userAuthMenus.put(uID, menuIDS);
			return menuIDS;

		}
		return null;
	}

	/**
	 * @param userName
	 */
	public static void removeUserRightMenus(String userName) {
		UserInfoObject user = UserInfoUtils.getUserInfoObject(userName);
		if (user != null)
		{ userAuthMenus.remove(user.getUserID()); }
	}

	/**
	 * <p>
	 * Description: 根据用户信息控制菜单是否可操作性 disable
	 * </P>
	 *
	 * @param user :用户信息 @param itemMenus :菜单集合 @return @throws
	 */
	public static void authorityMenus(UserInformation user, List<ItemMenu> itemMenus) {
		if (ObjectHelperUtils.isEmpty(itemMenus))
		{ return; }
		if (!UserAuthority.getSuperUsers().contains(user.getUserName()))
		{
			String userID = user.getUserID();
			Map<String, String> rigths = getUserRightMenus(userID);
			itemMenus.forEach(menu -> {
				String menuID = menu.getId();
				if (ObjectHelperUtils.isEmpty(rigths))
				{
					menu.setDisabled(true);

				} else
				{
					if (rigths.containsKey(menuID))
					{
						menu.setDisabled(false);
					} else
					{
						menu.setDisabled(true);
					}
				}

			});
		}
	}

	public static List<String> getSuperUsers() {
		List<String> users = new ArrayList<String>();
		users.add("admin");
		users.add("hjh");
		users.add("lwp");
		return users;
	}
}
