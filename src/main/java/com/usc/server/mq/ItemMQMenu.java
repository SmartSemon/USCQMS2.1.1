package com.usc.server.mq;

import java.io.Serializable;
import java.util.Map;

import com.usc.server.util.BeanConverter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemMQMenu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -3816219930089042464L;
	private String id;
	private String no;
	private String name;
	private int mtype;
	private String mcontent;
	private String icon;
	private String param;
	private String reqParam;
	private String title;
	private String channelname;

	private boolean disabled;

	@Override
	public String toString() {
		return this.no + "-" + this.name + "-" + this.mcontent;
	}

	public Map<String, Object> toMap() {
		ItemMQMenu menuType = this;
		return BeanConverter.toMap(menuType);
	}
}
