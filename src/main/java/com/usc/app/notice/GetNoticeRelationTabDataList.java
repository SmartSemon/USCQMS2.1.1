package com.usc.app.notice;

import com.usc.app.action.a.AbstractAction;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;

/**
 * @Author: lwp
 * @DATE: 2019/12/19 14:45
 * @Description:
 **/
public class GetNoticeRelationTabDataList extends AbstractAction {
    @Override
    public Object executeAction() throws Exception {
        String noticeId = (String) context.getExtendInfo("itemId");
        String relItemNo = (String) context.getExtendInfo("relItemNo");

        //获取中间表关联对象的建模数据与关联数据
        String relSql = "FK_NOTICE_ID = '" + noticeId + "' AND" + " ITEMNO = '" + relItemNo + "'" + " AND del = 0";
        USCObject[] noticeObjects = USCObjectQueryHelper.getObjectsByCondition("NOTICE_ITEMNO", relSql);
        if (noticeObjects != null) {
            Object[] relationDataList = new Object[noticeObjects.length];
            for (int i = 0; i < noticeObjects.length; i++) {
                USCObject object = USCObjectQueryHelper.getObjectByID(relItemNo, noticeObjects[i].getFieldValueToString("ITEMNO_ID"));
                relationDataList[i] = object;
            }
            return relationDataList;
        }
        return null;

    }

    @Override
    public boolean disable() throws Exception {
        return false;
    }
}
