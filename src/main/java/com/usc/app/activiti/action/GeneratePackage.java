package com.usc.app.activiti.action;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lwp
 * @DATE: 2020/1/9 15:07
 * @Description: 生成数据包
 **/
public class GeneratePackage extends AbstractAction {
    @Override
    public Object executeAction() throws Exception {
        String userName = context.getUserName();
        Map<String, Object> newData = new HashMap<>();
        String[] strNow = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).toString().split("-");
        StringBuilder str = new StringBuilder();
        for (String string : strNow) {
            str.append(string);
        }
        newData.put("NO",str+userName );
        newData.put("NAME", str+userName);
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
