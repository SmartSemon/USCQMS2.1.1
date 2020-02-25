package com.usc.test.mate.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.usc.app.action.mate.MateFactory;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjExpHelper;
import com.usc.server.DBConnecter;
import com.usc.server.jdbc.DBUtil;
import com.usc.server.md.ModelClassView;
import com.usc.server.md.ModelClassViewTreeNode;
import com.usc.util.ObjectHelperUtils;

public class MGetClassViewModelData
{
	private static final String split = "#";
	private static final String PARENT = "PARENT.";
	private static final String sign = "%";
	private static final String CLASSVIEWTREENODEVALUE = "CLASSVIEWTREENODEVALUE";

	private static String ORDER = "order";
	private static String BY = "by";

	public static ModelClassView getClassViewModelData(String viewNo, USCObject object, String userName)
	{
		ModelClassView view = null;
		ModelClassView classView = MateFactory.getClassView(viewNo);
		if (classView != null)
		{
			view = new ModelClassView();
			view.setId(classView.getId());
			view.setNo(classView.getNo());
			view.setName(classView.getName());
			view.setItemNo(classView.getItemNo());
			view.setWcondition(classView.getWcondition());
			view.setRootNode(classView.getRootNode());
			view.setClassViewNodeList(classView.getClassViewNodeList());
			view.setClassViewNodeMap(classView.getClassViewNodeMap());
			List<ModelClassViewTreeNode> treeNodes = new ArrayList<ModelClassViewTreeNode>();
			ModelClassViewTreeNode rootNode = classView.getRootNode().clone();
			rootNode.setTreenodedata(rootNode.getName());
			rootNode.setTreenodeid(rootNode.getName());
			rootNode.setTreenodepid("0");

			String tableName = MateFactory.getItemInfo(classView.getItemNo()).getTableName();
			Integer dataTotal = setDataTotal(rootNode, tableName, object, userName);
			rootNode.setNodeDataTotal(dataTotal);
			treeNodes.add(rootNode);
			List<ModelClassViewTreeNode> treeNodeList = view.getClassViewNodeList();
			Map<String, ModelClassViewTreeNode> treeNodeMap = view.getClassViewNodeMap();
			getChildNode(rootNode, treeNodeList, treeNodeMap, treeNodes, object, tableName, userName);
			view.setClassViewNodeList(treeNodes);

		}
		return view;
	}

	private static Integer setDataTotal(ModelClassViewTreeNode rootNode, String tableName, USCObject object,
			String userName)
	{
		Integer sum = rootNode.getSummary();
		if (sum != null && sum != 0)
		{
			String condition = rootNode.getDatacondition();
			if (ObjectHelperUtils.isNotEmpty(condition))
			{
				condition = condition.replace("%SYSTEMUSER%", userName);
				String sql = "SELECT COUNT(ID) AS summary FROM " + tableName + " WHERE " + condition;
				Map<String, Object> count = DBConnecter.getJdbcTemplate().queryForMap(sql);
				Long summary = (Long) count.get("summary");
				return summary.intValue();
			}
		}

		return 0;
	}

	private static void getChildNode(ModelClassViewTreeNode pNode, List<ModelClassViewTreeNode> treeNodeList,
			Map<String, ModelClassViewTreeNode> treeNodeMap, List<ModelClassViewTreeNode> treeNodes, USCObject object,
			String tableName, String userName)
	{
		String pid = pNode.getId();
		treeNodeList.forEach(node -> {
			if (pid.equals(node.getPid()))
			{
				List<ModelClassViewTreeNode> cNodeList = getNodeData(pNode, node, treeNodes, object, tableName,
						userName);
				if (!ObjectHelperUtils.isEmpty(cNodeList))
				{
					cNodeList.forEach(cpNode -> {
						getChildNode(cpNode, treeNodeList, treeNodeMap, treeNodes, object, tableName, userName);
					});

				}
			}
		});
	}

