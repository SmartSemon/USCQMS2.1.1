package com.usc.app.action.demo.zc;

import com.usc.app.action.a.AbstractAction;
import com.usc.obj.api.USCObject;

/**
 * @Author: lwp
 * @DATE: 2020/3/4 10:30
 * @Description:
 **/
public class ActStartProcess extends AbstractAction {
    @Override
    public Object executeAction() throws Exception {
        return null;
    }

    @Override
    public boolean disable() throws Exception {
        USCObject[] objects = context.getSelectObjs();
        String user = context.getUserName();
        for (USCObject uscObject : objects)
        {
            String state = (String) uscObject.getFieldValue("STATE");
            String muser = uscObject.getFieldValueToString("CUSER");
            if (!"C".equals(state) || !user.equals(muser))
            {
                return true;
            }
        }
        return false;
    }
}
