/**
 * @Author lwp
 */
package com.usc.app.action.editor;
import com.usc.app.action.a.AbstractAction;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.server.DBConnecter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: lwp
 * @DATE: 2019/8/13 09:05
 * @Description:
 **/

public class SelectUserEditor extends AbstractAction
{
    @Override
    public Object executeAction() throws Exception {

        String sdepartmentSql = "SELECT * FROM SDEPARTMENT WHERE DEL = 0 AND ITEMNO = 'SPERSONNEL'";
        String condition = (String) context.getExtendInfo("condition")!=null && !((String) context.getExtendInfo("condition")).equals("") ?
                " AND " +(String) context.getExtendInfo("condition")+" OR PID = '0'":"";
        List<Map<String, Object>> sdepartmentList = new JdbcTemplate(DBConnecter.getDataSource()).queryForList(sdepartmentSql+condition);
        List resultList = new ArrayList<>(sdepartmentList);
        for (Map<String, Object> sdepartment : sdepartmentList) {
            String userSql = "SELECT SUSER.ID AS ID, CONCAT(SUSER.NAME,CONCAT(CONCAT('<',SPERSONNEL.NAME),'>')) AS NAME,SUSER.PASSWORD FROM " +
                    "SDEPARTMENT,SPERSONNEL,CRL_SPERSONNEL_OBJ,SUSER,SUSER_RELOBJ " +
                    "WHERE SUSER.DEL = 0 " +
                    "AND SDEPARTMENT.ID = ? " +
                    "AND CRL_SPERSONNEL_OBJ.NODEID = ? "+
                    "AND SPERSONNEL.ID = CRL_SPERSONNEL_OBJ.ITEMID " +
                    "AND SPERSONNEL.ID = SUSER_RELOBJ.ITEMAID " +
                    "AND SUSER.ID = SUSER_RELOBJ.ITEMBID";
            List<Map<String, Object>> userList = new JdbcTemplate(DBConnecter.getDataSource()).
                    queryForList(userSql, new Object[]{sdepartment.get("ID"),sdepartment.get("ID")});
            //给用户赋值pid方便前台组装成树结构
            for (Map<String, Object> user:  userList){
                user.put("PID",sdepartment.get("ID"));
            }
            resultList.addAll(userList);
        }

        return StandardResultTranslate.getQueryResult(true,"Action_Query",resultList);
    }

    @Override
    public boolean disable() throws Exception {
        return true;
    }

}
