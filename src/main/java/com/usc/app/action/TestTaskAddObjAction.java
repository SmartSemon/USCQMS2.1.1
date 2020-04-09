package com.usc.app.action;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.a.AbstractRelationAction;
import com.usc.app.action.mate.MateFactory;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.server.jdbc.DBUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTaskAddObjAction extends AbstractRelationAction {

    @Override
    public Object executeAction() throws Exception {
        //获取试验计划对象标识
        String itemA = (String) context.getExtendInfo("itemA");
        //获取试验任务对象标识
        String itemNo = (String) context.getExtendInfo("itemNo");
        //获取试验计划选中数据
        // Map<String, Object> itemAData = (Map<String, Object>) context.getExtendInfo("itemAData");
        String userName = (String) context.getExtendInfo("userName");
        //字段映射参数信息
        List<Map<String, Object>> mapFields = (List<Map<String, Object>>) context.getExtendInfo("otherParam");
        //获取选中的实验模板数据
        List<Map<String, Object>> selectData = (List<Map<String, Object>>) context.getExtendInfo("hData");
        //获取选中模板数据ID
        String selectDataId = (String) selectData.get(0).get("ID");
        //实验模板与实验项目中间对象
        String rel = MateFactory.getItemInfo("REL_EXPERIMENT_TEMPLATE_OBJ").getTableName();
        List<Map<String, Object>> dataList = DBUtil.getRelationItemResult(MateFactory.getItemInfo("EXPERIMENT_TEMPLATE").getTableName(), MateFactory.getItemInfo("EXPERIMENT_PROJECT"), rel, selectDataId, 1);

        Object[] object = new Object[dataList.size()];
        ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider
                .getContext("REL_TEST_TASK_OBJ", context.getUserName());
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> newData = new HashMap<>();
            newData.put("TEST_ITEM", dataList.get(i).get("EXPERIMENT_PROJECT_NAME"));//试验项目
            newData.put("TEST_METHOD", dataList.get(i).get("EXPERIMENT_PRIVILEGE"));//试验方法
            newData.put("TEST_CONTENT", dataList.get(i).get("EXPERIMENT__CONTENT"));//试验内容
            newData.put("STANDARD", dataList.get(i).get("EVALUATE_STANDARD"));//试验标准
            newData.put("TEST_EQUIPMENT", dataList.get(i).get("EXPERIMENT_EQUIPMENT"));//试验设备
            newData.put("ITEMNO", itemNo);
            newData.put("STATE", "C");
            newData.put("MUSER", userName);
            newData.put("MTIME", new Date());
            newData.put("DEL", 0);
            context.setFormData(newData);
            USCObject obj = context.createObj(itemNo);
            object[i] = obj;
            //构建关联信息
            Map relationData = GetRelationData.getData(getRoot(), obj);
            applicationContext.setFormData(relationData);
            applicationContext.createObj(applicationContext.getItemNo());
        }
        return new ActionMessage(flagTrue, RetSignEnum.NEW, StandardResultTranslate.translate("BatchAdd_Success"),
                object);
    }

    @Override
    public boolean disable() throws Exception {
        return true;
    }

}
