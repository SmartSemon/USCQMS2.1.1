package com.usc.app.ims.config.action.group;

import com.usc.app.action.a.AbstractAction;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lwp
 * @DATE: 2019/11/21 14:56
 * @Description:
 **/
public class SearchByUserName extends AbstractAction {
    @Override
    public Object executeAction() throws Exception {

        Map<String, Object> result = new HashMap<>();
        List usersList = new ArrayList();
        if (context.getExtendInfo("userName") != null) {
            String condition = "DEL = 0 AND " + "NAME Like " + "'%" + context.getExtendInfo("name") + "%'";
            USCObject[] spersonnel = USCObjectQueryHelper.getObjectsByCondition("SPERSONNEL", condition);
            if (spersonnel != null) {
                Map<String, Object> u;
                for (USCObject sper : spersonnel) {
                    String suser_reobj_condition = "DEL = 0 AND " + "ITEMAID = " + "'" + sper.getID() + "'";
                    USCObject[] suser_reobj = USCObjectQueryHelper.getObjectsByCondition("SR_SUSER", suser_reobj_condition);
                    if (suser_reobj != null) {
                        u = new HashMap<>();
                        for (USCObject sr : suser_reobj) {
                            USCObject user = USCObjectQueryHelper.getObjectByID("SUSER", (String) sr.getFieldValue("ITEMBID"));
                            u.put("id", user.getID());
                            u.put("name", user.getFieldValue("SNAME") + "<" + sper.getFieldValue("SUNAME") + ">");
                            u.put("sign", user.getFieldValue("SIGN"));
                            u.put("remark", user.getFieldValue("REMARK"));
                            usersList.add(u);
                        }
                    }
                }
                result.put("code",0);
                result.put("list", usersList);
            } else {
                result.put("code",0);
                result.put("list", usersList);
            }
        } else {
            result.put("code",0);
            result.put("list", usersList);
        }
        return result;
    }

    @Override
    public boolean disable() throws Exception {
        return false;
    }
}
