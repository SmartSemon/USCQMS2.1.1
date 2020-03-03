package com.usc.app.action.demo.zc;

import com.usc.app.action.mate.MateFactory;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.util.USCObjExpHelper;
import com.usc.obj.util.USCObjectQueryHelper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lwp
 * @DATE: 2020/2/26 10:44
 * @Description: 器具报废修改申请单数据状态为已归档，修改申请单下的器具为报废
 **/
public class ScrapAppliances implements ExecutionListener {

    private static final long serialVersionUID = 3019812586115473555L;

    @Override
    public void notify(DelegateExecution execution) throws Exception {

        String conditions = "DSNO = " + "'" + execution.getProcessInstanceId() + "'";
        //申请单的中的数据集
        USCObject[] objects = USCObjectQueryHelper.getObjectsByCondition("SAREPORT", conditions);
        //申请单数据改变状态为已归档
        Map<String, Object> restore = new HashMap();
        for (USCObject objectMap : objects) {
            restore.put("STATE", "F");
            ApplicationContext applicationContext = new ApplicationContext(null, objectMap);
            objectMap.setObjectFieldValues(restore);
            objectMap.save(applicationContext);
        }
        //获取查询视图规定的查询条件
        String queryViewCondition = MateFactory.getQueryView("QUERY_ORGAN_ACCOUNTS_A").getWcondition();
        //根据查询试图的条件查询关联页报废器具的数据
        for (USCObject object : objects) {
            String condition = USCObjExpHelper.parseObjValueInExpression(object, queryViewCondition);
            USCObject[] organs = USCObjectQueryHelper.getObjectsByConditionLimit("ORGAN_ACCOUNTS", condition, 1);
            if (organs.length > 0) {
                for (USCObject objectMap : organs) {
                    restore.put("ORGAN_STATE", "3");//1在库，2停用，3报废
                    ApplicationContext applicationContext = new ApplicationContext(null, objectMap);
                    objectMap.setObjectFieldValues(restore);
                    objectMap.save(applicationContext);
                }
            }
        }

    }
}
