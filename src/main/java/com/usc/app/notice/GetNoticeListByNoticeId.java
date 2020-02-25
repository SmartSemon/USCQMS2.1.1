package com.usc.app.notice;

import java.util.List;
import java.util.Map;

import com.usc.app.util.Utils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.usc.app.action.a.AbstractAction;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;
import com.usc.server.DBConnecter;

/**
 * @Author: lwp
 * @DATE: 2019/12/19 10:28
 * @Description:
 **/
public class GetNoticeListByNoticeId extends AbstractAction {
    @Override
    public Object executeAction() throws Exception {
        List<Map<String, Object>> noticeList = null;
        // 根据id回去notice
        String noticeId = (String) context.getExtendInfo("noticeId");
        if (!Utils.isEmpty(noticeId)) {
            USCObject notice = USCObjectQueryHelper.getObjectByID("NOTICE", noticeId);
            if (notice != null) {
                // 修改notice为已读
                notice.setFieldValue("STATUS", 1);
                notice.save(context);
                String noticeSql = "SELECT * FROM NOTICE WHERE DEL = 0";
                String condition = (String) context.getExtendInfo("condition") != null
                        && !((String) context.getExtendInfo("condition")).equals("")
                        ? (String) context.getExtendInfo("condition")
                        : "";
                noticeList = new JdbcTemplate(DBConnecter.getDataSource()).queryForList(noticeSql + condition);
            }
        } else {
            String noticeSql = "SELECT * FROM NOTICE WHERE DEL = 0";
            String condition = (String) context.getExtendInfo("condition") != null
                    && !((String) context.getExtendInfo("condition")).equals("")
                    ? (String) context.getExtendInfo("condition")
                    : "";
            noticeList = new JdbcTemplate(DBConnecter.getDataSource()).queryForList(noticeSql + condition);
        }


        return noticeList;
    }

    @Override
    public boolean disable() throws Exception {
        return false;
    }
}
