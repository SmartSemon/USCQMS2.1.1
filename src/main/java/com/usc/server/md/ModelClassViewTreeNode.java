package com.usc.server.md;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelClassViewTreeNode implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8782490787688143442L;

	private String id;
	private String no;

	private String icon;
	private String nodecondition;
	private String datacondition;
	private String pid;

	private String treenodeid;
	private String treenodepid;
	private String treenodedata;

	private Integer summary;
	private Integer loadDataSet;
	private String itemid;

	private String caption;
	private String name;
	private String enName;

	private Integer nodeDataTotal;

	@Override
	public ModelClassViewTreeNode clone() {
		ModelClassViewTreeNode classViewTreeNode = new ModelClassViewTreeNode();
		classViewTreeNode.setId(id);
		classViewTreeNode.setNo(no);
		classViewTreeNode.setName(name);
		classViewTreeNode.setIcon(icon);
		classViewTreeNode.setNodecondition(nodecondition);
		classViewTreeNode.setDatacondition(datacondition);
		classViewTreeNode.setPid(pid);
		classViewTreeNode.setItemid(itemid);
		classViewTreeNode.setSummary(summary);
		classViewTreeNode.setLoadDataSet(loadDataSet);
		return classViewTreeNode;
	}

}
