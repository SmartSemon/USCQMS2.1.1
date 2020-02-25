package com.usc.app.activiti.action;

import com.usc.app.action.a.AbstractAction;
import com.usc.util.JBeanUtils;
import com.usc.util.SpringContextUtil;
import org.activiti.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: lwp
 * @DATE: 2020/1/14 14:20
 * @Description:
 **/
public class StartProcessAction extends AbstractAction {

    @Override
    public Object executeAction() throws Exception {
        System.err.println(SpringContextUtil.getBean(IdentityService.class));
        System.err.println(context.getExtendInfo("processId"));
        System.err.println(context.getExtendInfo("dataPack"));
        return null;
    }

    @Override
    public boolean disable() throws Exception {
        return false;
    }
}
