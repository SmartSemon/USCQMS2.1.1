package com.usc.app.action.demo.zc;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.USCObject;

/**
 * @Author: lwp
 * @DATE: 2019/12/20 17:11
 * @Description: 确认一搬责任判定
 **/
public class ConfirmationOfGeneralAction extends AbstractAction {
    @Override
    public Object executeAction() throws Exception {
        USCObject[] objects = context.getSelectObjs();
        for (USCObject u : objects) {
            u.setFieldValue("DWSTATE", "D");
            u.save(context);
        }
        return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "接受判定责任成功", objects);
    }

    @Override
    public boolean disable() throws Exception {
        String userName = context.getUserName();
        USCObject[] objects = context.getSelectObjs();
        for (USCObject uscObject : objects) {
            String dwState = (String) uscObject.getFieldValue("DWSTATE");
            String type = (String) uscObject.getFieldValue("TYPE");
            String pjuser = (String) uscObject.getFieldValue("PJUSER");
            //判断汇报所选中数据是否全是B状态（已通知责任单位），有一条不是则返回false
            if (!"B".equals(dwState)||!"A".equals(type)||!userName.equals(pjuser)) {
                return true;
            }
        }
        return false;
    }
}
