package com.usc.app.activiti.action;

import com.usc.app.activiti.ActCommonUtil;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.io.Serializable;

/**
 * @Author: lwp
 * @DATE: 2020/2/25 17:12
 * @Description: 流程结束归档
 **/
public class EndArchiving implements ExecutionListener, Serializable {
    private static final long serialVersionUID = 5885288685427671149L;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        ActCommonUtil.restore(execution.getProcessInstanceId(), null,
                "F", execution.getProcessInstanceId(), null);
    }
}
