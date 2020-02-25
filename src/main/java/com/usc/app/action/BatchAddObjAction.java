package com.usc.app.action;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;

import java.util.*;

public class BatchAddObjAction extends AbstractAction {

    @Override
    public Object executeAction() throws Exception {
        String itemNo = (String) context.getExtendInfo("itemNo");
        String userName = (String) context.getExtendInfo("userName");
        List<Map<String, Object>> selectData = (List<Map<String, Object>>) context.getExtendInfo("hData");
        List<Map<String, Object>> mapFields = (List<Map<String, Object>>) context.getExtendInfo("otherParam");
        Object[] object = new Object[selectData.size()];
        for (int i = 0; i < selectData.size(); i++) {
            Map<String, Object> newData = new HashMap<>();
            for (Map<String, Object> mapField : mapFields) {
                newData.put((String) mapField.get("tfield"), selectData.get(i).get(mapField.get("sfield")));
                newData.put("ITEMNO", itemNo);
                newData.put("STATE", "C");
                newData.put("MUSER", userName);
                newData.put("MTIME", new Date());
                newData.put("DEL", 0);
            }
            context.setFormData(newData);
            object[i] = context.createObj(itemNo);
        }
        return new ActionMessage(flagTrue, RetSignEnum.NEW, StandardResultTranslate.translate("BatchAdd_Success"),
                object);
    }

    @Override
    public boolean disable() throws Exception {
        return true;
    }

}
