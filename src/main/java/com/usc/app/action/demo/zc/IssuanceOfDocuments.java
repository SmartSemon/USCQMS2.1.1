package com.usc.app.action.demo.zc;

import com.usc.app.action.mate.MateFactory;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.app.wxdd.WxDdMessageService;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.type.GeneralObject;
import com.usc.obj.util.USCObjExpHelper;
import com.usc.obj.util.USCObjectQueryHelper;
import com.usc.util.SpringContextUtil;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lwp
 * @DATE: 2020/3/2 17:34
 * @Description: 文件下发并通知接收人
 **/
public class IssuanceOfDocuments implements ExecutionListener {
    private static final long serialVersionUID = 7434334549336067081L;

    @Override
    public void notify(DelegateExecution execution) throws Exception {

        String conditions = "DSNO = " + "'" + execution.getProcessInstanceId() + "'";
        //质量文件管理数据
        USCObject[] objects = USCObjectQueryHelper.getObjectsByCondition("QCDOCUMENTMANAGEMENT", conditions);
        //根据关联关系查询质量文件台账数据，并通知到台账接收人
        String relItem = MateFactory.getItemInfo("REL_QCDOCUMENTMANAGEMENT_OBJ").getTableName();
        for (USCObject object : objects) {
            USCObject[] accounts = USCObjectQueryHelper.getRelationObjectsByConditionLimit(relItem, "QCDOCUMENTMANAGEMENT",
                    "QCDOCUMENTACCOUNT", object.getID(), 1);
            if (accounts != null && accounts.length > 0) {
                for (USCObject account : accounts) {
                    WxDdMessageService wxddService = SpringContextUtil.getBean(WxDdMessageService.class);
                    wxddService.sendTextMessage("质量文件提醒:\r\n\t内容：您有新的质量文件需要查看！", account.getFieldValueToString("LOWERRUNNER"), null, null);
                    USCObject uscObject = new GeneralObject("NOTICE", null);
                    ApplicationContext context = new ApplicationContext(account.getFieldValueToString("CUSER"), uscObject);
                    SendMessageUtils.sendToUser(EndpointEnum.RefreshUnread, context, null, "notice",
                            "质量文件提醒", "您有新的文件需要查看,文件编号为：" + object.getFieldValueToString("NO"),
                            account.getFieldValueToString("CUSER"), account.getFieldValueToString("LOWERRUNNER"));
                }
            }

        }
    }
}
