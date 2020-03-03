package com.usc.app.action.demo.zc;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.query.rt.QueryReturnRequest;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;

import java.util.*;

/**
 * @Author: lwp
 * @DATE: 2020/2/26 15:18
 * @Description: 根据用户获取报警
 **/
public class GetAlertByUser extends AbstractAction implements QueryReturnRequest {
    @Override
    public Object executeAction() throws Exception {
        String condition = (String) context.getExtendInfo("condition");
        USCObject[] objects = USCObjectQueryHelper.getObjectsByCondition("EARLYWARNING", condition);
        List<Map<String, Object>> list = new ArrayList<>();
        if (objects != null && objects.length > 0) {
            for (USCObject object : objects) {
                Map<String, Object> map = new HashMap<>();
                map.put("NO", object.getFieldValue("NO"));
                map.put("NAME", object.getFieldValue("NAME"));
                map.put("TYPE", object.getFieldValue("TYPE"));
                map.put("STATE", object.getFieldValue("STATE"));
                map.put("SEVERITY", object.getFieldValue("SEVERITY"));
                map.put("INFLUENCES", object.getFieldValue("INFLUENCES"));
                map.put("CONTINUEDTIME", object.getFieldValue("CONTINUEDTIME"));
                map.put("MEASURES", object.getFieldValue("MEASURES"));
                map.put("DWSTATE", object.getFieldValue("DWSTATE"));
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public boolean disable() throws Exception {
        return false;
    }
}
