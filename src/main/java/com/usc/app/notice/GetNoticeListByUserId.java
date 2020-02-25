package com.usc.app.notice;

import com.usc.app.action.a.AbstractAction;
import com.usc.server.DBConnecter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * @Author: lwp
 * @DATE: 2019/12/20 11:43
 * @Description:
 **/
public class GetNoticeListByUserId extends AbstractAction {
    @Override
    public Object executeAction() throws Exception {
        String noticeSql = "SELECT * FROM NOTICE WHERE DEL = 0";
        String condition = (String) context.getExtendInfo("condition") != null && !((String) context.getExtendInfo("condition")).equals("") ?
                (String) context.getExtendInfo("condition") : "";
        List<Map<String, Object>> noticeList = new JdbcTemplate(DBConnecter.getDataSource()).queryForList(noticeSql + condition);
        return noticeList;
    }

    @Override
    public boolean disable() throws Exception {
        return false;
    }
}
