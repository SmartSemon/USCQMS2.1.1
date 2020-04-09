package com.usc.app.activiti.action;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lwp
 * @DATE: 2020/1/6 15:30
 * @Description: 部署流程关联对象
 **/
public class CreateProcessItem extends AbstractAction {
    @Override
    public Object executeAction() throws Exception {
        Map<String,Object> formData = context.getFormData();
        Map<String, Object> selectRow = (Map<String, Object>) context.getExtendInfo("itemAData");
        Map<String, Object> newData = new HashMap<>();
        newData.put("ITEMNO", formData.get("ITEMNO"));
        newData.put("REMARK", formData.get("REMARK"));
        newData.put("PROCDEF_ID", selectRow.get("ID"));
        context.setFormData(newData);
        USCObject object = context.createObj(context.getItemNo());
        if (object == null)
        {
            if (context.getExtendInfo("CreateResult") != null)
            {
                return context.getExtendInfo("CreateResult");
            }
        }
        return new ActionMessage(flagTrue, RetSignEnum.NEW, StandardResultTranslate.translate("Action_Create_1"),
                object);
    }

    @Override
    public boolean disable() throws Exception {
        return false;
    }
}