	private static List<ModelClassViewTreeNode> getNodeData(ModelClassViewTreeNode pNode, ModelClassViewTreeNode node,
			List<ModelClassViewTreeNode> treeNodes, USCObject object, String tableName, String userName)
	{
		if (node == null || node.getDatacondition() == null)
		{
			return null;
		}

		List<ModelClassViewTreeNode> cTreeNodes = new ArrayList<ModelClassViewTreeNode>();
		String pNodeID = pNode.getTreenodeid();
//		String pDataCondition = pNode.getDatacondition().replace(CLASSVIEWTREENODEVALUE,
//				PARENT + CLASSVIEWTREENODEVALUE);
		String cDataCondition = node.getDatacondition();
//		String treeNodeDataCondition = node.getDatacondition();

		Integer loadDataSet = node.getLoadDataSet();
		if (loadDataSet != null && loadDataSet != 0)
		{
			String pDataCondition = pNode.getDatacondition().replace(CLASSVIEWTREENODEVALUE,
					PARENT + CLASSVIEWTREENODEVALUE);
			cDataCondition = getOrderByCondition(pDataCondition, cDataCondition);
		}
		String treeNodeDataCondition = cDataCondition;

		String cNodeDataSql = node.getNodecondition();
		if (ObjectHelperUtils.isEmpty(cNodeDataSql))
		{
			ModelClassViewTreeNode treeNodeData = node.clone();
			treeNodeData.setTreenodepid(pNodeID);
			treeNodeData.setDatacondition(treeNodeDataCondition);
			treeNodeData.setNodeDataTotal(setDataTotal(treeNodeData, tableName, object, userName));
			String treeNodeId = pNodeID + split + node.getName();

			treeNodeData.setTreenodeid(treeNodeId);
			treeNodeData.setTreenodedata(node.getName());
			treeNodes.add(treeNodeData);
			cTreeNodes.add(treeNodeData);
			return cTreeNodes;
		}
		if (cNodeDataSql.contains(CLASSVIEWTREENODEVALUE + sign))
		{
			cNodeDataSql = replaceCNodeDataSql(cNodeDataSql, pNodeID);
		}
		if (object != null)
		{
			cNodeDataSql = USCObjExpHelper.parseObjValueInExpression(object, cNodeDataSql);
		}
		List<Map<String, Object>> nodeMaps = DBUtil.queryForList(cNodeDataSql);
		if (nodeMaps == null)
		{
			return null;
		}

		nodeMaps.forEach(nodeData -> {
			nodeData.forEach((k, v) -> {
				String ndata = String.valueOf(v).replace("null", "");
				if (!ndata.equals(""))
				{
					ModelClassViewTreeNode treeNodeData = node.clone();
					treeNodeData.setTreenodepid(pNodeID);
					treeNodeData.setDatacondition(treeNodeDataCondition);
					treeNodeData.setNodeDataTotal(setDataTotal(treeNodeData, tableName, object, userName));
					String treeNodeId = pNodeID + split + v;
					treeNodeData.setTreenodeid(treeNodeId);
					treeNodeData.setTreenodedata(ndata);
					treeNodes.add(treeNodeData);
					cTreeNodes.add(treeNodeData);
				}

			});
		});
		return cTreeNodes;

	}

	private static String replaceCNodeDataSql(String cNodeDataSql, String pNodeID)
	{
		int i = 1;
		while (cNodeDataSql.contains(CLASSVIEWTREENODEVALUE + sign))
		{
			if (i == 20)
			{
				return cNodeDataSql;
			}
			int index = cNodeDataSql.indexOf(CLASSVIEWTREENODEVALUE + sign);
			int n = index + (CLASSVIEWTREENODEVALUE + sign).length();

			while (index > 6)
			{
				String b = cNodeDataSql.substring(index - 2, index - 1);
				if (b.equals(sign))
				{
					String nodeValue = cNodeDataSql.substring(index - 2, n);
					String str = replaceParentNodeData(nodeValue, pNodeID);
					cNodeDataSql = cNodeDataSql.replace(nodeValue, str);
					break;
				}
				index--;

			}
			i++;
		}
		return cNodeDataSql;
	}

	private static String replaceParentNodeData(String nodeValue, String pNodeID)
	{
		int n = 0;
		while (nodeValue.contains(PARENT))
		{
			n++;
			nodeValue = nodeValue.substring(nodeValue.indexOf(PARENT) + 7);
		}
		String rpnd = replaceParentNodeData(n, pNodeID);
		return rpnd;
	}

	private static String replaceParentNodeData(int n, String pNodeID)
	{
		String[] pNodeIDs = pNodeID.split(split);
		int len = pNodeIDs.length;
		if (n > len || n < 1)
		{
			return "null";
		}
		return pNodeIDs[len - n];
	}

	private static String getOrderByCondition(String pTreeNodeDataCondition, String cTreeNodeDataCondition)
	{
		String pSql = pTreeNodeDataCondition.toLowerCase();
		if (!pSql.contains(ORDER))
		{
			return pTreeNodeDataCondition + " AND " + cTreeNodeDataCondition;
		}

		int podIndex = pSql.indexOf(BY) + 2;
		String pOrderfs = pTreeNodeDataCondition.substring(podIndex, pTreeNodeDataCondition.length()).trim();
		String order = " ORDER BY ";
		if (!cTreeNodeDataCondition.toLowerCase().contains(ORDER))
		{
			return pTreeNodeDataCondition.substring(0, pSql.indexOf(ORDER)) + " AND " + cTreeNodeDataCondition + order
					+ pOrderfs;
		}
		String sql = cTreeNodeDataCondition.toLowerCase();
		int odIndex = sql.indexOf(BY) + 2;
		String orderfs = cTreeNodeDataCondition.substring(odIndex, cTreeNodeDataCondition.length()).trim();
		String cond = pTreeNodeDataCondition.substring(0, pSql.indexOf(ORDER)) + " AND "
				+ (cTreeNodeDataCondition.substring(0, sql.indexOf(ORDER))) + order + orderfs + "," + pOrderfs;
		return cond;

	}

}
