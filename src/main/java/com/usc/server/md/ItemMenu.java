package com.usc.server.md;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.usc.cache.redis.RedisUtil;
import com.usc.server.mq.ItemMQMenu;
import com.usc.server.util.BeanConverter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemMenu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -3816219930089042464L;
	private String id;
	private String itemNo;
	private String no;
	private String mtype;
	private String implclass;
	private String webpath;
	private String icon;
	private String pid;
	private String param;
	private String reqparam;
	private String wtype;
	private String abtype;
	private String mno;
	private String propertyparam;
	private String title;
	private int impltype;

	private List<MenuLibrary> beforeActionList;
	private List<MenuLibrary> afterActionList;

	private boolean disabled;

	private String caption;
	private String name;
	private String enName;

	@Override
	public String toString() {
		return this.no + "-" + this.name;
	}

	public ItemMQMenu getMqMenu() {
		if (impltype == 1)
		{
			RedisUtil redisUtil = RedisUtil.getInstanceOfObject();
			ItemMQMenu mqMenu = redisUtil.hget("ITEM_MQAFFAIRS", getImplclass(), ItemMQMenu.class);
			return mqMenu;
		}
		return null;
	}

	public Map<String, Object> toMap() {
		ItemMenu menuType = this;
		return BeanConverter.toMap(menuType);
	}
}
