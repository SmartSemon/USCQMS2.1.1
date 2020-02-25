package com.usc.app.ims.config.action;

import com.usc.app.action.a.AbstractAction;
import com.usc.dto.Dto;
import com.usc.dto.impl.MapDto;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;

import java.util.*;

/**
 * @Author: lwp
 * @DATE: 2019/11/8 15:16
 * @Description: 获取ims聊天记录
 **/
public class ChatHistory extends AbstractAction {

    @Override
    public Object executeAction() throws Exception {

        Map<String, Object> dto = new HashMap<>();
        String id = (String) context.getExtendInfo("id");
        String type = (String) context.getExtendInfo("type");
        List<Dto> chatHistory = new ArrayList<>();

        if ("group".equals(type)) {
            //获取群的消息c
            String condition = "DEL = 0 AND " + "RECEIVERID = " + "'" + id + "'" + " AND " + "TYPES = " + "'" + type + "'";
            USCObject[] groupHistoryList = USCObjectQueryHelper.getObjectsByCondition("CHAT_HISTORY", condition);
            for (int i = 0; i < groupHistoryList.length; i++) {
                if (groupHistoryList[i].getFieldValue("SENDERID").equals(groupHistoryList[i].getFieldValueToString("AVATAR")
                        .substring(groupHistoryList[i].getFieldValueToString("AVATAR").lastIndexOf("/") + 1))) {
                    Dto history = new MapDto();
                    history.put("username", groupHistoryList[i].getFieldValue("SENDER_NAME"));
                    history.put("id", groupHistoryList[i].getFieldValue("SENDERID"));
                    history.put("avatar", "/api/src/user/getAvatar/" + groupHistoryList[i].getFieldValue("SENDERID"));
//                    Date date =df.parse( df.format(Utils.intToDateD(lists.get(i).getDouble("createTime"))));
                    Date dateGroup = (Date) groupHistoryList[i].getFieldValue("CTIME");
                    history.put("timestamp", dateGroup.getTime());
                    history.put("content", groupHistoryList[i].getFieldValue("MSG"));
                    chatHistory.add(history);
                }
            }
            dto.put("code", 0);
            dto.put("msg", "获取成功");
            dto.put("data", chatHistory);
            return dto;
        } else {
            String minCondition = "DEL = 0 AND " + "RECEIVERID = " + "'" + id + "'" + " AND " + "TYPES = " + "'" + type + "'";
            //获取我发送的消息
            USCObject[] minHistoryList = USCObjectQueryHelper.getObjectsByCondition("CHAT_HISTORY", minCondition);
            for (USCObject minHistory : minHistoryList) {
                Dto history = new MapDto();
                history.put("username", minHistory.getFieldValue("SENDER_NAME"));
                history.put("id", minHistory.getFieldValue("SENDERID"));
                history.put("avatar", "/api/src/user/getAvatar/" + minHistory.getFieldValue("SENDERID"));
                Date dateMe = (Date) minHistory.getFieldValue("CTIME");
//            Date dateMe =df.parse( df.format(minHistory.getFieldValueToString("ctime")));
                history.put("timestamp", dateMe.getTime());
                history.put("content", minHistory.getFieldValue("MSG"));
                chatHistory.add(history);
            }
            String youCondition = "DEL = 0 AND " + "SENDERID = " + "'" + id + "'" + " AND " + "TYPES = " + "'" + type + "'";
            //获取他的消息
            USCObject[] youHistoryList = USCObjectQueryHelper.getObjectsByCondition("CHAT_HISTORY", youCondition);
            for (USCObject youHistory : youHistoryList) {
                Dto history = new MapDto();
                history.put("username", youHistory.getFieldValue("SENDER_NAME"));
                history.put("id", youHistory.getFieldValue("SENDERID"));
                history.put("avatar", "/api/src/user/getAvatar/" + youHistory.getFieldValue("SENDERID"));
                Date dateYou = (Date) youHistory.getFieldValue("CTIME");
                history.put("timestamp", dateYou.getTime());
                history.put("content", youHistory.getFieldValue("MSG"));
                chatHistory.add(history);
            }
            //根据时间进行排序
            Collections.sort(chatHistory, new Comparator<Dto>() {
                @Override
                public int compare(Dto o1, Dto o2) {
                    if (o1.getDouble("timestamp") > o2.getDouble("timestamp")) {
                        return 1;
                    }
                    if (o1.getDouble("timestamp") > o2.getDouble("timestamp")) {
                        return 0;
                    }
                    return -1;
                }
            });
            dto.put("code", 0);
            dto.put("msg", "获取成功");
            dto.put("data", chatHistory);
            return dto;
        }
    }

    @Override
    public boolean disable() throws Exception {
        return false;
    }
}
