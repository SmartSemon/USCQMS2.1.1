package com.usc.app.ims.config.action.group;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.ims.config.Endpoint;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: lwp
 * @DATE: 2019/11/18 11:11
 * @Description: 获取群列表(自己创建的群和加入的群)
 **/
public class GetGroups extends AbstractAction {

    @Override
    public Object executeAction() throws Exception {
        String userId = context.getUserInformation().getUserID();
        Map<String, Object> result = new HashMap<>();
        Set<Map<String, Object>> groupsList = new HashSet<>();
        if (userId != null) {
            //获取自己创建的群
            Map<String, Object> param = new HashMap<>();
            String groupCondition = "DEL = 0 AND " + "CREATE_BY = " + "'" + userId + "'";
            USCObject[] groupList = USCObjectQueryHelper.getObjectsByCondition("CHAT_GROUP", groupCondition);
            Map<String, Object> group;
            if (groupList != null) {
                for (USCObject i : groupList) {
                    group = new HashMap<>();
                    group.put("id", i.getID());
                    group.put("groupName", i.getFieldValue("GROUP_NAME"));
                    group.put("remarks", i.getFieldValue("REMARK"));
                    group.put("photo", "/api/src/user/getAvatar/" + i.getFieldValue("CREATE_BY"));
                    group.put("avatar", "/api/src/chatGroup/getAvatar/" + i.getID());
                    //获取创建者的姓名
                    String userCondition = "DEL = 0 AND " + "ID = " + "'" + i.getFieldValue("CREATE_BY") + "'";
                    USCObject[] userList = USCObjectQueryHelper.getObjectsByCondition("SUSER", userCondition);
                    group.put("createByUser", userList[0].getFieldValue("SNAME"));
                    groupsList.add(group);
                }
            }
            //获取加入的群
            String groupUserCondition = "DEL = 0 AND " + "USERID = " + "'" + userId + "'";
            USCObject[] joins = USCObjectQueryHelper.getObjectsByCondition("CHAT_GROUP_USER", groupUserCondition);
            if (joins != null) {
                for (int i = 0; i < joins.length; i++) {
                    //根据关联表中的群组id获取群信息
                    String grop = "DEL = 0 AND " + "ID = " + "'" + joins[i].getFieldValue("GROUPID") + "'";
                    USCObject[] g = USCObjectQueryHelper.getObjectsByCondition("CHAT_GROUP", grop);
                    Map<String, Object> oup = new HashMap<>();
                    oup.put("id", g[0].getID());
                    oup.put("groupName", g[0].getFieldValue("GROUP_NAME"));
                    oup.put("remarks", g[0].getFieldValue("REMARK"));
                    oup.put("photo", "/api/src/user/getAvatar/" + g[0].getFieldValue("CREATE_BY"));
                    oup.put("avatar", "/api/src/chatGroup/getAvatar/" + g[0].getID());

                    //获取创建者的姓名
                    String userCondition = "DEL = 0 AND " + "ID = " + "'" + g[0].getFieldValue("CREATE_BY") + "'";
                    USCObject[] userList = USCObjectQueryHelper.getObjectsByCondition("SUSER", userCondition);
                    oup.put("createByUser", userList[0].getFieldValue("SNAME"));
                    groupsList.add(oup);
                }
            }
            result.put("list", groupsList);
        }
        return result;
    }

    @Override
    public boolean disable() throws Exception {
        return false;
    }
}
