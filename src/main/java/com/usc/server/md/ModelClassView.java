package com.usc.server.md;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelClassView implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5612311598280685960L;

	private String id;
	private String no;

	private String itemNo;
	private String wcondition;

	private String caption;
	private String name;
	private String enName;

	private List<ModelClassViewTreeNode> classViewNodeList;
	private Map<String, ModelClassViewTreeNode> classViewNodeMap = new ConcurrentHashMap<String, ModelClassViewTreeNode>();
	private ModelClassViewTreeNode rootNode;
	private List<ItemMenu> itemMenus;
	private ItemInfo itemInfo;
	private Integer isLife;
	private ItemGrid itemGrid;
	private ItemPage itemPropertyPage;

}
